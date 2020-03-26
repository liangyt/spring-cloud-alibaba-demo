package com.liangyt.app.info.entity;

import lombok.Data;

/**
 * <p>
 * 级别表
 * </p>
 *
 * @author liangyongtong
 * @since 2020-03-16
 */
@Data
public class Level {
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 编号
     */
    private String code;
}
