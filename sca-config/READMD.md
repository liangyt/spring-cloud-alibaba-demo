#### Nacos 服务发现与注册

```java
cd server-producer
mvn spring-boot:run -Dserver.port=8001
mvn spring-boot:run -Dserver.port=8002
```
启动完成可以看到从配置中心拉取配置的日志：
```java
2019-12-24 09:38:08.801  INFO 11692 --- [           main] c.a.c.n.c.NacosPropertySourceBuilder     : Loading nacos data, dataId: 'common-db.properties', group: 'DEFAULT_GROUP'
2019-12-24 09:38:08.814  INFO 11692 --- [           main] c.a.c.n.c.NacosPropertySourceBuilder     : Loading nacos data, dataId: 'nacos-producer-dev.properties', group: 'DEFAULT_GROUP'
2019-12-24 09:38:08.815  INFO 11692 --- [           main] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource {name='NACOS', propertySources=[NacosPropertySource {name='nacos-producer-dev.properties'}, NacosPropertySource {name='nacos-producer.properties'}, NacosPropertySource {name='common-db.properties'}]}
```

```java
cd server-consumer
mvn spring-boot:run -Dserver.port=9001
```

```java
2019-12-24 09:38:34.449  INFO 11888 --- [           main] c.a.c.n.c.NacosPropertySourceBuilder     : Loading nacos data, dataId: 'common-db.properties', group: 'DEFAULT_GROUP'
2019-12-24 09:38:34.467  INFO 11888 --- [           main] c.a.c.n.c.NacosPropertySourceBuilder     : Loading nacos data, dataId: 'nacos-consumer.properties', group: 'DEFAULT_GROUP'
2019-12-24 09:38:34.468  INFO 11888 --- [           main] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource {name='NACOS', propertySources=[NacosPropertySource {name='nacos-consumer.properties'}, NacosPropertySource {name='common-db.properties'}]}
```

```html
http://localhost:9001/back
```

返回结果：

> 我是nacos producer配置:姓名:123456我是nacos consumer配置:姓名:123456