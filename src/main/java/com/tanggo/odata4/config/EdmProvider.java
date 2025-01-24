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
                                        .setTarget("OrderItems")));
            }
        }
        return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() {
        List<CsdlEntitySet> entitySets = new ArrayList<>();
        entitySets.add(getEntitySet(CONTAINER, ES_ORDERS_NAME));
        
        return new CsdlEntityContainer()
                .setName(CONTAINER_NAME)
                .setEntitySets(entitySets);
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