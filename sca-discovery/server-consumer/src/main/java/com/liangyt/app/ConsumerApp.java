package com.liangyt.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 描述：启动
 * 作者：liangyongtong
 * 日期：2019/10/24 3:32 PM
 */
@EnableDiscoveryClient // 开启服务发现客户端
@EnableFeignClients // 开启扫描Spring Cloud Feign客户端的功能
@SpringBootApplication
public class ConsumerApp {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApp.class, args);
    }
}
