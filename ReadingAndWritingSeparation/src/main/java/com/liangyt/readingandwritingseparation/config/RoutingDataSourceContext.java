package com.liangyt.readingandwritingseparation.config;

/**
 * 描述：数据源切换
 * 作者：liangyongtong
 */
public class RoutingDataSourceContext implements AutoCloseable {
    // 确定当前线程的数据
    static final ThreadLocal<String> threadLocalDataSourceKey = new ThreadLocal<>();

    public static String getDataSourceRoutingKey() {
        String key = threadLocalDataSourceKey.get();
        return key == null ? "masterDataSource" : key;
    }

    public RoutingDataSourceContext(String key) {
        threadLocalDataSourceKey.set(key);
    }

    @Override
    public void close() throws Exception {
        threadLocalDataSourceKey.remove();
    }
}
