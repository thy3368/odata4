package com.tanggo.odata4.controller;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.model.Product;
import com.tanggo.odata4.model.User;
import com.tanggo.odata4.repository.OrderRepository;
import com.tanggo.odata4.repository.ProductRepository;
import com.tanggo.odata4.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ApiController(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Order endpoints
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return ResponseEntity.ok(Map.of(
            "data", orderPage.getContent(),
            "total", orderPage.getTotalElements(),
            "page", orderPage.getNumber(),
            "size", orderPage.getSize()
        ));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Product endpoints
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);

        return ResponseEntity.ok(Map.of(
            "data", productPage.getContent(),
            "total", productPage.getTotalElements(),
            "page", productPage.getNumber(),
            "size", productPage.getSize()
        ));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // User endpoints
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        Page<User> userPage = userRepository.findAll(pageable);

        return ResponseEntity.ok(Map.of(
            "data", userPage.getContent(),
            "total", userPage.getTotalElements(),
            "page", userPage.getNumber(),
            "size", userPage.getSize()
        ));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private Pageable createPageable(int page, int size, String sort) {
        if (sort != null && !sort.isEmpty()) {
            String[] parts = sort.split(",");
            String property = parts[0];
            Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, property));
        }
        return PageRequest.of(page, size);
    }
} 