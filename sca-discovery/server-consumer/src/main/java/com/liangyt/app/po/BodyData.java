package com.liangyt.app.po;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 描述：request body 数据
 * 作者：liangyongtong
 * 日期：2019/12/4 5:22 PM
 */
@Setter
@Getter
@ToString
public class BodyData implements Serializable {
    private String name;
    private int age;
}
