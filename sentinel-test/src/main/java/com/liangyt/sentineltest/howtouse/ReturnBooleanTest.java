package com.liangyt.sentineltest.howtouse;

import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：返回布尔值方式定义资源
 * https://github.com/alibaba/Sentinel/wiki/如何使用#方式三返回布尔值方式定义资源
 * 作者：liangyongtong
 * 日期：2019/12/24
 */
public class ReturnBooleanTest {
    public static void main(String[] args) {
        initFlowRules();
        while (true) {
            // 资源名可使用任意有业务语义的字符串
            if (SphO.entry("自定义资源名")) {
                // 务必保证finally会被执行
                try {
                    /**
                     * 被保护的业务逻辑
                     */
                    System.out.println("我是正常访问");
                } finally {
                    SphO.exit();
                }
            } else {
                // 资源访问阻止，被限流或被降级
                // 进行相应的处理操作
                System.out.println("我被阻止了");
            }
        }
    }

    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // 设置规则
        FlowRule rule = new FlowRule();
        rule.setResource("自定义资源名");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(10);

        rules.add(rule);
        // 加载规则
        FlowRuleManager.loadRules(rules);
    }
}
