package com.liangyt.gateway.config.filter.factory;

import com.liangyt.gateway.config.filter.CustomGatewayFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * 描述：把自定义的非全局过滤器加入工厂
 * 作者：liangyongtong
 * 日期：2019/11/27 4:00 PM
 */
@Component
public class CustomGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    @Autowired
    CustomGatewayFilter customGatewayFilter;

    @Override
    public GatewayFilter apply(Object object) {
        return customGatewayFilter;
    }
}
