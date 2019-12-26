package com.liangyt.sentineltest;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * 受控后处理程序
 * 详情解释:
 * https://github.com/alibaba/Sentinel/wiki/注解支持
 * @author：liangyongtong
 * @date：2019/12/26
 */
@Slf4j
public class ExceptionHandler {
    /**
     * 异常处理方法，参数需要跟 @SentinelResource 所在方法的入参一致，同时在后面可以新加一个 BlockException 参数
     * @param name
     * @param exception
     * @return
     */
    public static String exceptionHandle(String name, BlockException exception) {
        log.debug("name -> {}", name);

        return name + exception.getMessage();
    }

    /**
     * 熔断回调
     * @return
     */
    public static String fallbackHandle(String name) {
        log.debug("啊，我被熔断了");
        return name;
    }
}
