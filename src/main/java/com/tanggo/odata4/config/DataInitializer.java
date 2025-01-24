package com.tanggo.odata4.config;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.model.OrderItem;
import com.tanggo.odata4.model.OrderStatus;
import com.tanggo.odata4.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // 创建测试订单1
        Order order1 = new Order();
        order1.setOrderNumber("ORD-2024-001");
        order1.setStatus(OrderStatus.CONFIRMED);
        order1.setCustomerName("张三");
        order1.setCustomerEmail("zhangsan@example.com");
        order1.setCustomerPhone("13800138001");
        order1.setNotes("普通配送");

        OrderItem item1 = new OrderItem();
        item1.setProductCode("P001");
        item1.setProductName("iPhone 15");
        item1.setUnitPrice(new BigDecimal("6999.00"));
        item1.setQuantity(1);
        item1.setNotes("黑色 256GB");

        OrderItem item2 = new OrderItem();
        item2.setProductCode("P002");
        item2.setProductName("AirPods Pro");
        item2.setUnitPrice(new BigDecimal("1999.00"));
        item2.setQuantity(1);
        item2.setNotes("白色");

        order1.addItem(item1);
        order1.addItem(item2);

        // 创建测试订单2
        Order order2 = new Order();
        order2.setOrderNumber("ORD-2024-002");
        order2.setStatus(OrderStatus.DRAFT);
        order2.setCustomerName("李四");
        order2.setCustomerEmail("lisi@example.com");
        order2.setCustomerPhone("13900139002");
        order2.setNotes("加急配送");

        OrderItem item3 = new OrderItem();
        item3.setProductCode("P003");
        item3.setProductName("MacBook Pro");
        item3.setUnitPrice(new BigDecimal("12999.00"));
        item3.setQuantity(1);
        item3.setNotes("银色 16GB/512GB");

        order2.addItem(item3);

        // 保存订单
        orderRepository.saveAll(Arrays.asList(order1, order2));
    }
} 