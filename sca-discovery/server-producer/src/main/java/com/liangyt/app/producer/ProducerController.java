package com.liangyt.app.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：服务提供者
 * 作者：liangyongtong
 * 日期：2019/10/24 2:30 PM
 */
@RestController
public class ProducerController {

    /**
     * 对外提供服务
     * @return
     */
    @GetMapping("/api/ok")
    public String producer() {
        return "服务提供者";
    }
}
