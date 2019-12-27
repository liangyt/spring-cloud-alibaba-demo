package com.liangyt.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 网关结合sentinel配置
 * 官网文档: https://github.com/alibaba/Sentinel/wiki/网关限流#spring-cloud-gateway
 * 定义自定义API官网例子: https://github.com/alibaba/Sentinel/blob/master/sentinel-demo/sentinel-demo-spring-cloud-gateway/src/main/java/com/alibaba/csp/sentinel/demo/spring/sc/gateway/GatewayConfiguration.java
 * @author：liangyongtong
 * @date：2019/12/26
 */
@Slf4j
@Configuration
public class GatewaySentinelConfig {

    // 取配置的Nacos地址
    @Value("${spring.cloud.nacos.config.server-addr:localhost:8848}")
    private String serverAddr;
    // 取配置的命名空间
    @Value("${spring.cloud.nacos.config.namespace:c8a2cd88-1c7e-400a-b270-86f689979b8f}")
    private String namespace;
    // 取配置的组
    @Value("${dynamic.consumer-sentinel-rule-group:SENTINEL_GROUP}")
    private String group;
    // 路由存放的文件
    @Value("${dynamic.consumer-sentinel-rule-dataid:server-gateway-flow-rules}")
    private String ruleDataId;

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewaySentinelConfig(ObjectProvider<List<ViewResolver>> viewResolvers, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolvers.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {

        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * 定义API分组，流控的时候可以针对API类型进行控制
     */
    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        ApiDefinition api1 = new ApiDefinition("some_customized_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/product/baz"));
                    add(new ApiPathPredicateItem().setPattern("/product/foo/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        ApiDefinition api2 = new ApiDefinition("another_customized_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/ahas"));
                }});
        definitions.add(api1);
        definitions.add(api2);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    /**
     * 针对定义的API分组进行定义规则
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();

        rules.add(new GatewayFlowRule("some_customized_api")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setCount(5)
                .setIntervalSec(1)
                .setParamItem(new GatewayParamFlowItem()
                    .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                    .setFieldName("pn")
                )
        );
        GatewayRuleManager.loadRules(rules);
    }

    /**
     * 配置从Nacos加载规则
     */
    @PostConstruct
    public void initRules() {
        initDynamicFlowRules();

        initCustomizedApis();
        initGatewayRules();
    }

    /**
     * 动态加载限制规则
     */
    private void initDynamicFlowRules() {
        log.debug("开始初始化 Sentinel 规则 -> serverAddr: {}, namespace: {}, group: {}, dataId: {}", serverAddr, namespace, group, ruleDataId);

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, namespace);

        // 监听规则变化
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(properties, group, ruleDataId,
                source -> {
                    log.debug("server-gateway-flow-rules content -> {}", source);
                    return JSON.parseObject(source, new TypeReference<List<FlowRule>>() {});
                }
        );
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        log.debug("开始初始化 Sentinel 规则");
    }
}
