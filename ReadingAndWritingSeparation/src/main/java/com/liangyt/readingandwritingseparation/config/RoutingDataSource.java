package com.liangyt.readingandwritingseparation.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 描述：根据当前线程确定的key动态返回数据源
 * 作者：liangyongtong
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingDataSourceContext.getDataSourceRoutingKey();
    }
}
