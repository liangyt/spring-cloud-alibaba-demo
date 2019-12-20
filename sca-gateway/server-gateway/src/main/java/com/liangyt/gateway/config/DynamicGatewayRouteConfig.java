package com.liangyt.gateway.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangyt.gateway.po.RouteDefinitionPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 描述：动态更新路由信息
 * 通过Nacos Api 监听文件更新，获取路由文件内容；再通过 Gateway Api动态更新路由信息
 * 系统启动的时候会加载一次路由配置文件，在文件更新的时候，则会通过监听获取到内容
 *
  [
      {
          "id": "dynamicroute",
          "order": 0,
          "uri": "lb://nacos-consumer",
          "predicates": [
              {
                  "name": "Path",
                  "args": {
                      "1234567": "/dynamicroute"
                  }
              }
          ],
          "filters": [
              {
                  "name": "Custom"
              }
          ]
      }
  ]
 *
 * 作者：liangyongtong
 * 日期：2019/12/20 3:59 PM
 */
@Slf4j
@Component
public class DynamicGatewayRouteConfig implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    // 取配置的地址
    @Value("${spring.cloud.nacos.config.server-addr:localhost:8848}")
    private String serverAddr;
    // 取配置的命名空间
    @Value("${spring.cloud.nacos.config.namespace:c8a2cd88-1c7e-400a-b270-86f689979b8f}")
    private String namespace;
    // 取配置的组
    @Value("${spring.cloud.nacos.config.group:DEFAULT_GROUP}")
    private String group;
    // 路由存放的文件
    @Value("${dynamic.route-dataid:gateway-route.json}")
    private String routeDataId;
    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    public void refreshRoute() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, namespace);
        ConfigService configService = NacosFactory.createConfigService(properties);

        configService.addListener(routeDataId, group, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String routeJson) {
                log.debug("路由配置信息: {}", routeJson);

                try {
                    // 利用 jackson 转化为列表
                    JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, RouteDefinitionPO.class);
                    List<RouteDefinitionPO> routes = mapper.readValue(routeJson, type);

                    updateRoute(routes);
                } catch (Exception e) {
                    log.error("更新路由信息异常：", e);
                }
            }
        });
    }

    /**
     * 动态更新路由信息
     * @param routes
     */
    public void updateRoute(List<RouteDefinitionPO> routes) {

        if (null == routes || routes.size() == 0) {
            return;
        }

        RouteDefinitionPO routePo = null;
        for (int i = 0; i < routes.size(); i++) {
            routePo = routes.get(i);
            RouteDefinition definition = new RouteDefinition();

            definition.setId(routePo.getId());
            definition.setOrder(routePo.getOrder());
            definition.setPredicates(routePo.getPredicates());
            definition.setFilters(routePo.getFilters());

            URI uri = UriComponentsBuilder.fromUriString(routePo.getUri()).build().toUri();
            definition.setUri(uri);

            // 删除原来可能存在的路由
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
            log.debug("删除路由成功 -> {}", definition.getId());
            // 保存/更新路由
            this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            log.debug("更新路由成功 -> {}", definition.getId());
        }
    }
}
