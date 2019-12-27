package com.liangyt.gateway.config;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * 描述：CachedBodyOutputMessage -> 由于版本原因类变成了保护类了，参考它自定义一个
 * 官方建议使用 com.liangyt.gateway.config.filter.* 来处理请求体和响应体，这里只是为了示例
 * 作者：liangyongtong
 * 日期：2019/12/12 10:35 AM
 */
public class CustomCachedBodyOutputMessage implements ReactiveHttpOutputMessage {
    private final DataBufferFactory bufferFactory;
    private final HttpHeaders httpHeaders;
    private Flux<DataBuffer> body = Flux.error(new IllegalStateException("The body is not set. Did handling complete with success?"));

    public CustomCachedBodyOutputMessage(ServerWebExchange exchange, HttpHeaders httpHeaders) {
        this.bufferFactory = exchange.getResponse().bufferFactory();
        this.httpHeaders = httpHeaders;
    }

    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
    }

    public boolean isCommitted() {
        return false;
    }

    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }

    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    public Flux<DataBuffer> getBody() {
        return this.body;
    }

    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        this.body = Flux.from(body);
        return Mono.empty();
    }

    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return this.writeWith(Flux.from(body).flatMap((p) -> {
            return p;
        }));
    }

    public Mono<Void> setComplete() {
        return this.writeWith(Flux.empty());
    }
}
