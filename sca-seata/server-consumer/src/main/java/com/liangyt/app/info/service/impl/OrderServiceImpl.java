package com.liangyt.app.info.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangyt.app.consumer.LevelClientService;
import com.liangyt.app.info.entity.Order;
import com.liangyt.app.info.mapper.OrderMapper;
import com.liangyt.app.info.service.IOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 表单 服务实现类
 * </p>
 *
 * @author liangyongtong
 * @since 2020-03-16
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private LevelClientService levelClientService;
    @Autowired
    private OrderServiceImpl orderService;

    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public void test() {
        Order order = new Order();
        order.setName("seate-test");
        order.setTime(new Date());
        orderService.save(order);
        levelClientService.addLevel();
    }
}
