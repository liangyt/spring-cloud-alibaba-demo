package com.liangyt.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：常用工具全局实例化
 * 作者：liangyongtong
 * 日期：2019/11/22 10:38 AM
 */
@Configuration
public class SystemConfig {

    /**
     * 全局注入 ObjectMapper，免去实例化的过程，增强性能
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        return mapper;
    }
}
