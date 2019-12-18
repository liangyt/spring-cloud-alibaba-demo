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
public class CustomComplexArgumentGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomComplexArgumentGatewayFilterFactory.Config> {

    @Override
    public List<String> shortcutFieldOrder() {
        // 返回属性对应的字符串列表
        return Arrays.asList("ips");
    }

    public CustomComplexArgumentGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config ips) {
        return new CustomArgumentGatewayFilter(ips.getIps());
    }

    @Getter
    @Setter
    public static class Config {
        private List<String> ips;
    }
}
