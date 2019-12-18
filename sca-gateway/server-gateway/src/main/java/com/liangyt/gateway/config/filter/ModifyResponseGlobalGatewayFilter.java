package com.liangyt.gateway.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangyt.gateway.config.CustomCachedBodyOutputMessage;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 描述：记录全局出参
 * 自定义全局 GatewayFilter 只需要实现 GlobalFilter, Ordered 并加入 Spring Ioc 就可以了。
 * GlogbalFilter需要实现 filter 方法， Ordered 需要实现 getOrder 方法
 * filter 方法用于处理请求转发前和转发处理后一些处理 也就是 chain.filter(exchange) 调用前后做的处理; 前置处理按 getOrder 返回值从小到大执行， 后置处理按 getOrder返回值 从大到小执行
 * getOrder 方法返回一个整数，用于判断在有多个全局filter的时候哪个先执行哪个后执行，值越小越先执行;
 * 作者：liangyongtong
 * 日期：2019/11/22 10:25 AM
 */
@SuppressWarnings("all")
@Slf4j
@Component // 加入Spring Ioc 中，这样就可以起作用了
public class ModifyResponseGlobalGatewayFilter implements GlobalFilter, Ordered {
    @Autowired
    private ObjectMapper mapper;

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("path -> {}", exchange.getRequest().getPath());

        // queryParams
        MultiValueMap<String, String> printQueryParams = exchange.getRequest().getQueryParams();
        // headers
        HttpHeaders printheaders = exchange.getRequest().getHeaders();

        // 提交内容类型
        MediaType contentType = printheaders.getContentType();
        log.debug("contentType -> {}", contentType);

        // Content-Type 不是 application/json 和 application/x-www-form-urlencoded 的直接跳过
        if (!(contentType.equals(MediaType.APPLICATION_JSON)
                || contentType.equals(MediaType.APPLICATION_FORM_URLENCODED))) {
            return chain.filter(exchange);
        }
        // cookie
        exchange.getRequest().getCookies().forEach((key, value) -> log.debug("global cookie -> {}:{}", key, value));

        printQueryParams.forEach((key, value) -> log.debug("global query -> {}:{}", key, value));
        printheaders.forEach((key, value) -> log.debug("global header -> {}:{}", key, value));

        // 响应内容的装饰
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {

            /**
             * 修改返回的 body
             * @param body
             * @return
             */
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                String originalResponseContentType = (String)exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Content-Type", originalResponseContentType);

                ClientResponse clientResponse = ClientResponse.create(exchange.getResponse().getStatusCode())
                        .headers((headers) -> {
                            headers.putAll(httpHeaders);
                        })
                        .body(Flux.from(body))
                        .build();

                // 处理响应内容 这是基于返回的内容为 string 类型处理的，如果返回的是其它类型，可以参考
                // org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory
                Mono modifiedBody = clientResponse.bodyToMono(String.class).map((originalBody) -> {
                    log.debug("source response body -> {}", originalBody);

                    return originalBody + 2222;
                });
                BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
                CustomCachedBodyOutputMessage outputMessage = new CustomCachedBodyOutputMessage(exchange, exchange.getResponse().getHeaders());
                return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                    Flux<DataBuffer> messageBody = outputMessage.getBody();
                    HttpHeaders headers = this.getDelegate().getHeaders();

                    // 内容长度修改
                    if (!headers.containsKey("Transfer-Encoding")) {
                        messageBody = messageBody.doOnNext((data) -> {
                            headers.setContentLength((long)data.readableByteCount());
                        });
                    }

                    // 添加头部信息
                    headers.add("ADD-POST-GLOBALFILTER", "add-post-globalfilter");

                    return this.getDelegate().writeWith(messageBody);
                }));
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return super.writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };

        return chain.filter(
                    // 生成一个新的 exchange 实例
                    exchange.mutate()
                    // 个性 response 内容
                    .response(responseDecorator).build()
                );
    }


    public int getOrder() {
        return -8;
    }
}
