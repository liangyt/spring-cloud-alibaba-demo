package com.liangyt.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 描述：启动服务
 * 作者：liangyongtong
 * 日期：2019/10/24 2:21 PM
 */
@EnableDiscoveryClient // 开启服务发现与注册功能
@EnableFeignClients // 开启扫描Spring Cloud Feign客户端的功能
@SpringBootApplication(scanBasePackages = {"com.liangyt.app"}, exclude = DataSourceAutoConfiguration.class)
public class ProducerApp {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApp.class, args);
    }
}
