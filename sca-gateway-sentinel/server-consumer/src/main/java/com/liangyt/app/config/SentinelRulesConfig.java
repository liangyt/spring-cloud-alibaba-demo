package com.liangyt.app.config;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

/**
 * 自动加载Nacos配置的Sentinel规则
 * @author：liangyongtong
 * @date：2019/12/27
 */
@Slf4j
@Configuration
public class SentinelRulesConfig {

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
    @Value("${dynamic.consumer-sentinel-rule-dataid:nacos-consumer-flow-rules}")
    private String ruleDataId;

    /**
     * 配置从Nacos加载规则
     */
    @PostConstruct
    public void initRules() {
        log.debug("开始初始化 Sentinel 规则 -> serverAddr: {}, namespace: {}, group: {}, dataId: {}", serverAddr, namespace, group, ruleDataId);

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, namespace);

        // 监听规则变化
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(properties, group, ruleDataId,
                    source -> {
                        log.debug("nacos-consumer-flow-rules content -> {}", source);
                        return JSON.parseObject(source, new TypeReference<List<FlowRule>>() {});
                    }
        );
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        log.debug("开始初始化 Sentinel 规则");
    }
}
