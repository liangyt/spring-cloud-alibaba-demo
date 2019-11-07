package com.liangyt.readingandwritingseparation.controller;

import com.liangyt.readingandwritingseparation.config.DataSourceRoutingWith;
import com.liangyt.readingandwritingseparation.entity.Member;
import com.liangyt.readingandwritingseparation.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：会员
 * 作者：liangyongtong
 */
@RestController
@Slf4j
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/insert")
    @DataSourceRoutingWith("masterDataSource")
    public void insert(String name) {
        memberService.insert(new Member(null, name));
    }

    @GetMapping("/detail")
    @DataSourceRoutingWith("slaveDataSource")
    public Object get(int id) {
        return memberService.find(id);
    }
}
