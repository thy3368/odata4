package com.tanggo.odata4.config;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component("edmProvider")
public class EdmProvider extends CsdlAbstractEdmProvider {
    // Service Namespace
    public static final String NAMESPACE = "OData.Demo";

    // EDM Container
    public static final String CONTAINER_NAME = "Container";
    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    // Entity Types Names
    public static final String ET_ORDER_NAME = "Order";
    public static final FullQualifiedName ET_ORDER_FQN = new FullQualifiedName(NAMESPACE, ET_ORDER_NAME);

    public static final String ET_ORDER_ITEM_NAME = "OrderItem";
    public static final FullQualifiedName ET_ORDER_ITEM_FQN = new FullQualifiedName(NAMESPACE, ET_ORDER_ITEM_NAME);

    // Entity Set Names
    public static final String ES_ORDERS_NAME = "Orders";
    public static final String ES_ORDER_ITEMS_NAME = "OrderItems";

    // 添加操作定义
    public static final FullQualifiedName ACTION_AUDIT_ORDER = new FullQualifiedName(NAMESPACE, "AuditOrder");
    public static final FullQualifiedName ACTION_GET_PREV_ORDER = new FullQualifiedName(NAMESPACE, "GetPrevOrder");
    public static final FullQualifiedName ACTION_GET_NEXT_ORDER = new FullQualifiedName(NAMESPACE, "GetNextOrder");

    private final EntityCollectionProcessor orderCollectionProcessor;
    private final EntityProcessor orderEntityProcessor;
    private final BatchProcessor batchProcessor;

    public EdmProvider(EntityCollectionProcessor orderCollectionProcessor, EntityProcessor orderEntityProcessor, BatchProcessor batchProcessor) {
        this.orderCollectionProcessor = orderCollectionProcessor;
        this.orderEntityProcessor = orderEntityProcessor;
        this.batchProcessor = batchProcessor;
    }

    @Override
    public List<CsdlSchema> getSchemas() {
        try {
            CsdlSchema schema = new CsdlSchema();
            schema.setNamespace(NAMESPACE);

            List<CsdlEntityType> entityTypes = new ArrayList<>();
            entityTypes.add(getEntityType(ET_ORDER_FQN));
            entityTypes.add(getEntityType(ET_ORDER_ITEM_FQN));
            schema.setEntityTypes(entityTypes);

            schema.setEntityContainer(getEntityContainer());

            return Collections.singletonList(schema);
        } catch (Exception e) {
            throw new RuntimeException("Error creating schema", e);
        }
    }

    @Override
    public CsdlEntityContainer getEntityContainer() {
        try {
            List<CsdlEntitySet> entitySets = new ArrayList<>();
            entitySets.add(getEntitySet(CONTAINER, ES_ORDERS_NAME));
            entitySets.add(getEntitySet(CONTAINER, ES_ORDER_ITEMS_NAME));

            CsdlEntityContainer entityContainer = new CsdlEntityContainer();
            entityContainer.setName(CONTAINER_NAME);
            entityContainer.setEntitySets(entitySets);

            return entityContainer;
        } catch (Exception e) {
            throw new RuntimeException("Error creating entity container", e);
        }
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {
        if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
            return new CsdlEntityContainerInfo().setContainerName(CONTAINER);
        }
        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {
        if (entityContainer.equals(CONTAINER)) {
            if (entitySetName.equals(ES_ORDERS_NAME)) {
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_ORDERS_NAME);
                entitySet.setType(ET_ORDER_FQN);
                return entitySet;
            }
            if (entitySetName.equals(ES_ORDER_ITEMS_NAME)) {
                CsdlEntitySet entitySet = new CsdlEntitySet();
                entitySet.setName(ES_ORDER_ITEMS_NAME);
                entitySet.setType(ET_ORDER_ITEM_FQN);
                return entitySet;
            }
        }
        return null;
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
        if (entityTypeName.equals(ET_ORDER_FQN)) {
            CsdlEntityType entityType = new CsdlEntityType();
            entityType.setName(ET_ORDER_NAME);

            List<CsdlProperty> properties = new ArrayList<>();
            properties.add(new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()).setNullable(false));
            properties.add(new CsdlProperty().setName("OrderDate").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()).setNullable(true));

            entityType.setProperties(properties);

            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName("Id");
            entityType.setKey(Collections.singletonList(propertyRef));

            return entityType;
        } else if (entityTypeName.equals(ET_ORDER_ITEM_FQN)) {
            CsdlProperty id = new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());

            CsdlProperty productCode = new CsdlProperty().setName("ProductCode").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            CsdlProperty productName = new CsdlProperty().setName("ProductName").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            CsdlProperty unitPrice = new CsdlProperty().setName("UnitPrice").setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName()).setPrecision(19).setScale(4);

            CsdlProperty quantity = new CsdlProperty().setName("Quantity").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

            CsdlProperty subtotal = new CsdlProperty().setName("Subtotal").setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName()).setPrecision(19).setScale(4);

            CsdlProperty notes = new CsdlProperty().setName("Notes").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(true);

            // Navigation Property back to Order
            CsdlNavigationProperty order = new CsdlNavigationProperty().setName("Order").setType(ET_ORDER_FQN).setNullable(false).setPartner("Items");

            List<CsdlProperty> entityTypeProperties = Arrays.asList(id, productCode, productName, unitPrice, quantity, subtotal, notes);

            return new CsdlEntityType().setName(ET_ORDER_ITEM_NAME).setProperties(entityTypeProperties).setNavigationProperties(Collections.singletonList(order)).setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));
        }

        return null;
    }

    @Override
    public List<CsdlAction> getActions(FullQualifiedName actionName) {
        if (actionName.equals(ACTION_AUDIT_ORDER)) {
            // 审核订单操作
            CsdlParameter bindingParameter = new CsdlParameter().setName("orderId").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName()).setNullable(false);

            return Collections.singletonList(new CsdlAction().setName(ACTION_AUDIT_ORDER.getName()).setParameters(Collections.singletonList(bindingParameter)).setReturnType(new CsdlReturnType().setType(ET_ORDER_FQN)));
        }
        if (actionName.equals(ACTION_GET_PREV_ORDER) || actionName.equals(ACTION_GET_NEXT_ORDER)) {
            // 获取前一个或后一个订单操作
            CsdlParameter bindingParameter = new CsdlParameter().setName("orderId").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName()).setNullable(false);

            return Collections.singletonList(new CsdlAction().setName(actionName.getName()).setParameters(Collections.singletonList(bindingParameter)).setReturnType(new CsdlReturnType().setType(ET_ORDER_FQN)));
        }
        return null;
    }

    public void registerProcessors(OData odata, ServiceMetadata serviceMetadata) {
        orderCollectionProcessor.init(odata, serviceMetadata);
        orderEntityProcessor.init(odata, serviceMetadata);
        batchProcessor.init(odata, serviceMetadata);
    }
}
