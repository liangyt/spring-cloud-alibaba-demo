package com.liangyt.gateway.config.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangyt.gateway.config.CustomCachedBodyOutputMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 描述：记录全局入参
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
public class ModifyRequestGlobagGatewayFilter implements GlobalFilter, Ordered {
    @Autowired
    private ObjectMapper mapper;

    @Override
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

        // 重写 request body 逻辑
        List<HttpMessageReader<?>> httpMessageReaders = HandlerStrategies.withDefaults().messageReaders();
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();

        ServerRequest serverRequest = ServerRequest.create(exchange, httpMessageReaders);
        // 修改提交的内容处理
        Mono<String> modifyBody = serverRequest.bodyToMono(String.class).map(s -> {

            log.debug("source body -> {}", s);

            String modifyResult = "";

            // application/json  -> {"a": "name", "b": 10}
            if (contentType.equals(MediaType.APPLICATION_JSON)) {
                try {
                    // 这里可能存在多层次的对象
                    Map<String, Object> body = mapper.readValue(s, Map.class);

                    // 这里可以修改 request body 内容
//                    body.put("name", "我是新的Name值");

                    modifyResult = mapper.writeValueAsString(body);

                    log.debug("modifybody json body -> {}", modifyResult);
                } catch (IOException e) {
                    log.error("global filter json -> map error ", e);
                }
            }
            // application/x-www-form-urlencoded  -> a=name&b=10
            else if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
                List<String[]> bodyList = Arrays.stream(s.split("&")).map(kv -> kv.split("=")).collect(Collectors.toList());
                Map<String, String> body = new HashMap<>();
                bodyList.forEach(kv -> body.put(kv[0], kv[1]));

                // 这里可以修改 request body 内容

                try {
                    log.debug("source form body -> {}", mapper.writeValueAsString(body));

//                    body.put("name", "我是新的Name值");

                    modifyResult = StringUtils.join(
                            body.keySet()
                                    .stream()
                                    .map(key -> key + "=" + body.get(key))
                                    .collect(Collectors.toList())
                            , "&");

                    log.debug("modifybody form body -> {}", modifyResult);
                } catch (JsonProcessingException e) {
                    log.error("global filter form -> map error ", e);
                }
            }

            return modifyResult;
        });
        BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifyBody, String.class);
        HttpHeaders cacheHttpHeaders = new HttpHeaders();
        cacheHttpHeaders.putAll(exchange.getRequest().getHeaders());
        // 移除长度
        cacheHttpHeaders.remove(HttpHeaders.CONTENT_LENGTH);

        CustomCachedBodyOutputMessage cachedBodyOutputMessage = new CustomCachedBodyOutputMessage(exchange, cacheHttpHeaders);

        return bodyInserter.insert(cachedBodyOutputMessage, new BodyInserterContext())
            .then(Mono.defer(() -> {

                ServerHttpRequestDecorator requestDecorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders headers = new HttpHeaders();

                        // 修改后内容的长度
                        long contentLength = cacheHttpHeaders.getContentLength();
                        log.debug("contentLength -> {}", contentLength);

                        headers.addAll(super.getHeaders());

                        if (contentLength > 0) {
                            headers.setContentLength(contentLength);
                        } else {
                            headers.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                        }

                        // 添加自定义 header 数据
                        headers.add("ADD-PRE-GLOBALFILTER", "add-pre-globalfilter");

                        return headers;
                    }

                    /**
                     * 这里需要重新返回一个 Flux<DataBuffer>,要不然后面会读取不到 body 内容，报以下异常
                     * reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.IllegalStateException: Only one connection receive subscriber allowed.
                     * @return
                     */
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return cachedBodyOutputMessage.getBody();
                    }

                    /**
                     * 查询 query 参数
                     * @return
                     */
                    @Override
                    public MultiValueMap<String, String> getQueryParams() {
                        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
                        queryParams.putAll(super.getQueryParams());
                        queryParams.add("abc", "abc");
                        queryParams.add("abc", "efg");
                        return queryParams;
                    }
                };

                return chain.filter(
                        // 生成一个新的可修改的 exchange 实例
                        exchange.mutate()
                                // 修改 request 内容
                                .request(requestDecorator)
                                .build()
                );
            }));
    }

    @Override
    public int getOrder() {
        return -10;
    }
}
