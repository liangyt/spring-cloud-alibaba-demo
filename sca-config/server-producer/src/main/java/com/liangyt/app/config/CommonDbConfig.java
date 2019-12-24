package com.liangyt.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 描述：数据库配置
 * 作者：liangyongtong
 * 日期：2019/11/21 4:08 PM
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "db")
public class CommonDbConfig {
    private String username;
    private String password;
}
