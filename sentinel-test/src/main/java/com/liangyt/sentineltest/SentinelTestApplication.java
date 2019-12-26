package com.liangyt.sentineltest;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@SpringBootApplication
public class SentinelTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelTestApplication.class, args);
    }

    /**
     * 若 blockHandler 和 fallback 都进行了配置，则被限流降级而抛出 BlockException 时只会进入 blockHandler 处理逻辑。
     * 若未配置 blockHandler、fallback 和 defaultFallback，则被限流降级时会将 BlockException 直接抛出。
     */

    /**
     * 异常处理方法在本类中
     * @param name
     * @return
     */
    @GetMapping("/hello")
    @SentinelResource(value = "hello", blockHandler = "exceptionHandle", fallback = "fallbackHandle")
    public String hello(String name) {
        return StringUtil.isEmpty(name) ? "空名正常" : name + " -> 正常";
    }

    /**
     * 异常处理方法，参数需要跟 @SentinelResource 所在方法的入参一致，同时在后面可以新加一个 BlockException 参数
     * @param name
     * @param exception
     * @return
     */
    public String exceptionHandle(String name, BlockException exception) {
        log.debug("name -> {}", name);

        return name + exception.getMessage();
    }

    /**
     * 熔断回调
     * @return
     */
    public String fallbackHandle(String name) {
        log.debug("啊，我被熔断了");
        return name;
    }

    /**
     * 异常处理方法不在本类中
     * @param name
     * @return
     */
    @GetMapping("/hi")
    @SentinelResource(value = "hi"
            , blockHandler = "exceptionHandle", blockHandlerClass = {ExceptionHandler.class}
            , fallback = "fallbackHandle", fallbackClass = {ExceptionHandler.class}
            )
    public String hi(String name) {
        return StringUtil.isEmpty(name) ? "空名正常" : name + "正常";
    }
}
