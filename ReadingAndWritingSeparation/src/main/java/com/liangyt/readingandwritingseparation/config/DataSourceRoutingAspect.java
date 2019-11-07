package com.liangyt.readingandwritingseparation.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 描述：数据源切换拦截
 * 作者：liangyongtong
 */
@Aspect
@Component
@Order(1)
public class DataSourceRoutingAspect {

    // 拦截数据源切换
    @Around("@annotation(dataSourceRoutingWith)")
    public Object routingWithDataSource(ProceedingJoinPoint joinPoint, DataSourceRoutingWith dataSourceRoutingWith) throws Throwable {
        String key = dataSourceRoutingWith.value();
        try (RoutingDataSourceContext ctx = new RoutingDataSourceContext(key)) {
            return joinPoint.proceed();
        }
    }
}
