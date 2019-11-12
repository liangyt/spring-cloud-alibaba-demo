package com.liangyt.skywalking.demo;

import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 描述：业务
 * 作者：liangyongtong
 */
@Service
public class BusinessService {

    @Trace
    public Object service(String name) {
        ActiveSpan.info("业务方法");
        return (StringUtils.isEmpty(name) ? "name is null" : name) + TraceContext.traceId();
    }
}
