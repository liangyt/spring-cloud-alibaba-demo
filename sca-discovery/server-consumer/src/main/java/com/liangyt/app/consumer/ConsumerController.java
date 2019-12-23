package com.liangyt.app.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述：消费
 * 作者：liangyongtong
 * 日期：2019/10/24 3:34 PM
 */
@Slf4j
@RestController
public class ConsumerController {
    @Autowired
    private ProducerService producerService;

    @GetMapping("/back")
    public String back() {
        return producerService.ok();
    }
}
