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
    public static final String ET_ORDER_ITEM_NAME = "OrderItem";
    public static final String ET_USER_NAME = "User";
    public static final String ET_PRODUCT_NAME = "Product";

    public static final FullQualifiedName ET_ORDER_FQN = new FullQualifiedName(NAMESPACE, ET_ORDER_NAME);
    public static final FullQualifiedName ET_ORDER_ITEM_FQN = new FullQualifiedName(NAMESPACE, ET_ORDER_ITEM_NAME);
    public static final FullQualifiedName ET_USER_FQN = new FullQualifiedName(NAMESPACE, ET_USER_NAME);
    public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

    // Entity Set Names
    public static final String ES_ORDERS_NAME = "Orders";
    public static final String ES_ORDER_ITEMS_NAME = "OrderItems";
    public static final String ES_USERS_NAME = "Users";
    public static final String ES_PRODUCTS_NAME = "Products";

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
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);

        List<CsdlEntityType> entityTypes = new ArrayList<>();
        entityTypes.add(getEntityType(ET_ORDER_FQN));
        entityTypes.add(getEntityType(ET_ORDER_ITEM_FQN));
        entityTypes.add(getEntityType(ET_USER_FQN));
        entityTypes.add(getEntityType(ET_PRODUCT_FQN));
        schema.setEntityTypes(entityTypes);

        schema.setEntityContainer(getEntityContainer());

        return Collections.singletonList(schema);
    }

    @Override
    public CsdlEntityContainer getEntityContainer() {
        List<CsdlEntitySet> entitySets = Arrays.asList(
            getEntitySet(CONTAINER, ES_ORDERS_NAME),
            getEntitySet(CONTAINER, ES_ORDER_ITEMS_NAME),
            getEntitySet(CONTAINER, ES_USERS_NAME),
            getEntitySet(CONTAINER, ES_PRODUCTS_NAME)
        );

        return new CsdlEntityContainer()
            .setName(CONTAINER_NAME)
            .setEntitySets(entitySets);
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
        if (!entityContainer.equals(CONTAINER)) {
            return null;
        }

        switch (entitySetName) {
            case ES_ORDERS_NAME:
                return new CsdlEntitySet()
                    .setName(ES_ORDERS_NAME)
                    .setType(ET_ORDER_FQN)
                    .setNavigationPropertyBindings(Arrays.asList(
                        new CsdlNavigationPropertyBinding().setPath("Items").setTarget(ES_ORDER_ITEMS_NAME),
                        new CsdlNavigationPropertyBinding().setPath("User").setTarget(ES_USERS_NAME)
                    ));

            case ES_ORDER_ITEMS_NAME:
                return new CsdlEntitySet()
                    .setName(ES_ORDER_ITEMS_NAME)
                    .setType(ET_ORDER_ITEM_FQN)
                    .setNavigationPropertyBindings(Arrays.asList(
                        new CsdlNavigationPropertyBinding().setPath("Order").setTarget(ES_ORDERS_NAME),
                        new CsdlNavigationPropertyBinding().setPath("Product").setTarget(ES_PRODUCTS_NAME)
                    ));

            case ES_USERS_NAME:
                return new CsdlEntitySet()
                    .setName(ES_USERS_NAME)
                    .setType(ET_USER_FQN)
                    .setNavigationPropertyBindings(Collections.singletonList(
                        new CsdlNavigationPropertyBinding().setPath("Orders").setTarget(ES_ORDERS_NAME)
                    ));

            case ES_PRODUCTS_NAME:
                return new CsdlEntitySet()
                    .setName(ES_PRODUCTS_NAME)
                    .setType(ET_PRODUCT_FQN);

            default:
                return null;
        }
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
        if (entityTypeName == null) {
            return null;
        }

        switch (entityTypeName.getName()) {
            case ET_ORDER_NAME:
                return new CsdlEntityType()
                    .setName(ET_ORDER_NAME)
                    .setProperties(Arrays.asList(
                        new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName()),
                        new CsdlProperty().setName("OrderNumber").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                        new CsdlProperty().setName("OrderDate").setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName()),
                        new CsdlProperty().setName("TotalAmount").setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName()),
                        new CsdlProperty().setName("Status").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    ))
                    .setNavigationProperties(Arrays.asList(
                        new CsdlNavigationProperty().setName("Items").setType(ET_ORDER_ITEM_FQN).setCollection(true),
                        new CsdlNavigationProperty().setName("User").setType(ET_USER_FQN)
                    ))
                    .setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));

            case ET_ORDER_ITEM_NAME:
                return new CsdlEntityType()
                    .setName(ET_ORDER_ITEM_NAME)
                    .setProperties(Arrays.asList(
                        new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName()),
                        new CsdlProperty().setName("Quantity").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()),
                        new CsdlProperty().setName("UnitPrice").setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName())
                    ))
                    .setNavigationProperties(Arrays.asList(
                        new CsdlNavigationProperty().setName("Order").setType(ET_ORDER_FQN),
                        new CsdlNavigationProperty().setName("Product").setType(ET_PRODUCT_FQN)
                    ))
                    .setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));

            case ET_USER_NAME:
                return new CsdlEntityType()
                    .setName(ET_USER_NAME)
                    .setProperties(Arrays.asList(
                        new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName()),
                        new CsdlProperty().setName("Username").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                        new CsdlProperty().setName("Email").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                        new CsdlProperty().setName("Phone").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                        new CsdlProperty().setName("Address").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    ))
                    .setNavigationProperties(Collections.singletonList(
                        new CsdlNavigationProperty().setName("Orders").setType(ET_ORDER_FQN).setCollection(true)
                    ))
                    .setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));

            case ET_PRODUCT_NAME:
                return new CsdlEntityType()
                    .setName(ET_PRODUCT_NAME)
                    .setProperties(Arrays.asList(
                        new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName()),
                        new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                        new CsdlProperty().setName("Description").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                        new CsdlProperty().setName("Price").setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName()),
                        new CsdlProperty().setName("Stock").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()),
                        new CsdlProperty().setName("ImageUrl").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    ))
                    .setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));

            default:
                return null;
        }
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
