package com.liangyt.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 描述：启动服务
 * 作者：liangyongtong
 * 日期：2019/10/24 2:21 PM
 */
@EnableDiscoveryClient // 开启服务发现与注册功能
@SpringBootApplication
public class ProducerApp {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApp.class, args);
    }
}
