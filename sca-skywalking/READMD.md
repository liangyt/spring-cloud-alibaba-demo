#### Nacos 服务发现与注册

#### Gateway 路由网关

#### Skywalking 链路追踪 日志管理

1. 下载包 6.4.0 (也可以下载其它的版本)  
[下载](http://skywalking.apache.org/zh/downloads/)
2. 配置nacos服务发现( 假定已有Nacos环境)  
${root_dir}/config/application.yml

* 把原先默认的配置注释掉:
```java
cluster:
# standalone:
```

* 放开以下配置:  
```java
cluster:  
    nacos:  
       serviceName: ${SW_SERVICE_NAME:"SkyWalking_OAP_Cluster"}  
       hostPort: ${SW_CLUSTER_NACOS_HOST_PORT:localhost:8848}
```
3.配置存储组件为 elasticsearch
${root_dir}/config/application.yml
* 放开对应的配置
```java
storage:
  elasticsearch:
    nameSpace: ${SW_NAMESPACE:""}
    clusterNodes: ${SW_STORAGE_ES_CLUSTER_NODES:localhost:9200}
    protocol: ${SW_STORAGE_ES_HTTP_PROTOCOL:"http"}
#    trustStorePath: ${SW_SW_STORAGE_ES_SSL_JKS_PATH:"../es_keystore.jks"}
#    trustStorePass: ${SW_SW_STORAGE_ES_SSL_JKS_PASS:""}
#    user: ${SW_ES_USER:""}
#    password: ${SW_ES_PASSWORD:""}
    indexShardsNumber: ${SW_STORAGE_ES_INDEX_SHARDS_NUMBER:2}
    indexReplicasNumber: ${SW_STORAGE_ES_INDEX_REPLICAS_NUMBER:0}
#    # Those data TTL settings will override the same settings in core module.
#    recordDataTTL: ${SW_STORAGE_ES_RECORD_DATA_TTL:7} # Unit is day
#    otherMetricsDataTTL: ${SW_STORAGE_ES_OTHER_METRIC_DATA_TTL:45} # Unit is day
#    monthMetricsDataTTL: ${SW_STORAGE_ES_MONTH_METRIC_DATA_TTL:18} # Unit is month
#    # Batch process setting, refer to https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.5/java-docs-bulk-processor.html
#    bulkActions: ${SW_STORAGE_ES_BULK_ACTIONS:1000} # Execute the bulk every 1000 requests
#    flushInterval: ${SW_STORAGE_ES_FLUSH_INTERVAL:10} # flush the bulk every 10 seconds whatever the number of requests
#    concurrentRequests: ${SW_STORAGE_ES_CONCURRENT_REQUESTS:2} # the number of concurrent requests
#    metadataQueryMaxSize: ${SW_STORAGE_ES_QUERY_MAX_SIZE:5000}
#    segmentQueryMaxSize: ${SW_STORAGE_ES_QUERY_SEGMENT_SIZE:200}
```
* 注释掉默认的H2存储
```java
storage:
  # h2:
  #   driver: ${SW_STORAGE_H2_DRIVER:org.h2.jdbcx.JdbcDataSource}
  #   url: ${SW_STORAGE_H2_URL:jdbc:h2:mem:skywalking-oap-db}
  #   user: ${SW_STORAGE_H2_USER:sa}
  #   metadataQueryMaxSize: ${SW_STORAGE_H2_QUERY_MAX_SIZE:5000}
```
4.把对应的可选组件复制到组件目录  
* gateway 对应的组件 
${root_dir}/agent/optional-plugins/apm-spring-cloud-gateway-2.x-plugin-6.4.0.jar  
复制到  
${root_dir}/agent/plugins/  
* gateway 对应的组件 
${root_dir}/agent/optional-plugins/apm-trace-ignore-plugin-6.4.0.jar  
复制到  
${root_dir}/agent/plugins/

5.运行(oap 和 ui 都会启动)  
```
sh ${root_dir}/bin/startup.sh
```  
ui 访问路径: http://localhost:8080  
oap: 路径 http://localhost:11800
> 这两个端口都可以修改  
8080 在 
> ${root_dir}/webapp/webapp.yml 文件下  
```
server:  
    port: 8080
collector:
  path: /graphql
  ribbon:
    ReadTimeout: 10000
    # Point to all backend's restHost:restPort, split by ,
    listOfServers: 127.0.0.1:12800
```
> 11800 默认端口在
> ${root_dir}/config/application.yml 文件下
```java
core:
  default:
    restPort: ${SW_CORE_REST_PORT:12800}
    gRPCPort: ${SW_CORE_GRPC_PORT:11800}
```  
这里的12800是对应的对外提供的查询查询，ui 会使用到 webapp.yml 配置


6.项目配置
添加依赖（需要跟服务版本一致）  
```java
<!-- 追踪 Context 相应的一些 API -->
<dependency>
    <groupId>org.apache.skywalking</groupId>
    <artifactId>apm-toolkit-trace</artifactId>
    <version>6.4.0</version>
    <scope>provided</scope>
</dependency>
<!-- 结合日志 在日志打印 traceId -->
<dependency>
    <groupId>org.apache.skywalking</groupId>
    <artifactId>apm-toolkit-logback-1.x</artifactId>
    <version>6.4.0</version>
</dependency>
```

7. 配置日志文件  
使用 %tid  

```java
<configuration>
    <springProperty scope="context" name="app_name" source="spring.application.name"/>
    <springProperty scope="context" name="env" source="spring.profiles.active"/>
    <property name="CONSOLE_LOG_PATTERN"
              value="[%d{yyyy-MM-dd HH:mm:ss.SSS} ${app_name} %tid %highlight(%-5level) %yellow(%thread) %green(%logger) %L %msg%n"/>


    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout">
                <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            </layout>
            <charset>utf-8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>debug</level>
        </filter>
    </appender>

    <logger name="org.springframework" level="INFO"/>
    <logger name="com.liangyt" level="DEBUG"/>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```