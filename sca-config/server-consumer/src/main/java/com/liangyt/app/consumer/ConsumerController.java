package com.liangyt.app.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * 描述：消费
 * 作者：liangyongtong
 * 日期：2019/10/24 3:34 PM
 */
@Slf4j
@RestController
@RefreshScope // 支持配置刷新
public class ConsumerController {
    @Autowired
    private ProducerService producerService;
    @Autowired
    private ObjectMapper mapper;

    @Value("${nacos.config.consumer:我是consumer本地默认配置}")
    private String valueFromNacos;

    @Value("${db.username:默认名}")
    private String userName;

    @Value("${db.password:默认密码}")
    private String password;

    @GetMapping("/back")
    public String back() {
        return producerService.ok() + valueFromNacos + ":" + this.userName + ":" + this.password;
    }

}
