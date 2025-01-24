package com.tanggo.odata4.processor;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.model.OrderStatus;
import com.tanggo.odata4.repository.OrderRepository;
import com.tanggo.odata4.service.ODataEntityConverter;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.olingo.server.api.ODataApplicationException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderEntityProcessorTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ODataEntityConverter entityConverter;

    @Mock
    private OData odata;

    @Mock
    private ServiceMetadata serviceMetadata;

    @Mock
    private UriInfo uriInfo;

    @Mock
    private ODataRequest request;

    @Mock
    private ODataResponse response;

    @Mock
    private UriResourceEntitySet uriResourceEntitySet;

    @Mock
    private EdmEntitySet edmEntitySet;

    @Mock
    private EdmEntityType edmEntityType;

    @InjectMocks
    private OrderEntityProcessor processor;

    @BeforeEach
    void setUp() {
        processor.init(odata, serviceMetadata);
    }

    @Test
    void readEntity_ShouldReturnOrder_WhenOrderExists() throws Exception {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setOrderNumber("TEST-001");
        order.setStatus(OrderStatus.CONFIRMED);

        UriParameter keyPredicate = mock(UriParameter.class);
        when(keyPredicate.getText()).thenReturn(orderId.toString());

        when(uriInfo.getUriResourceParts()).thenReturn(
                Collections.singletonList(uriResourceEntitySet));
        when(uriResourceEntitySet.getEntitySet()).thenReturn(edmEntitySet);
        when(uriResourceEntitySet.getKeyPredicates()).thenReturn(
                Collections.singletonList(keyPredicate));
        when(edmEntitySet.getName()).thenReturn("Orders");
        when(edmEntitySet.getEntityType()).thenReturn(edmEntityType);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(entityConverter.convertOrder(order)).thenReturn(mock(Entity.class));

        // Act
        processor.readEntity(request, response, uriInfo, ContentType.JSON);

        // Assert
        verify(orderRepository).findById(orderId);
        verify(entityConverter).convertOrder(order);
        verify(response).setStatusCode(HttpStatusCode.OK.getStatusCode());
    }

    @Test
    void deleteEntity_ShouldDeleteOrder_WhenOrderExists() throws Exception {
        // Arrange
        Long orderId = 1L;
        UriParameter keyPredicate = mock(UriParameter.class);
        when(keyPredicate.getText()).thenReturn(orderId.toString());

        when(uriInfo.getUriResourceParts()).thenReturn(
                Collections.singletonList(uriResourceEntitySet));
        when(uriResourceEntitySet.getEntitySet()).thenReturn(edmEntitySet);
        when(uriResourceEntitySet.getKeyPredicates()).thenReturn(
                Collections.singletonList(keyPredicate));
        when(edmEntitySet.getName()).thenReturn("Orders");

        when(orderRepository.existsById(orderId)).thenReturn(true);

        // Act
        processor.deleteEntity(request, response, uriInfo);

        // Assert
        verify(orderRepository).deleteById(orderId);
        verify(response).setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }

    @Test
    void readEntity_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        Long orderId = 999L;
        UriParameter keyPredicate = mock(UriParameter.class);
        when(keyPredicate.getText()).thenReturn(orderId.toString());

        when(uriInfo.getUriResourceParts()).thenReturn(
                Collections.singletonList(uriResourceEntitySet));
        when(uriResourceEntitySet.getEntitySet()).thenReturn(edmEntitySet);
        when(uriResourceEntitySet.getKeyPredicates()).thenReturn(
                Collections.singletonList(keyPredicate));
        when(edmEntitySet.getName()).thenReturn("Orders");

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ODataApplicationException.class, () -> 
            processor.readEntity(request, response, uriInfo, ContentType.JSON));
    }

    @Test
    void createEntity_ShouldCreateOrder_WhenValidRequest() throws Exception {
        // Arrange
        Order order = new Order();
        order.setOrderNumber("TEST-001");
        order.setStatus(OrderStatus.DRAFT);

        Entity requestEntity = mock(Entity.class);
        when(requestEntity.getProperty("OrderNumber")).thenReturn(
            new Property(null, "OrderNumber", ValueType.PRIMITIVE, "TEST-001"));
        when(requestEntity.getProperty("Status")).thenReturn(
            new Property(null, "Status", ValueType.PRIMITIVE, "DRAFT"));
        // ... 设置其他必要的属性

        when(uriInfo.getUriResourceParts()).thenReturn(
                Collections.singletonList(uriResourceEntitySet));
        when(uriResourceEntitySet.getEntitySet()).thenReturn(edmEntitySet);
        when(edmEntitySet.getName()).thenReturn("Orders");
        when(edmEntitySet.getEntityType()).thenReturn(edmEntityType);

        ODataDeserializer deserializer = mock(ODataDeserializer.class);
        when(odata.createDeserializer(any())).thenReturn(deserializer);
        DeserializerResult deserializerResult = mock(DeserializerResult.class);
        when(deserializer.entity(any(), any())).thenReturn(deserializerResult);
        when(deserializerResult.getEntity()).thenReturn(requestEntity);

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(entityConverter.convertOrder(any(Order.class))).thenReturn(mock(Entity.class));

        // Act
        processor.createEntity(request, response, uriInfo, ContentType.JSON, ContentType.JSON);

        // Assert
        verify(orderRepository).save(any(Order.class));
        verify(response).setStatusCode(HttpStatusCode.CREATED.getStatusCode());
    }

    @Test
    void updateEntity_ShouldUpdateOrder_WhenOrderExists() throws Exception {
        // Arrange
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setOrderNumber("TEST-001");
        existingOrder.setStatus(OrderStatus.DRAFT);

        Entity requestEntity = mock(Entity.class);
        when(requestEntity.getProperty("OrderNumber")).thenReturn(
            new Property(null, "OrderNumber", ValueType.PRIMITIVE, "TEST-001-UPDATED"));
        when(requestEntity.getProperty("Status")).thenReturn(
            new Property(null, "Status", ValueType.PRIMITIVE, "CONFIRMED"));
        // ... 设置其他必要的属性

        UriParameter keyPredicate = mock(UriParameter.class);
        when(keyPredicate.getText()).thenReturn(orderId.toString());

        when(uriInfo.getUriResourceParts()).thenReturn(
                Collections.singletonList(uriResourceEntitySet));
        when(uriResourceEntitySet.getEntitySet()).thenReturn(edmEntitySet);
        when(uriResourceEntitySet.getKeyPredicates()).thenReturn(
                Collections.singletonList(keyPredicate));
        when(edmEntitySet.getName()).thenReturn("Orders");
        when(edmEntitySet.getEntityType()).thenReturn(edmEntityType);

        ODataDeserializer deserializer = mock(ODataDeserializer.class);
        when(odata.createDeserializer(any())).thenReturn(deserializer);
        DeserializerResult deserializerResult = mock(DeserializerResult.class);
        when(deserializer.entity(any(), any())).thenReturn(deserializerResult);
        when(deserializerResult.getEntity()).thenReturn(requestEntity);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        // Act
        processor.updateEntity(request, response, uriInfo, ContentType.JSON, ContentType.JSON);

        // Assert
        verify(orderRepository).save(any(Order.class));
        verify(response).setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }
} 