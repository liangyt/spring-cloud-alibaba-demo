package com.liangyt.app.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 描述：获取其它服务
 * 作者：liangyongtong
 * 日期：2019/10/24 5:05 PM
 */
@FeignClient(
        name = "nacos-producer", // 服务名 也可以使用 value = "nacos-producer"
        path = "/api", // 这个值会拼接在下面的各请求的前面  如: /api/ok
        fallback = ProducerService.ProducerServiceFallback.class // 如果调用的方法异常无响应，则调用对应的备用方法; ()
)
public interface ProducerService {

    @RequestMapping("/ok")
    String ok();

    @Component
    class ProducerServiceFallback implements ProducerService {
        @Override
        public String ok() {
            return "我是备用方法";
        }
    }
}
