package com.liangyt.sentineltest.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 规则定义
 * 系统初始化对资源的规则定义，这些规则定义如果连上控制台的话也可以同步看到。
 * @author：liangyongtong
 * @date：2019/12/26
 */
@Configuration
public class RulesConfig {

    /**
     * 定义针对资源的规则
     */
    @PostConstruct
    public void initRules() {
        List<FlowRule> rules = new ArrayList<>();

        // 设置规则
        FlowRule rule = new FlowRule();
        rule.setResource("hello");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(2);

        rules.add(rule);
        // 加载规则
        FlowRuleManager.loadRules(rules);
    }
}
