package com.liangyt.readingandwritingseparation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.liangyt.readingandwritingseparation.dao")
@EnableTransactionManagement
public class ReadingAndWritingSeparationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadingAndWritingSeparationApplication.class, args);
    }
}
