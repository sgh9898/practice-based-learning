package excluded.kafka;

import com.demo.util.ResultUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Kafka
 *
 * @author Song gh on 2022/4/27.
 */
@RestController
@RequestMapping("/kafka")
@ApiModel(value = "Kafka Controller", description = "Kafka 相关")
public class KafkaController {

    @Resource
    private KafkaService kafkaService;

    @ApiOperation("发送消息(默认异步)")
    @PostMapping("/send")
    public Map<String, Object> sendMsg(String msg, Boolean async) {
        if (Boolean.FALSE == async) {
            return ResultUtil.success(kafkaService.synchronousSend(msg));
        } else {
            kafkaService.asynchronousSend(msg);
            return ResultUtil.success();
        }
    }
}
