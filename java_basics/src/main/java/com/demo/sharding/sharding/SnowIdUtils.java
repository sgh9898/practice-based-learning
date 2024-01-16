package com.demo.sharding.sharding;


import com.demo.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 雪花ID生成
 * <br> 集群环境下通过 Redis 去重
 *
 * @author Song gh on 2024/1/12.
 */
@Component
public class SnowIdUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private SnowIdUtils() {
    }

    /** 获取雪花id */
    public static long uniqueLong() {
        return SnowFlake.SNOW_FLAKE.nextId();
    }

    /** [集群环境] 允许静态类借助 redis 初始化 */
    @PostConstruct
    private void redisInit() {
        SnowFlake.SNOW_FLAKE.clusterRedisInit(stringRedisTemplate);
    }

    /** 私有的 静态内部类 */
    @Slf4j
    private static class SnowFlake {

        /** 内部类对象(单例模式) */
        private static final SnowFlake SNOW_FLAKE = new SnowFlake();

        /** 机房id长度, 二进制位数, 默认 5 */
        private static final long DATACENTER_ID_BITS = 5L;
        /** 机房id最大值, 默认 31 */
        private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

        /** 机器id长度, 二进制位数, 默认 5 */
        private static final long WORKER_ID_BITS = 5L;
        /** 机器id 最大值, 默认31 */
        private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

        /** 1毫秒内生成的 序列id长度, 二进制位数, 默认 12 */
        private static final long SEQUENCE_BITS = 12L;
        /** 序列id最大值, 默认 4095 */
        private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

        /** 机器id左移 = 序列id长度 */
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
        /** 机房id = 序列id长度 + 机器id左移 */
        private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        /** 时间戳左移 = 序列id长度 + 机器id左移 + 机房id */
        private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
        /** 时钟回拨最大容忍值(毫秒), 低于容忍值时进行短暂等待 */
        private static final long MAX_BACKWARD_MS = 5L;
        /** 时钟回拨低于容忍值时, 最大等待次数 */
        private static final int MAX_BACKWARD_RETRY_TIMES = 1;
        /** 时间初始值, 2010-01-01, 设置更新的时间戳可以使用更长时间 */
        private static final long TW_EPOCH = 1262275201000L;

        /** [redisKey 前缀] 用于机房id, 机器id去重 */
        private static final String REDIS_PREFIX_UNIQUE_ID = "SNOWFLAKE_UTILS_UNIQUE_ID:";
        /** [redisKey 有效期] */
        private static final Duration SNOWFLAKE_REDIS_KEY_DURATION = Duration.ofHours(1);

        /** 机房id */
        private long datacenterId;
        /** 机器id */
        private long workerId;
        /** 记录1毫秒内生成id最新序号 */
        private long sequence;
        /** 记录产生时间毫秒数, 判断是否是同1毫秒 */
        private long lastTimestamp = -1L;

        protected SnowFlake() {
            // 通过当前物理网卡地址获取 datacenterId
            this.datacenterId = getDatacenterId();
            // 物理网卡地址 + jvm pid 获取 workerId
            this.workerId = getMaxWorkerId(datacenterId);
        }

        /** 生成雪花id */
        public synchronized long nextId() {
            long timestamp = timeGen();
            // 处理时钟回拨问题
            if (timestamp < lastTimestamp) {
                long offset = lastTimestamp - timestamp;

                // 少量回拨, 短暂等待
                int retryTimes = 0;
                while (offset > 0 && offset <= MAX_BACKWARD_MS && retryTimes < MAX_BACKWARD_RETRY_TIMES) {
                    try {
                        wait(offset << 1);
                        timestamp = timeGen();
                        offset = lastTimestamp - timestamp;
                        retryTimes++;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new BaseException("时钟回拨, 生成雪花id失败", e);
                    }
                }

                // 等待后仍然存在回拨, 报错
                if (offset > 0) {
                    throw new BaseException(String.format("时钟回拨, %d 毫秒内无法生成雪花id", offset));
                }
            }

            // 控制1毫秒内数据量, 超过最大值时必须等待至下一毫秒
            if (lastTimestamp == timestamp) {
                sequence = sequence + 1L & SEQUENCE_MASK;
                if (sequence == 0L) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = ThreadLocalRandom.current().nextLong(1L, 3L);
            }

            lastTimestamp = timestamp;
            return timestamp - TW_EPOCH << TIMESTAMP_LEFT_SHIFT
                    | datacenterId << DATA_CENTER_ID_SHIFT
                    | workerId << WORKER_ID_SHIFT
                    | sequence;
        }

        /** [集群环境] 借助 redis 初始化, 项目未配置 redis 则不生效  */
        protected void clusterRedisInit(StringRedisTemplate stringRedisTemplate) {
            if (stringRedisTemplate == null) {
                return;
            }

            // 从机器id开始查重, 不允许机房id与机器id同时重复
            for (int i = 0; i <= MAX_DATACENTER_ID; i++) {
                boolean duplicated = true;
                for (int j = 0; j <= MAX_WORKER_ID; j++) {
                    String redisKeyUniqueId = REDIS_PREFIX_UNIQUE_ID + this.datacenterId + this.workerId;
                    String uniqueId = stringRedisTemplate.opsForValue().get(redisKeyUniqueId);

                    if (StringUtils.isNotBlank(uniqueId)) {
                        // id重复, 更换机器id
                        this.workerId = (this.workerId + 1) % (MAX_WORKER_ID + 1);
                    } else {
                        // id未重复, 通过
                        stringRedisTemplate.opsForValue().set(redisKeyUniqueId, "1", SNOWFLAKE_REDIS_KEY_DURATION);
                        duplicated = false;
                        break;
                    }
                }
                if (!duplicated) {
                    break;
                } else {
                    this.datacenterId = (this.datacenterId + 1) % (MAX_DATACENTER_ID + 1);
                }
            }
        }

        /** 等待至下一毫秒 */
        protected long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();

            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        /** 获取当前时间 */
        protected long timeGen() {
            return SystemClock.now();
        }

        /**
         * 获取机器id
         *
         * @param datacenterId 机房id
         */
        private static long getMaxWorkerId(long datacenterId) {
            StringBuilder pid = new StringBuilder();
            pid.append(datacenterId);
            // 获取 jvm 进程信息
            String name = ManagementFactory.getRuntimeMXBean().getName();
            if (StringUtils.isNotBlank(name)) {
                // 获取进程PID
                pid.append(name.split("@")[0]);
            }

            // MAC + PID 的 hashcode 获取16个低位
            return (pid.toString().hashCode() & '\uffff') % (MAX_WORKER_ID + 1L);
        }

        /** 通过当前物理网卡地址获取机房id */
        private static long getDatacenterId() {
            long id = 0L;
            try {
                // 获取本机(或者服务器ip地址)
                InetAddress ip = InetAddress.getLocalHost();
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                if (network == null) {
                    id = 1L;
                } else {
                    // 获取物理网卡地址
                    byte[] mac = network.getHardwareAddress();
                    if (null != mac) {
                        id = (255L & mac[mac.length - 2] | 65280L & (long) mac[mac.length - 1] << 8) >> 6;
                        id %= SnowFlake.MAX_DATACENTER_ID + 1L;
                    }
                }
            } catch (Exception e) {
                log.error("生成机房id失败: " + e.getMessage());
            }
            return id;
        }
    }

//    /** 测试20万次生成 */
//    public static void main(String[] args) throws InterruptedException {
//        // 计时开始时间
//        long start = System.currentTimeMillis();
//        // 让100个线程同时进行
//        final CountDownLatch latch = new CountDownLatch(100);
//        // 判断生成的20万条记录是否有重复
//        final Map<Long, Integer> map = new ConcurrentHashMap<>();
//        for (int i = 0; i < 100; i++) {
//            // 创建 100 个线程
//            new Thread(() -> {
//                for (int s = 0; s < 2000; s++) {
//                    long snowID = SnowIdUtils.uniqueLong();
//                    System.out.println("生成雪花ID = " + snowID);
//                    Integer put = map.put(snowID, 1);
//                    if (put != null) {
//                        throw new RuntimeException("主键重复");
//                    }
//                }
//                latch.countDown();
//            }).start();
//        }
//        // 让上面100个线程执行结束后，在走下面输出信息
//        latch.await();
//        System.out.println("生成20万条雪花ID总用时 = " + (System.currentTimeMillis() - start) + "ms"); // 1066ms
//    }
}

