package com.liangyt.gateway.po;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.util.List;

/**
 * 描述：Nacos配置的路由信息
 * 作者：liangyongtong
 * 日期：2019/12/20 5:01 PM
 */
@Setter
@Getter
public class RouteDefinitionPO {
    private String id;
    private int order = 0;
    private String uri;
    private List<PredicateDefinition> predicates;
    private List<FilterDefinition> filters;
}
