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
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Component
public class OrderEntityProcessor implements EntityProcessor {
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
} 