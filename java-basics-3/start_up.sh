#!/bin/bash
# 常用 jar 包部署命令 -- Sgh 2024

# 使用说明: sh start_up.sh + 命令参数(省略命令参数时为[启动]或[重启])

# 命令参数:
# 1) start   启动(可省略)
# 2) stop    停止
# 3) restart 重启(可省略, 停止和启动 10s 间隔, 如间隔不足以关闭应用, 需要手动 stop/start)
# 4) status  应用状态
# 5) start_dev/start_prod    启动, 且指定配置文件

#------------------------- 可变参数 -------------------------

# 启动参数: 
# 1. Xms 启动时占用内存
# 2. Xmx 占用内存最大值(通常与 Xms 一致)
# 3. Xss 每个线程堆栈大小
JAVA_OPTS="-server -Xms1g -Xmx1g -Xss512k"

# jar 包, JAR_NAME 为空时可自动获取当前目录下唯一[.jar]文件
JAR_NAME="" # 例: sample-0.0.1.jar

# 当前激活的配置文件, PROP_NAME 为空时可自动获取当前目录下唯一[.yaml]文件
PROP_PREFIX="--spring.profiles.active=" # 配置文件前缀, 不要改动
PROP_NAME="" # 激活的配置文件名, 例: dev

#------------------------- 工具类 -------------------------

# 检查进程是否存在
is_exist() {
    auto_get_jar
    pid=$(pgrep -f "${JAR_NAME}")
    if [ -z "${pid}" ]; then
        return 1
    else
        return 0
    fi
}

# 自动获取当前目录下唯一[.jar]文件名称, 仅未设置 ${JAR_NAME} 时生效
auto_get_jar() {
    if [ "${JAR_NAME}" == "" ]; then
        i=0
        for str in `ls *.jar`
        do
            str_list[$i]=$str
            ((i++))
        done
        if [ ${#str_list[@]} -eq 1 ]; then
            JAR_NAME="${str_list[0]}"
        else
            echo "当前目录下有多个[.jar]文件, 请手动设置 JAR_NAME 或清理多余[.jar]文件."
            exit
        fi
    fi
}

# 自动获取当前目录下唯一[.yml]文件名称, 仅未设置 $PROD_NAME 时生效
auto_get_prop() {
    if [ "$PROP_NAME" == "" ]; then
        i=0
        for str in `ls *.yml`
        do
            str_list[$i]=$str
            ((i++))
        done
        if [ ${#str_list[@]} -eq 1 ]; then
            # 文件名取中间部分
            PROP_NAME="${str_list[0]}"
        else
            echo "当前目录下有多个[.yaml]文件, 请手动设置 PROP_NAME 或清理多余[.yaml]文件."
            exit
        fi
    fi
    PROP_NAME="${PROP_NAME#*-}"
    PROP_NAME="${PROP_NAME%.yml}"
    active_prop=$PROP_PREFIX$PROP_NAME
}

#------------------------- 主方法 -------------------------
# 启动
start() {
    is_exist
    if [ $? -eq 0 ]; then
        echo "${JAR_NAME} 已在运行, pid = ${pid}."
    else
        auto_get_prop
        nohup java "${JAVA_OPTS}" -jar ./"${JAR_NAME}" "${active_prop}" > /dev/null 2>&1 &
        is_exist
        if [ $? -eq 0 ]; then
            echo "${JAR_NAME} 启动, pid = ${pid}."
        else
            echo "${JAR_NAME} 启动失败."
        fi
    fi
}

# 启动, 配置文件 dev
start_dev() {
    is_exist
    if [ $? -eq 0 ]; then
        echo "${JAR_NAME} 已在运行, pid = ${pid}."
    else
        auto_get_prop
        nohup java "${JAVA_OPTS}" -jar ./"${JAR_NAME}" ${PROP_PREFIX}dev > /dev/null 2>&1 &
        is_exist
        if [ $? -eq 0 ]; then
            echo "${JAR_NAME} 启动, pid = ${pid}."
        else
            echo "${JAR_NAME} 启动失败."
        fi
    fi
}

# 启动, 配置文件 prod
start_prod() {
    is_exist
    if [ $? -eq 0 ]; then
        echo "${JAR_NAME} 已在运行, pid = ${pid}."
    else
        auto_get_prop
        nohup java "${JAVA_OPTS}" -jar ./"${JAR_NAME}" ${PROP_PREFIX}prod > /dev/null 2>&1 &
        is_exist
        if [ $? -eq 0 ]; then
            echo "${JAR_NAME} 启动, pid = ${pid}."
        else
            echo "${JAR_NAME} 启动失败."
        fi
    fi
}

# 停止
stop() {
    is_exist
    if [ $? -eq 0 ]; then
        kill -9 "${pid}"
        echo "${JAR_NAME} 停止运行."
    else
        echo "${JAR_NAME} 未运行."
    fi
}

# 检查应用状态
status() {
    is_exist
    if [ $? -eq 0 ]; then
        # 输出标题行及内容
        echo
        ps aux|head -1
        echo "------------------------------------------------------------------------"
        ps aux|grep "${JAR_NAME}"
        echo "------------------------------------------------------------------------"
        echo "${JAR_NAME} 正在运行, pid = ${pid}."
        echo
    else
        echo "${JAR_NAME} 未运行."
    fi
}

#------------------------- 命令参数 -------------------------
case $1 in
    # 默认命令为 start/restart
    "")
        is_exist
        if [ $? -eq 0 ]; then
            stop
            sleep 2
        fi
        start;;
    start)
        start;;
    # 重启, 默认间隔 10s
    restart)
        stop
        sleep 10
        start;;

    # 适用不同配置文件频繁切换
    start_dev)
        start_dev;;
    start_prod)
        start_prod;;

    # 中止
    stop)
        stop;;

    # 查看应用状态
    status)
        status;;
    *)
        echo "使用方法: sh $0 {start(可省略)|restart(可省略)|stop|status|start_dev|start_prod}"
esac
