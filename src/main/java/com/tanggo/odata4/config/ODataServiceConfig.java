package com.tanggo.odata4.config;

import com.tanggo.odata4.processor.OrderEntityCollectionProcessor;
import com.tanggo.odata4.processor.OrderEntityProcessor;
import com.tanggo.odata4.repository.OrderRepository;
import com.tanggo.odata4.service.ODataEntityConverter;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class ODataServiceConfig {

    @Bean
    public OData odata() {
        return OData.newInstance();
    }

    @Bean
    public ServiceMetadata serviceMetadata(EdmProvider edmProvider) {
        return odata().createServiceMetadata(edmProvider, new ArrayList<EdmxReference>());
    }

    @Bean
    public ODataHttpHandler oDataHttpHandler(OData odata, ServiceMetadata serviceMetadata) {
        return odata.createHandler(serviceMetadata);
    }

    @Bean
    public EntityCollectionProcessor entityCollectionProcessor(
            OrderRepository orderRepository, 
            ODataEntityConverter entityConverter) {
        return new OrderEntityCollectionProcessor(orderRepository, entityConverter);
    }

    @Bean
    public EntityProcessor entityProcessor(
            OrderRepository orderRepository, 
            ODataEntityConverter entityConverter) {
        return new OrderEntityProcessor(orderRepository, entityConverter);
    }
}
