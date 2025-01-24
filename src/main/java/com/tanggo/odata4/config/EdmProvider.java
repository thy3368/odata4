package com.tanggo.odata4.config;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
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
    public static final FullQualifiedName ACTION_AUDIT_ORDER = 
        new FullQualifiedName(NAMESPACE, "AuditOrder");
    public static final FullQualifiedName ACTION_GET_PREV_ORDER = 
        new FullQualifiedName(NAMESPACE, "GetPrevOrder");
    public static final FullQualifiedName ACTION_GET_NEXT_ORDER = 
        new FullQualifiedName(NAMESPACE, "GetNextOrder");

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
        if (entityTypeName.equals(ET_ORDER_FQN)) {
            CsdlProperty id = new CsdlProperty()
                    .setName("Id")
                    .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());

            CsdlProperty orderNumber = new CsdlProperty()
                    .setName("OrderNumber")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            CsdlProperty status = new CsdlProperty()
                    .setName("Status")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            CsdlProperty customerName = new CsdlProperty()
                    .setName("CustomerName")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            CsdlProperty customerEmail = new CsdlProperty()
                    .setName("CustomerEmail")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    .setNullable(true);

            CsdlProperty customerPhone = new CsdlProperty()
                    .setName("CustomerPhone")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    .setNullable(true);

            CsdlProperty totalAmount = new CsdlProperty()
                    .setName("TotalAmount")
                    .setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName())
                    .setPrecision(19)
                    .setScale(4);

            CsdlProperty notes = new CsdlProperty()
                    .setName("Notes")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    .setNullable(true);

            // Navigation Property for Items
            CsdlNavigationProperty items = new CsdlNavigationProperty()
                    .setName("Items")
                    .setType(ET_ORDER_ITEM_FQN)
                    .setCollection(true)
                    .setPartner("Order");

            List<CsdlProperty> properties = Arrays.asList(id, orderNumber, status, 
                    customerName, customerEmail, customerPhone, totalAmount, notes);
            
            return new CsdlEntityType()
                    .setName(ET_ORDER_NAME)
                    .setProperties(properties)
                    .setNavigationProperties(Collections.singletonList(items))
                    .setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));
        }
        else if (entityTypeName.equals(ET_ORDER_ITEM_FQN)) {
            CsdlProperty id = new CsdlProperty()
                    .setName("Id")
                    .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());

            CsdlProperty productCode = new CsdlProperty()
                    .setName("ProductCode")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            CsdlProperty productName = new CsdlProperty()
                    .setName("ProductName")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            CsdlProperty unitPrice = new CsdlProperty()
                    .setName("UnitPrice")
                    .setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName())
                    .setPrecision(19)
                    .setScale(4);

            CsdlProperty quantity = new CsdlProperty()
                    .setName("Quantity")
                    .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

            CsdlProperty subtotal = new CsdlProperty()
                    .setName("Subtotal")
                    .setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName())
                    .setPrecision(19)
                    .setScale(4);

            CsdlProperty notes = new CsdlProperty()
                    .setName("Notes")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    .setNullable(true);

            // Navigation Property back to Order
            CsdlNavigationProperty order = new CsdlNavigationProperty()
                    .setName("Order")
                    .setType(ET_ORDER_FQN)
                    .setNullable(false)
                    .setPartner("Items");

            List<CsdlProperty> properties = Arrays.asList(id, productCode, productName, 
                    unitPrice, quantity, subtotal, notes);
            
            return new CsdlEntityType()
                    .setName(ET_ORDER_ITEM_NAME)
                    .setProperties(properties)
                    .setNavigationProperties(Collections.singletonList(order))
                    .setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));
        }

        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {
        if (entityContainer.equals(CONTAINER)) {
            if (entitySetName.equals(ES_ORDERS_NAME)) {
                return new CsdlEntitySet()
                        .setName(ES_ORDERS_NAME)
                        .setType(ET_ORDER_FQN)
                        .setNavigationPropertyBindings(Collections.singletonList(
                                new CsdlNavigationPropertyBinding()
                                        .setPath("Items")
                                        .setTarget(ES_ORDER_ITEMS_NAME)));
            }
            if (entitySetName.equals(ES_ORDER_ITEMS_NAME)) {
                return new CsdlEntitySet()
                        .setName(ES_ORDER_ITEMS_NAME)
                        .setType(ET_ORDER_ITEM_FQN)
                        .setNavigationPropertyBindings(Collections.singletonList(
                                new CsdlNavigationPropertyBinding()
                                        .setPath("Order")
                                        .setTarget(ES_ORDERS_NAME)));
            }
        }
        return null;
    }

    @Override
    public List<CsdlAction> getActions(FullQualifiedName actionName) {
        if (actionName.equals(ACTION_AUDIT_ORDER)) {
            // 审核订单操作
            CsdlParameter bindingParameter = new CsdlParameter()
                .setName("orderId")
                .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName())
                .setNullable(false);

            return Collections.singletonList(
                new CsdlAction()
                    .setName(ACTION_AUDIT_ORDER.getName())
                    .setParameters(Collections.singletonList(bindingParameter))
                    .setReturnType(new CsdlReturnType().setType(ET_ORDER_FQN))
            );
        }
        if (actionName.equals(ACTION_GET_PREV_ORDER) || 
            actionName.equals(ACTION_GET_NEXT_ORDER)) {
            // 获取前一个或后一个订单操作
            CsdlParameter bindingParameter = new CsdlParameter()
                .setName("orderId")
                .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName())
                .setNullable(false);

            return Collections.singletonList(
                new CsdlAction()
                    .setName(actionName.getName())
                    .setParameters(Collections.singletonList(bindingParameter))
                    .setReturnType(new CsdlReturnType().setType(ET_ORDER_FQN))
            );
        }
        return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() {
        List<CsdlActionImport> actions = Arrays.asList(
            new CsdlActionImport()
                .setName("AuditOrder")
                .setAction(ACTION_AUDIT_ORDER),
            new CsdlActionImport()
                .setName("GetPrevOrder")
                .setAction(ACTION_GET_PREV_ORDER),
            new CsdlActionImport()
                .setName("GetNextOrder")
                .setAction(ACTION_GET_NEXT_ORDER)
        );

        List<CsdlEntitySet> entitySets = Arrays.asList(
            getEntitySet(CONTAINER, ES_ORDERS_NAME),
            getEntitySet(CONTAINER, ES_ORDER_ITEMS_NAME)
        );

        return new CsdlEntityContainer()
            .setName(CONTAINER_NAME)
            .setEntitySets(entitySets)
            .setActionImports(actions);
    }

    @Override
    public List<CsdlSchema> getSchemas() {
        List<CsdlSchema> schemas = new ArrayList<>();
        
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);
        
        List<CsdlEntityType> entityTypes = new ArrayList<>();
        entityTypes.add(getEntityType(ET_ORDER_FQN));
        entityTypes.add(getEntityType(ET_ORDER_ITEM_FQN));
        schema.setEntityTypes(entityTypes);
        
        schema.setEntityContainer(getEntityContainer());
        
        schemas.add(schema);
        
        return schemas;
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {
        if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
            return new CsdlEntityContainerInfo()
                    .setContainerName(CONTAINER);
        }
        return null;
    }
} 