package com.tanggo.odata4.processor;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.model.OrderItem;
import com.tanggo.odata4.model.OrderStatus;
import com.tanggo.odata4.repository.OrderRepository;
import com.tanggo.odata4.service.ODataEntityConverter;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.ActionEntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import com.tanggo.odata4.config.EdmProvider;

@Component
public class OrderEntityProcessor implements EntityProcessor, ActionEntityProcessor {
    private static final Logger log = LoggerFactory.getLogger(OrderEntityProcessor.class);
    
    private OData odata;
    private ServiceMetadata serviceMetadata;
    private final OrderRepository orderRepository;
    private final ODataEntityConverter entityConverter;

    public OrderEntityProcessor(OrderRepository orderRepository, ODataEntityConverter entityConverter) {
        this.orderRepository = orderRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    @Transactional(readOnly = true)
    public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) 
            throws ODataApplicationException, ODataLibraryException {
        try {
            // 获取请求的实体集
            final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
            final UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
            final EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
            
            // 获取主键
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            Long id = Long.parseLong(keyPredicates.get(0).getText());
            
            // 创建序列化选项
            ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
            EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();
            
            // 获取序列化器
            ODataSerializer serializer = odata.createSerializer(responseFormat);
            
            // 获取实体数据
            Entity entity = null;
            if (edmEntitySet.getName().equals("Orders")) {
                entity = entityConverter.convertOrder(
                    orderRepository.findByIdWithItems(id)
                        .orElseThrow(() -> new ODataApplicationException(
                            "Order not found", 
                            HttpStatusCode.NOT_FOUND.getStatusCode(), 
                            Locale.ENGLISH))
                );
            }
            
            // 序列化结果
            SerializerResult serializerResult = serializer.entity(serviceMetadata, 
                    edmEntitySet.getEntityType(), entity, options);
            
            // 设置响应
            response.setContent(serializerResult.getContent());
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        } catch (Exception e) {
            log.error("Error reading order: {}", e.getMessage(), e);
            throw new ODataApplicationException("Error reading order: " + e.getMessage(),
                    HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
        }
    }

    @Override
    public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
                           ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
        try {
            // 获取实体集
            final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
            final UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
            final EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
            
            // 只处理Orders实体集
            if (!edmEntitySet.getName().equals("Orders")) {
                throw new ODataApplicationException("Only Orders entity set is supported",
                        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
            }

            // 读取请求体
            InputStream requestInputStream = request.getBody();
            ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
            DeserializerResult deserializerResult = deserializer.entity(requestInputStream, 
                    edmEntitySet.getEntityType());
            Entity requestEntity = deserializerResult.getEntity();

            // 转换为Order对象
            Order order = new Order();
            
            // 获取属性值，添加空值检查和类型转换
            for (Property property : requestEntity.getProperties()) {
                String propertyName = property.getName();
                Object value = property.getValue();
                
                if (value != null) {
                    switch (propertyName) {
                        case "OrderNumber":
                            order.setOrderNumber((String) value);
                            break;
                        case "Status":
                            order.setStatus(OrderStatus.valueOf((String) value));
                            break;
                        case "CustomerName":
                            order.setCustomerName((String) value);
                            break;
                        case "CustomerEmail":
                            order.setCustomerEmail((String) value);
                            break;
                        case "CustomerPhone":
                            order.setCustomerPhone((String) value);
                            break;
                        case "Notes":
                            order.setNotes((String) value);
                            break;
                        case "Items":
                            if (value instanceof List) {
                                @SuppressWarnings("unchecked")
                                List<Entity> itemEntities = (List<Entity>) value;
                                for (Entity itemEntity : itemEntities) {
                                    OrderItem item = new OrderItem();
                                    for (Property itemProp : itemEntity.getProperties()) {
                                        String itemPropName = itemProp.getName();
                                        Object itemValue = itemProp.getValue();
                                        if (itemValue != null) {
                                            switch (itemPropName) {
                                                case "ProductCode":
                                                    item.setProductCode((String) itemValue);
                                                    break;
                                                case "ProductName":
                                                    item.setProductName((String) itemValue);
                                                    break;
                                                case "UnitPrice":
                                                    item.setUnitPrice(new BigDecimal(itemValue.toString()));
                                                    break;
                                                case "Quantity":
                                                    item.setQuantity(Integer.valueOf(itemValue.toString()));
                                                    break;
                                                case "Notes":
                                                    item.setNotes((String) itemValue);
                                                    break;
                                            }
                                        }
                                    }
                                    order.addItem(item);
                                }
                            }
                            break;
                    }
                }
            }

            // 保存订单
            order = orderRepository.save(order);

            // 转换为OData实体
            Entity createdEntity = entityConverter.convertOrder(order);

            // 创建响应
            ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
            EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();
            SerializerResult serializerResult = odata.createSerializer(responseFormat)
                    .entity(serviceMetadata, edmEntitySet.getEntityType(), createdEntity, options);

            response.setContent(serializerResult.getContent());
            response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
            
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            throw new ODataApplicationException("Error creating order: " + e.getMessage(),
                    HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
        }
    }

    @Override
    @Transactional
    public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
                           ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
        try {
            // 获取实体集和ID
            final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
            final UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
            final EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
            
            List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
            Long id = Long.parseLong(keyPredicates.get(0).getText());

            // 只处理Orders实体集
            if (!edmEntitySet.getName().equals("Orders")) {
                throw new ODataApplicationException("Only Orders entity set is supported",
                        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
            }

            // 读取请求体
            InputStream requestInputStream = request.getBody();
            ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
            DeserializerResult deserializerResult = deserializer.entity(requestInputStream, 
                    edmEntitySet.getEntityType());
            Entity requestEntity = deserializerResult.getEntity();

            // 获取现有订单
            Order order = orderRepository.findByIdWithItems(id)
                    .orElseThrow(() -> new ODataApplicationException("Order not found",
                            HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH));

            // 更新订单信息
            order.setOrderNumber((String) requestEntity.getProperty("OrderNumber").getValue());
            order.setStatus(OrderStatus.valueOf((String) requestEntity.getProperty("Status").getValue()));
            order.setCustomerName((String) requestEntity.getProperty("CustomerName").getValue());
            order.setCustomerEmail((String) requestEntity.getProperty("CustomerEmail").getValue());
            order.setCustomerPhone((String) requestEntity.getProperty("CustomerPhone").getValue());
            order.setNotes((String) requestEntity.getProperty("Notes").getValue());

            // 保存更新
            order = orderRepository.save(order);

            response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
        } catch (Exception e) {
            log.error("Error updating order: {}", e.getMessage(), e);
            throw new ODataApplicationException("Error updating order: " + e.getMessage(),
                    HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
        }
    }

    @Override
    public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo) 
            throws ODataApplicationException, ODataLibraryException {
        // 获取实体集和ID
        final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        final UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        final EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        
        List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
        Long id = Long.parseLong(keyPredicates.get(0).getText());

        // 只处理Orders实体集
        if (!edmEntitySet.getName().equals("Orders")) {
            throw new ODataApplicationException("Only Orders entity set is supported",
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }

        // 检查订单是否存在
        if (!orderRepository.existsById(id)) {
            throw new ODataApplicationException("Order not found",
                    HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
        }

        // 删除订单
        orderRepository.deleteById(id);

        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }

    @Override
    public void processActionEntity(ODataRequest request, ODataResponse response, 
            UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        
        try {
            // 获取 action 信息
            List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
            if (resourcePaths == null || resourcePaths.isEmpty()) {
                throw new ODataApplicationException(
                    "Invalid action request",
                    HttpStatusCode.BAD_REQUEST.getStatusCode(),
                    Locale.ENGLISH);
            }

            UriResource uriResource = resourcePaths.get(0);
            if (!(uriResource instanceof UriResourceAction)) {
                throw new ODataApplicationException(
                    "Invalid resource type",
                    HttpStatusCode.BAD_REQUEST.getStatusCode(),
                    Locale.ENGLISH);
            }

            UriResourceAction uriAction = (UriResourceAction) uriResource;
            String actionName = uriAction.getAction().getName();
            
            // 获取 Orders EntitySet
            EdmEntitySet edmEntitySet = serviceMetadata.getEdm()
                .getEntityContainer()
                .getEntitySet(EdmProvider.ES_ORDERS_NAME);

            // 从请求体中获取参数
            InputStream requestInputStream = request.getBody();
            ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
            DeserializerResult result = deserializer.entity(requestInputStream, 
                edmEntitySet.getEntityType());
            Entity requestEntity = result.getEntity();
            
            // 获取 orderId 参数
            Property orderIdProperty = requestEntity.getProperty("orderId");
            if (orderIdProperty == null || orderIdProperty.getValue() == null) {
                throw new ODataApplicationException(
                    "Missing orderId parameter",
                    HttpStatusCode.BAD_REQUEST.getStatusCode(),
                    Locale.ENGLISH);
            }
            Long orderId = Long.parseLong(orderIdProperty.getValue().toString());

            Entity resultEntity = null;
            
            if (actionName.equals(EdmProvider.ACTION_AUDIT_ORDER.getName())) {
                Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ODataApplicationException(
                        "Order not found", 
                        HttpStatusCode.NOT_FOUND.getStatusCode(), 
                        Locale.ENGLISH));

                // 检查订单状态
                if (order.getStatus() != OrderStatus.DRAFT) {
                    throw new ODataApplicationException(
                        "只有草稿状态的订单可以审核",
                        HttpStatusCode.BAD_REQUEST.getStatusCode(),
                        Locale.ENGLISH);
                }

                // 更新订单状态为待处理
                order.setStatus(OrderStatus.PENDING);
                order = orderRepository.save(order);
                
                // 转换为 OData 实体
                resultEntity = entityConverter.convertOrder(order);
                
                log.info("订单 {} 审核成功，状态更新为：{}", order.getOrderNumber(), order.getStatus());
            } 
            else if (actionName.equals(EdmProvider.ACTION_GET_PREV_ORDER.getName()) || 
                     actionName.equals(EdmProvider.ACTION_GET_NEXT_ORDER.getName())) {
                List<Order> orders = orderRepository.findAllWithItems();
                int currentIndex = -1;
                
                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).getId().equals(orderId)) {
                        currentIndex = i;
                        break;
                    }
                }

                if (currentIndex == -1) {
                    throw new ODataApplicationException(
                        "Order not found",
                        HttpStatusCode.NOT_FOUND.getStatusCode(),
                        Locale.ENGLISH);
                }

                int targetIndex = actionName.equals(EdmProvider.ACTION_GET_PREV_ORDER.getName()) 
                    ? currentIndex - 1 
                    : currentIndex + 1;

                if (targetIndex < 0 || targetIndex >= orders.size()) {
                    throw new ODataApplicationException(
                        "No " + (targetIndex < 0 ? "previous" : "next") + " order available",
                        HttpStatusCode.NOT_FOUND.getStatusCode(),
                        Locale.ENGLISH);
                }

                resultEntity = entityConverter.convertOrder(orders.get(targetIndex));
            }

            if (resultEntity != null) {
                // 序列化结果
                ContextURL contextUrl = ContextURL.with()
                    .entitySet(edmEntitySet)
                    .suffix(ContextURL.Suffix.ENTITY)
                    .build();
                EntitySerializerOptions options = EntitySerializerOptions.with()
                    .contextURL(contextUrl)
                    .build();

                ODataSerializer serializer = odata.createSerializer(responseFormat);
                SerializerResult serializerResult = serializer.entity(
                    serviceMetadata, 
                    edmEntitySet.getEntityType(), 
                    resultEntity,
                    options);

                response.setContent(serializerResult.getContent());
                response.setStatusCode(HttpStatusCode.OK.getStatusCode());
                response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
            } else {
                response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
            }

        } catch (DeserializerException e) {
            throw new ODataApplicationException(
                "Error deserializing request body: " + e.getMessage(),
                HttpStatusCode.BAD_REQUEST.getStatusCode(),
                Locale.ENGLISH);
        }
        catch (Exception e) {
            throw new ODataApplicationException(
                e.getMessage(),
                HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
                Locale.ENGLISH);
        }
    }
} 