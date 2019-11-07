package com.liangyt.readingandwritingseparation.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：数据源
 * 作者：liangyongtong
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.datasource-hikari")
public class CustomHikariDataSource {
    // 主库数据源
    private HikariDataSource master;
    // 从库数据源
    private HikariDataSource slave;

    /**
     * 配置多数据源
     * @return
     */
    @Bean
    @Primary
    DataSource primaryDataSource() {
        Map<Object, Object> map = new HashMap<>();
        map.put("masterDataSource", getMaster());
        map.put("slaveDataSource", getSlave());
        RoutingDataSource routing = new RoutingDataSource();
        routing.setTargetDataSources(map);
        routing.setDefaultTargetDataSource(getMaster());
        return routing;
    }
}
