package com.liangyt.gateway.config;

import com.liangyt.gateway.config.filter.CustomGatewayFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 描述：gateway 路由配置
 * 作者：liangyongtong
 * 日期：2019/12/17 3:13 PM
 */
@Slf4j
@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("routeid"
                , predicateSpec -> predicateSpec
                    // 内置的route: org.springframework.cloud.gateway.route.builder.PredicateSpec
                    // 在这个类里面内置了gateway已实现的路由 org.springframework.cloud.gateway.handler.predicate 内置的路由 factory
                    .path("/routeLocator")
                    // 添加过滤器
                    .filters(filter ->
                            // 内置的filter: org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
                            // 在这个类里面可以看到内置的过滤器实际上就是 org.springframework.cloud.gateway.filter.factory 包下面的 filterfactory 实现
                            filter.modifyRequestBody(String.class, String.class, (exchange, s) -> {
                                exchange.getRequest().getQueryParams().forEach((key, value) -> log.debug("key:{} -> value:{}", key, value));
                                return Mono.just(s);
                            })
                            // 自定义 filter 当然也可以通过构造函数传参，也可以定义 setter (返回 thisDynamic,\ Trends,\ DHTML) 设置参数
                            .filter(new CustomGatewayFilter())
                            )
                    // 需要访问的服务
                    .uri("lb://nacos-consumer"))
            .build();
    }
}
