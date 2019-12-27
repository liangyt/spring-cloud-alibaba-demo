package com.liangyt.gateway.config.filter.factory;

import com.liangyt.gateway.config.filter.CustomArgumentGatewayFilter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 描述：把自定义的非全局过滤器加入工厂
 * 作者：liangyongtong
 * 日期：2019/11/27 4:00 PM
 */
@Slf4j
@Component
public class CustomSimpleArgumentGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomSimpleArgumentGatewayFilterFactory.Config> {

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("ips");
    }

    public CustomSimpleArgumentGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config ips) {

        return new CustomArgumentGatewayFilter(Arrays.asList(ips.getIps()));
    }

    @Getter
    @Setter
    public static class Config {
        // 简单定义单属性输入，属性类型可以换为其它基本的类型 int long boolean等
        private String ips;
    }
}
