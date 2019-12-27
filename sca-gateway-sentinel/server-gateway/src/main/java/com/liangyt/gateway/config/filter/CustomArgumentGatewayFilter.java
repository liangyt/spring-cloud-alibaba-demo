package com.liangyt.gateway.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 描述：自定义过滤器 要使过滤器能使用还需要加入工厂;
 * 带参的过滤器,这里简单实现 ip 过滤器
 * 实现 GatewayFilter 的过滤器跟 实现 GlobalFilter 的过滤器有些区别，区别不大；
 * 实现 GatewayFilter 的过滤器需要指定作用于哪个路由下，顺序跟全局网关过滤器共享，在一条链上，依据 getOrder或@Order 返回值决定
 * 作者：liangyongtong
 * 日期：2019/11/21 5:51 PM
 */
@Slf4j
public class CustomArgumentGatewayFilter implements GatewayFilter, Ordered {

    /** 可以访问的 ip 列表 */
    private List<String> ips;

    public CustomArgumentGatewayFilter(List<String> ips) {
        this.ips = ips;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("limit ips -> {}", this.ips);

        InetSocketAddress address = exchange.getRequest().getRemoteAddress();

        log.debug("hostName -> {}, hostString -> {}, port -> {}", address.getHostName(), address.getHostString(), address.getPort());

        InetAddress inetAddress = address.getAddress();

        // 如果是限制的ip则不让访问了
        if (this.ips.contains(address.getHostName())) {
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -7;
    }
}
