package com.liangyt.skywalking.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：启动
 * 作者：liangyongtong
 */
@SpringBootApplication
@Slf4j
@RestController
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Autowired
    private BusinessService businessService;

    @Trace
    @GetMapping("/hello/{level}")
    public Object hello(@PathVariable String level, @RequestParam(required = false) String name) {
        if ("debug".equals(level)) {
            ActiveSpan.debug("debug 测试");
        } else if ("info".equals(level)) {
            ActiveSpan.info("我只是一个信息输出");
        } else if ("error".equals(level)) {
            ActiveSpan.error(new Exception("模拟错误"));
            throw new RuntimeException("模拟错误");
        }
        else {
            ActiveSpan.tag("traceTag", "Skywalking Test Tag");
        }
        log.debug("正常日志输出");
        log.info("Skywalking traceId: {}", TraceContext.traceId());
        return businessService.service(name);
    }
}
