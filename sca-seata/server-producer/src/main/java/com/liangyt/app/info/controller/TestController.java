package com.liangyt.app.info.controller;

import com.liangyt.app.info.service.impl.LevelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
     * 添加级别 报异常
     * @return
     */
    @GetMapping("/ex")
    public void throwException() {
        levelService.throwException();
    }
}

