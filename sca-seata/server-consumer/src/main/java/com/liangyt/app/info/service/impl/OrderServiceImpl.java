package com.liangyt.app.info.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangyt.app.consumer.LevelClientService;
import com.liangyt.app.info.entity.Order;
import com.liangyt.app.info.mapper.OrderMapper;
import com.liangyt.app.info.service.IOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 正常
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public void test() {
        Order order = new Order();
        order.setName("seate-test");
        order.setTime(new Date());
        orderService.save(order);
        levelClientService.addLevel();
    }

    /**
     * 分支异常
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public void ex() {
        Order order = new Order();
        order.setName("business exception");
        order.setTime(new Date());
        orderService.save(order);
        levelClientService.ex();
    }

    /**
     * 分支先正常后异常
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public void normalToEx() {
        levelClientService.addLevel();
        Order order = new Order();
        order.setName("business exception");
        order.setTime(new Date());
        orderService.save(order);
        levelClientService.ex();
    }
}
