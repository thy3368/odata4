package com.tanggo.odata4.service;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> searchOrders(String customerName, String status, String dateFrom, String dateTo) {
        List<Order> orders = orderRepository.findAllWithItems();

        return orders.stream()
                .filter(order -> filterByCustomerName(order, customerName))
                .filter(order -> filterByStatus(order, status))
                .filter(order -> filterByDateRange(order, dateFrom, dateTo))
                .collect(Collectors.toList());
    }

    private boolean filterByCustomerName(Order order, String customerName) {
        return customerName == null || 
               order.getCustomerName().toLowerCase().contains(customerName.toLowerCase());
    }

    private boolean filterByStatus(Order order, String status) {
        return status == null || order.getStatus().equals(status);
    }

    private boolean filterByDateRange(Order order, String dateFrom, String dateTo) {
        LocalDateTime orderDate = order.getOrderDate();
        
        if (dateFrom != null) {
            LocalDateTime from = LocalDateTime.parse(dateFrom, DATE_FORMATTER);
            if (orderDate.isBefore(from)) {
                return false;
            }
        }
        
        if (dateTo != null) {
            LocalDateTime to = LocalDateTime.parse(dateTo, DATE_FORMATTER);
            if (orderDate.isAfter(to)) {
                return false;
            }
        }
        
        return true;
    }

    @Transactional
    public Optional<Order> cancelOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus().equals("PENDING")) {
                order.setStatus("CANCELLED");
                return Optional.of(orderRepository.save(order));
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Order> completeOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus().equals("PROCESSING")) {
                order.setStatus("COMPLETED");
                return Optional.of(orderRepository.save(order));
            }
        }
        return Optional.empty();
    }
} 