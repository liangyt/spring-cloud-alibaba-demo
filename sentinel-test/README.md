#### Sentinel
简单的实现了一下限流熔断处理

原始的代码层级测试：
```java
com.liangyt.sentineltest.howtouse
```
跑一下main方法直接可以看到效果.  
更详细的说明可以直接看官方文档: https://github.com/alibaba/Sentinel/wiki/如何使用

另外通过连接 Sentinel 控制台实现限流控制。  
需要先部署 Sentinel Dashboard，可以直接下载 jar 也可以自行编译。

这里偿试了注解的方式限流，通过控制实时控制。

也可以在系统启动的时候初始化相应的资源规则:
```java
com.liangyt.sentineltest.config.RulesConfig
```