package com.tanggo.odata4.repository;

import com.tanggo.odata4.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 基础的CRUD操作由JpaRepository提供
    // 可以根据需要添加自定义查询方法
} 