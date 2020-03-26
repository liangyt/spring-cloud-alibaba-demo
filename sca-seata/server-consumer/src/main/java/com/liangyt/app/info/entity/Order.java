package com.liangyt.app.info.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 表单表
 * </p>
 *
 * @author liangyongtong
 * @since 2020-03-16
 */
@Data
@TableName("order_item")
public class Order {
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 时间
     */
    private Date time;
}
