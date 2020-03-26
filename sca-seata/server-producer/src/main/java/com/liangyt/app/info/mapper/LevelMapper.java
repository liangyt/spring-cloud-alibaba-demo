package com.liangyt.app.info.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liangyt.app.info.entity.Level;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * 级别 Mapper 接口
 * </p>
 *
 * @author liangyongtong
 * @since 2020-03-16
 */
@Mapper
public interface LevelMapper extends BaseMapper<Level> {

    Level getLevelById(Long id);
}
