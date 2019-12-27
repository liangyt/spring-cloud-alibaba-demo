package com.liangyt.gateway.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 描述：自定义过滤器 要使过滤器能使用还需要加入工厂;
 * 无参的过滤器 argument
 * 实现 GatewayFilter 的过滤器跟 实现 GlobalFilter 的过滤器有些区别，区别不大；
 * 实现 GatewayFilter 的过滤器需要指定作用于哪个路由下，顺序跟全局网关过滤器共享，在一条链上，依据 getOrder或@Order 返回值决定
 * 作者：liangyongtong
 * 日期：2019/11/21 5:51 PM
 */
@Slf4j
@Component
public class    CustomGatewayFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 打印 query 内容
        exchange.getRequest().getQueryParams().forEach((key, value) -> log.debug("custom query -> {}:{}", key, value));

        // 打印 head 内容，可以看到之前的filter添加的内容
        exchange.getRequest().getHeaders().forEach((key, value) -> log.debug("custom header -> {}:{}", key, value));

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -9;
    }
}
