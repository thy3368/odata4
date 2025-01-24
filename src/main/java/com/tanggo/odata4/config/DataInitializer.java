package com.tanggo.odata4.config;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.model.OrderItem;
import com.tanggo.odata4.model.OrderStatus;
import com.tanggo.odata4.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;

    public DataInitializer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        // 如果数据库为空，则初始化一些测试数据
        if (orderRepository.count() == 0) {
            // 创建草稿订单
            Order order1 = new Order();
            order1.setOrderNumber("ORD-2024-001");
            order1.setStatus(OrderStatus.DRAFT);
            order1.setCustomerName("张三");
            order1.setCustomerEmail("zhangsan@example.com");
            order1.setCustomerPhone("13800138000");
            
            OrderItem item1 = new OrderItem();
            item1.setProductCode("P001");
            item1.setProductName("商品1");
            item1.setUnitPrice(new BigDecimal("100.00"));
            item1.setQuantity(2);
            order1.addItem(item1);
            
            orderRepository.save(order1);

            // 创建待处理订单
            Order order2 = new Order();
            order2.setOrderNumber("ORD-2024-002");
            order2.setStatus(OrderStatus.PENDING);
            order2.setCustomerName("李四");
            order2.setCustomerEmail("lisi@example.com");
            order2.setCustomerPhone("13900139000");
            
            OrderItem item2 = new OrderItem();
            item2.setProductCode("P002");
            item2.setProductName("商品2");
            item2.setUnitPrice(new BigDecimal("200.00"));
            item2.setQuantity(1);
            order2.addItem(item2);
            
            orderRepository.save(order2);

            // 创建处理中订单
            Order order3 = new Order();
            order3.setOrderNumber("ORD-2024-003");
            order3.setStatus(OrderStatus.PROCESSING);
            order3.setCustomerName("王五");
            order3.setCustomerEmail("wangwu@example.com");
            order3.setCustomerPhone("13700137000");
            
            OrderItem item3 = new OrderItem();
            item3.setProductCode("P003");
            item3.setProductName("商品3");
            item3.setUnitPrice(new BigDecimal("300.00"));
            item3.setQuantity(3);
            order3.addItem(item3);
            
            orderRepository.save(order3);

            // 创建已完成订单
            Order order4 = new Order();
            order4.setOrderNumber("ORD-2024-004");
            order4.setStatus(OrderStatus.COMPLETED);
            order4.setCustomerName("赵六");
            order4.setCustomerEmail("zhaoliu@example.com");
            order4.setCustomerPhone("13600136000");
            
            OrderItem item4 = new OrderItem();
            item4.setProductCode("P004");
            item4.setProductName("商品4");
            item4.setUnitPrice(new BigDecimal("400.00"));
            item4.setQuantity(1);
            order4.addItem(item4);
            
            orderRepository.save(order4);
        }
    }
} 