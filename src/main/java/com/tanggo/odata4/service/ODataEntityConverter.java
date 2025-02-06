package com.tanggo.odata4.service;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.model.OrderItem;
import com.tanggo.odata4.model.Product;
import com.tanggo.odata4.model.User;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ODataEntityConverter {

    public List<Entity> convertOrders(List<Order> orders) {
        return orders.stream()
                .map(this::convertOrder)
                .collect(Collectors.toList());
    }

    public Entity convertOrder(Order order) {
        Entity e = new Entity();
        e.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, order.getId()));
        e.addProperty(new Property(null, "OrderNumber", ValueType.PRIMITIVE, order.getOrderNumber()));
        e.addProperty(new Property(null, "OrderDate", ValueType.PRIMITIVE, order.getOrderDate()));
        e.addProperty(new Property(null, "TotalAmount", ValueType.PRIMITIVE, order.getTotalAmount()));
        e.addProperty(new Property(null, "Status", ValueType.PRIMITIVE, order.getStatus()));

        // 添加导航属性
        if (order.getItems() != null) {
            EntityCollection items = new EntityCollection();
            items.getEntities().addAll(convertOrderItems(order.getItems()));
            e.addProperty(new Property(null, "Items", ValueType.COLLECTION_COMPLEX, items));
        }

        if (order.getUser() != null) {
            e.addProperty(new Property(null, "User", ValueType.COMPLEX, convertUser(order.getUser())));
        }

        return e;
    }

    public List<Entity> convertOrderItems(List<OrderItem> items) {
        return items.stream()
                .map(this::convertOrderItem)
                .collect(Collectors.toList());
    }

    public Entity convertOrderItem(OrderItem item) {
        Entity e = new Entity();
        e.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, item.getId()));
        e.addProperty(new Property(null, "Quantity", ValueType.PRIMITIVE, item.getQuantity()));
        e.addProperty(new Property(null, "UnitPrice", ValueType.PRIMITIVE, item.getUnitPrice()));

        if (item.getProduct() != null) {
            e.addProperty(new Property(null, "Product", ValueType.COMPLEX, convertProduct(item.getProduct())));
        }

        return e;
    }

    public List<Entity> convertProducts(List<Product> products) {
        return products.stream()
                .map(this::convertProduct)
                .collect(Collectors.toList());
    }

    public Entity convertProduct(Product product) {
        Entity e = new Entity();
        e.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, product.getId()));
        e.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, product.getName()));
        e.addProperty(new Property(null, "Description", ValueType.PRIMITIVE, product.getDescription()));
        e.addProperty(new Property(null, "Price", ValueType.PRIMITIVE, product.getPrice()));
        e.addProperty(new Property(null, "Stock", ValueType.PRIMITIVE, product.getStock()));
        e.addProperty(new Property(null, "ImageUrl", ValueType.PRIMITIVE, product.getImageUrl()));
        return e;
    }

    public List<Entity> convertUsers(List<User> users) {
        return users.stream()
                .map(this::convertUser)
                .collect(Collectors.toList());
    }

    public Entity convertUser(User user) {
        Entity e = new Entity();
        e.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, user.getId()));
        e.addProperty(new Property(null, "Username", ValueType.PRIMITIVE, user.getUsername()));
        e.addProperty(new Property(null, "Email", ValueType.PRIMITIVE, user.getEmail()));
        e.addProperty(new Property(null, "Phone", ValueType.PRIMITIVE, user.getPhone()));
        e.addProperty(new Property(null, "Address", ValueType.PRIMITIVE, user.getAddress()));

        if (user.getOrders() != null) {
            EntityCollection orders = new EntityCollection();
            orders.getEntities().addAll(convertOrders(user.getOrders()));
            e.addProperty(new Property(null, "Orders", ValueType.COLLECTION_COMPLEX, orders));
        }

        return e;
    }
} 