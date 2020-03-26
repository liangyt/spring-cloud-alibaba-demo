package com.liangyt.app.info.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liangyt.app.info.entity.Level;
import com.liangyt.app.info.service.impl.LevelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author liangyongtong
 * @since 2020-03-16
 */
@RestController
@RequestMapping("/mybatis/info")
public class TestController {
    @Autowired
    private LevelServiceImpl levelService;

    /**
     * 添加级别
     * 结果： 两个数据源的级别表都添加了相同的数据记录
     */
    @GetMapping("/addLevel")
    public void addLevel() {
        levelService.test();
    }

    /**
     * 获取级别
     * 结果：从其中一个数据源获取结果
     * @param code
     * @return
     */
    @GetMapping("/getLevelByCode/{code}")
    public Object getLevelByCode(@PathVariable String code) {
        QueryWrapper query = new QueryWrapper();
        query.eq("code", code);
        return levelService.getOne(query);
    }
}

