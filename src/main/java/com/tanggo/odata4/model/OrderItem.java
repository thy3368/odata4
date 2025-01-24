package com.tanggo.odata4.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem extends BaseEntity {
    
    @Column(nullable = false)
    private String productCode;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem() {
        // 初始化所有数值字段
        this.unitPrice = BigDecimal.ZERO;
        this.quantity = 0;
        this.subtotal = BigDecimal.ZERO;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = (unitPrice != null) ? unitPrice : BigDecimal.ZERO;
        calculateSubtotal();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = (quantity != null) ? quantity : 0;
        calculateSubtotal();
    }

    public BigDecimal getUnitPrice() {
        return (unitPrice != null) ? unitPrice : BigDecimal.ZERO;
    }

    public Integer getQuantity() {
        return (quantity != null) ? quantity : 0;
    }

    public BigDecimal getSubtotal() {
        return (subtotal != null) ? subtotal : BigDecimal.ZERO;
    }

    @PrePersist
    @PreUpdate
    private void calculateSubtotal() {
        BigDecimal safeUnitPrice = getUnitPrice();
        Integer safeQuantity = getQuantity();
        
        if (safeUnitPrice != null && safeQuantity != null) {
            this.subtotal = safeUnitPrice.multiply(BigDecimal.valueOf(safeQuantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }
} 