package com.liangyt.sentineltest.howtouse;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：熔断测试
 * 详情说明地址: https://github.com/alibaba/Sentinel/wiki/熔断降级
 * 作者：liangyongtong
 * 日期：2019/12/24
 */
public class DegradeRuleTest {
    public static void main(String[] args) {
        initDegradeRule();

        while (true) {
            Entry entry = null;
            try {
                entry = SphU.entry("hello", EntryType.IN);

                System.out.println("正在处理业务");
                Thread.sleep(100);
            } catch (Throwable t) {
                if (!BlockException.isBlockException(t)) {
                    Tracer.trace(t);
                }
                System.out.println("我被熔断了");
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        }
    }

    private static void initDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("hello");
        // 平均响应时间值 毫秒
        rule.setCount(10);
        // 平均响应时间类型
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        // 时间窗口 秒 在接下来的 10 秒内被直接返回
        rule.setTimeWindow(10);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }
}
