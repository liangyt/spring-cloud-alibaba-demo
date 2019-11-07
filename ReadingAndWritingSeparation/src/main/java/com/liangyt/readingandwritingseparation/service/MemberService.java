package com.liangyt.readingandwritingseparation.service;

import com.liangyt.readingandwritingseparation.dao.MemberMapper;
import com.liangyt.readingandwritingseparation.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述：会员
 * 作者：liangyongtong
 */
@SuppressWarnings("all")
@Service
public class MemberService {
    @Autowired
    private MemberMapper memberMapper;

    @Transactional
    public void insert(Member member) {
        memberMapper.insert(member);
    }

    public Member find(int id) {
        return memberMapper.selectByPrimaryKey(id);
    }
}
