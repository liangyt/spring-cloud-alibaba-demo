package com.liangyt.gateway.config.filter.factory;

import com.liangyt.gateway.config.filter.CustomArgumentGatewayFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 描述：把自定义的非全局过滤器加入工厂
 * 作者：liangyongtong
 * 日期：2019/11/27 4:00 PM
 */
@Slf4j
@Component
public class CustomKVArgumentGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

    @Override
    public GatewayFilter apply(NameValueConfig ips) {
        log.debug("key -> {}, value -> {}", ips.getName(), ips.getValue());
        return new CustomArgumentGatewayFilter(Arrays.asList(ips.getValue()));
    }

}
