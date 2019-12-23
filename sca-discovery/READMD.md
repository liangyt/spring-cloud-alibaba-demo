#### Nacos 服务发现

```java
cd server-producer
mvn spring-boot:run -Dserver.port=8001
mvn spring-boot:run -Dserver.port=8002
```
启动完成可以看到注册到服务的日志：
> 2019-12-23 17:33:21.469  INFO 9438 --- [           main] c.a.c.n.registry.NacosServiceRegistry    : nacos registry, nacos-producer 192.168.6.239:8001 register finished


```java
cd server-consumer
mvn spring-boot:run -Dserver.port=9001
```

```html
http://localhost:9001/back
```

