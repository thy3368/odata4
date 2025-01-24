package com.tanggo.odata4.processor;

import com.tanggo.odata4.model.Order;
import com.tanggo.odata4.repository.OrderRepository;
import com.tanggo.odata4.service.ODataEntityConverter;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;

import java.util.List;

@Component
public class OrderEntityCollectionProcessor implements EntityCollectionProcessor {
    private OData odata;
    private ServiceMetadata serviceMetadata;
    private final OrderRepository orderRepository;
    private final ODataEntityConverter entityConverter;

    public OrderEntityCollectionProcessor(OrderRepository orderRepository, ODataEntityConverter entityConverter) {
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
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {
        
        // 获取实体集
        final UriResource firstResourceSegment = uriInfo.getUriResourceParts().get(0);
        final UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) firstResourceSegment;
        final EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        // 创建序列化选项
        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
        EntityCollectionSerializerOptions options = EntityCollectionSerializerOptions.with()
                .contextURL(contextUrl)
                .build();

        // 获取序列化器
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        // 获取实体集合数据
        EntityCollection entityCollection = new EntityCollection();
        
        if (edmEntitySet.getName().equals("Orders")) {
            List<Order> orders = orderRepository.findAllWithItems();
            entityCollection.getEntities().addAll(
                entityConverter.convertOrders(orders)
            );
        }

        // 序列化结果
        SerializerResult serializerResult = serializer.entityCollection(serviceMetadata,
                edmEntitySet.getEntityType(), entityCollection, options);

        // 设置响应
        response.setContent(serializerResult.getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }
} 