package com.liangyt.app.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * 描述：获取其它服务
 * 作者：liangyongtong
 * 日期：2019/10/24 5:05 PM
 */
@FeignClient(
        name = "nacos-producer", // 服务名 也可以使用 value = "nacos-producer"
        path = "/mybatis/info", // 这个值会拼接在下面的各请求的前面  如: /api/ok
        fallback = LevelClientService.ProducerServiceFallback.class // 如果调用的方法异常无响应，则调用对应的备用方法; ()
)
public interface LevelClientService {

    /**
     * 添加级别
     * 结果： 两个数据源的级别表都添加了相同的数据记录
     */
    @GetMapping("/addLevel")
    void addLevel();

    /**
     * 获取级别
     * 结果：从其中一个数据源获取结果
     * @param code
     * @return
     */
    @GetMapping("/getLevelByCode/{code}")
    Object getLevelByCode(@PathVariable("code") String code);

    @Slf4j
    @Component
    class ProducerServiceFallback implements LevelClientService {
        @Override
        public void addLevel() {
            log.error("异常");
        }

        @Override
        public Object getLevelByCode(String code) {
            return null;
        }
    }
}
