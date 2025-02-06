package com.tanggo.odata4.repository;

import com.tanggo.odata4.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 基础的CRUD操作由JpaRepository提供
    // 可以根据需要添加自定义查询方法
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items LEFT JOIN FETCH o.user")
    List<Order> findAllWithItems();
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items LEFT JOIN FETCH o.user WHERE o.id = :id")
    Order findByIdWithItems(Long id);
} 