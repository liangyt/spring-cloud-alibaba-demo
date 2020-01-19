package com.liangyt.gateway.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：响应数据
 * 作者：liangyongtong
 * 日期：2019/11/25 6:57 PM
 */
@Setter
@Getter
@Builder
public class ResponseVO<T> {
    private String code;
    private String message;
    private T data;
}
