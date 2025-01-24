package com.tanggo.odata4.config;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class OrderEdmProvider extends CsdlAbstractEdmProvider {

    // Service Namespace
    public static final String NAMESPACE = "com.tanggo.odata4";
    
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

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        if (entityTypeName.equals(ET_ORDER_FQN)) {
            return new CsdlEntityType()
                    .setName(ET_ORDER_NAME)
                    .setProperties(Arrays.asList(
                            new CsdlProperty().setName("Id")
                                    .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName()),
                            new CsdlProperty().setName("OrderNumber")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("Status")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("CustomerName")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("CustomerEmail")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("CustomerPhone")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("TotalAmount")
                                    .setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName()),
                            new CsdlProperty().setName("Notes")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    ))
                    .setNavigationProperties(Collections.singletonList(
                            new CsdlNavigationProperty()
                                    .setName("Items")
                                    .setType(ET_ORDER_ITEM_FQN)
                                    .setCollection(true)
                                    .setPartner("Order")
                    ))
                    .setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));
        } else if (entityTypeName.equals(ET_ORDER_ITEM_FQN)) {
            return new CsdlEntityType()
                    .setName(ET_ORDER_ITEM_NAME)
                    .setProperties(Arrays.asList(
                            new CsdlProperty().setName("Id")
                                    .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName()),
                            new CsdlProperty().setName("ProductCode")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("ProductName")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("UnitPrice")
                                    .setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName()),
                            new CsdlProperty().setName("Quantity")
                                    .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName()),
                            new CsdlProperty().setName("Subtotal")
                                    .setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName()),
                            new CsdlProperty().setName("Notes")
                                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                    ))
                    .setNavigationProperties(Collections.singletonList(
                            new CsdlNavigationProperty()
                                    .setName("Order")
                                    .setType(ET_ORDER_FQN)
                                    .setCollection(false)
                                    .setPartner("Items")
                    ))
                    .setKey(Collections.singletonList(new CsdlPropertyRef().setName("Id")));
        }
        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        if (entityContainer.equals(CONTAINER)) {
            if (entitySetName.equals(ES_ORDERS_NAME)) {
                return new CsdlEntitySet()
                        .setName(ES_ORDERS_NAME)
                        .setType(ET_ORDER_FQN)
                        .setNavigationPropertyBindings(Collections.singletonList(
                                new CsdlNavigationPropertyBinding()
                                        .setPath("Items")
                                        .setTarget(ES_ORDER_ITEMS_NAME)
                        ));
            } else if (entitySetName.equals(ES_ORDER_ITEMS_NAME)) {
                return new CsdlEntitySet()
                        .setName(ES_ORDER_ITEMS_NAME)
                        .setType(ET_ORDER_ITEM_FQN)
                        .setNavigationPropertyBindings(Collections.singletonList(
                                new CsdlNavigationPropertyBinding()
                                        .setPath("Order")
                                        .setTarget(ES_ORDERS_NAME)
                        ));
            }
        }
        return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
        List<CsdlEntitySet> entitySets = new ArrayList<>();
        entitySets.add(getEntitySet(CONTAINER, ES_ORDERS_NAME));
        entitySets.add(getEntitySet(CONTAINER, ES_ORDER_ITEMS_NAME));
        
        return new CsdlEntityContainer()
                .setName(CONTAINER_NAME)
                .setEntitySets(entitySets);
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
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
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
        if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
            return new CsdlEntityContainerInfo()
                    .setContainerName(CONTAINER);
        }
        return null;
    }
} 