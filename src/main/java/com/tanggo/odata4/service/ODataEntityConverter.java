package com.tanggo.odata4.service;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.model.OrderItem;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import com.tanggo.odata4.config.EdmProvider;

@Service
public class ODataEntityConverter {
    
    public Entity convertOrder(Order order) {
        Entity e = new Entity();
        
        // 设置实体类型
        e.setType(EdmProvider.ET_ORDER_FQN.getFullQualifiedNameAsString());
        
        // 添加属性，确保所有值都是正确的类型
        e.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, order.getId()));
        e.addProperty(new Property(null, "OrderNumber", ValueType.PRIMITIVE, order.getOrderNumber()));
        e.addProperty(new Property(null, "Status", ValueType.ENUM, order.getStatus().name()));
        e.addProperty(new Property(null, "CustomerName", ValueType.PRIMITIVE, order.getCustomerName()));
        e.addProperty(new Property(null, "CustomerEmail", ValueType.PRIMITIVE, order.getCustomerEmail()));
        e.addProperty(new Property(null, "CustomerPhone", ValueType.PRIMITIVE, order.getCustomerPhone()));
        e.addProperty(new Property(null, "TotalAmount", ValueType.PRIMITIVE, 
            order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO));
        e.addProperty(new Property(null, "Notes", ValueType.PRIMITIVE, order.getNotes()));

        // 转换订单项
        EntityCollection itemsCollection = new EntityCollection();
        if (order.getItems() != null) {
            order.getItems().forEach(item -> 
                itemsCollection.getEntities().add(convertOrderItem(item)));
        }
        e.addProperty(new Property(null, "Items", ValueType.COLLECTION_COMPLEX, itemsCollection));
        
        return e;
    }
    
    public Entity convertOrderItem(OrderItem item) {
        Entity e = new Entity();
        
        // 设置实体类型
        e.setType(EdmProvider.ET_ORDER_ITEM_FQN.getFullQualifiedNameAsString());
        
        // 添加属性，确保所有值都是正确的类型
        e.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, item.getId()));
        e.addProperty(new Property(null, "ProductCode", ValueType.PRIMITIVE, item.getProductCode()));
        e.addProperty(new Property(null, "ProductName", ValueType.PRIMITIVE, item.getProductName()));
        e.addProperty(new Property(null, "UnitPrice", ValueType.PRIMITIVE, item.getUnitPrice()));
        e.addProperty(new Property(null, "Quantity", ValueType.PRIMITIVE, item.getQuantity()));
        e.addProperty(new Property(null, "Subtotal", ValueType.PRIMITIVE, item.getSubtotal()));
        e.addProperty(new Property(null, "Notes", ValueType.PRIMITIVE, item.getNotes()));
        
        return e;
    }
    
    public List<Entity> convertOrders(List<Order> orders) {
        return orders.stream()
                .map(this::convertOrder)
                .collect(Collectors.toList());
    }
} 