package com.tanggo.odata4.service;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.*;
import org.apache.olingo.server.api.uri.queryoption.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class QueryOptionsHandler {

    public EntityCollection applyQueryOptions(
            EntityCollection entityCollection,
            UriInfo uriInfo) throws ODataApplicationException {
        
        List<Entity> entities = entityCollection.getEntities();

        // 应用 $filter
        FilterOption filterOption = uriInfo.getFilterOption();
        if (filterOption != null) {
            entities = applyFilter(entities, filterOption);
        }

        // 应用 $orderby
        OrderByOption orderByOption = uriInfo.getOrderByOption();
        if (orderByOption != null) {
            entities = applyOrderBy(entities, orderByOption);
        }

        // 应用 $skip 和 $top
        SkipOption skipOption = uriInfo.getSkipOption();
        TopOption topOption = uriInfo.getTopOption();
        entities = applyPaging(entities, skipOption, topOption);

        EntityCollection result = new EntityCollection();
        result.getEntities().addAll(entities);
        return result;
    }

    private List<Entity> applyFilter(List<Entity> entities, FilterOption filterOption) 
            throws ODataApplicationException {
        FilterExpression filterExpression = filterOption.getExpression();
        return entities.stream()
                .filter(entity -> evaluateFilterExpression(entity, filterExpression))
                .collect(Collectors.toList());
    }

    private boolean evaluateFilterExpression(Entity entity, FilterExpression filterExpression) {
        // 实现过滤表达式评估逻辑
        return true; // 临时返回，需要实现具体逻辑
    }

    private List<Entity> applyOrderBy(List<Entity> entities, OrderByOption orderByOption) {
        List<OrderByItem> items = orderByOption.getOrders();
        // 实现排序逻辑
        return entities;
    }

    private List<Entity> applyPaging(List<Entity> entities, SkipOption skipOption, TopOption topOption) {
        int skip = skipOption != null ? skipOption.getValue() : 0;
        int top = topOption != null ? topOption.getValue() : entities.size();

        return entities.stream()
                .skip(skip)
                .limit(top)
                .collect(Collectors.toList());
    }
} 