package com.liangyt.app.producer;

import com.liangyt.app.config.CommonDbConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：服务提供者
 * 作者：liangyongtong
 * 日期：2019/10/24 2:30 PM
 */
@Slf4j
@RestController
@RefreshScope // 支持配置刷新
public class ProducerController {

    @Value("${nacos.config.producer:我是producer本地默认配置}")
    private String producer;

    @Autowired
    private CommonDbConfig dbConfig;

    /**
     * 对外提供服务
     * @return
     */
    @GetMapping("/api/ok")
    public String producer() {
        log.debug(this.producer + ":" + dbConfig.getUsername() + ":" + dbConfig.getPassword());
        return this.producer + ":" + dbConfig.getUsername() + ":" + dbConfig.getPassword();
    }
}
