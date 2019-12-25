package com.liangyt.gateway.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 描述：动态更新路由信息
 * 通过Nacos Api 监听文件更新，获取路由文件内容；再通过 Gateway Api动态更新路由信息
 * 系统启动的时候会加载一次路由配置文件，在文件更新的时候，则会通过监听获取到内容
 *
 * filters 下面的 args 的值可以参考对应的定义，看看属性 List 的顺序，然后按照 _genkey_{index} 设置
  [
      {
          "id": "dynamicroute",
          "order": 0,
          "uri": "lb://nacos-consumer",
          "predicates": [
              {
                  "name": "Path",
                  "args": {
                      "pattern": "/dynamicroute"
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
    private RouteDefinitionLocator routeDefinitionLocator;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostConstruct
    public void initRoute() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, namespace);
        ConfigService configService = NacosFactory.createConfigService(properties);

        // 系统启动拿到初始路由配置信息
        String routeContent = configService.getConfig(routeDataId, group, 3000);
        log.debug("初始化路由配置信息: {}", routeContent);
        // 初始化路由信息
        routeHandle(routeContent);
        // 路由配置文件修改发布监听
        configService.addListener(routeDataId, group, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String routeJson) {
                log.debug("路由配置信息: {}", routeJson);
                // 更新路由, 返回处理过的所有路由Id
                List<String> updateRouteIds = routeHandle(routeJson);

                // 删除除了更新以外的其它路由配置信息
                deleteRoute(updateRouteIds);
            }
        });
    }

    // 删除没有更新处理以外的路由
    private void deleteRoute(List<String> updateRouteIds) {
        // 这里返回的是系统中所有的路由列表，包含 (文件定义，动态添加)
        // 所以处理删除的时候需要注意误删除
        try {
            routeDefinitionLocator.getRouteDefinitions()
                .map(routeDefinition -> {
                    try {
                        log.debug("route -> {}", mapper.writeValueAsString(routeDefinition));
                    } catch (JsonProcessingException e) {
                        log.error("object to json error", e);
                    }
                    return routeDefinition;
                })
                .filter(routeDefinition -> StringUtils.isNotBlank(routeDefinition.getId()) && !updateRouteIds.contains(routeDefinition.getId()))
                .map(routeDefinition -> routeDefinition.getId())
                // 这一行是属于有点业务属性的意思了，不删除在配置文件里面配置的以 method 开头的路由id
                .filter(routeDefinitionId -> !routeDefinitionId.startsWith("method"))
                .subscribe((id) -> {
                    log.debug("路由Id -> {}", id);
                    try {
                        // 如果未包含在该处理列表中的路由则删除
                        this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
                        log.debug("删除路由成功 -> {}", id);
                    } catch (Exception e) {
                        log.error("删除路由失败 -> {} ", id, e);
                    }
                });
        } catch (Exception e) {
            log.error("删除不需要路由异常: ", e);
        }
    }

    /**
     * 路由信息处理
     * @param routeJson
     */
    private List<String> routeHandle(String routeJson) {
        try {
            // 利用 jackson 转化为列表
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, RouteDefinition.class);
            List<RouteDefinition> routes = mapper.readValue(routeJson, type);

            return updateRoute(routes);
        } catch (Exception e) {
            log.error("更新路由信息异常：", e);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 动态更新路由信息
     * @param routes
     */
    private List<String> updateRoute(List<RouteDefinition> routes) {

        if (null == routes || routes.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        // 文件中所有的路由id
        List<String> routeIds = new ArrayList<>();

        for (int i = 0; i < routes.size(); i++) {
            RouteDefinition definition = routes.get(i);

            // 保存/更新路由
            this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            log.debug("保存/更新路由成功 -> {}", definition.getId());

            routeIds.add(definition.getId());
        }

        return routeIds;
    }
}
