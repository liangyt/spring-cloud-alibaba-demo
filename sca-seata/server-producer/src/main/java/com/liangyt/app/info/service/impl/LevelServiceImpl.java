package com.liangyt.app.info.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangyt.app.info.entity.Level;
import com.liangyt.app.info.mapper.LevelMapper;
import com.liangyt.app.info.service.ILevelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 级别表 服务实现类
 * </p>
 *
 * @author liangyongtong
 * @since 2020-03-16
 */
@Service
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level> implements ILevelService {

    @Transactional(rollbackFor = Exception.class)
    public void test() {
        Level level = new Level();
        level.setCode("0001");
        level.setName("默认级别");
        this.save(level);
//        throw new RuntimeException("回滚");
    }
}
