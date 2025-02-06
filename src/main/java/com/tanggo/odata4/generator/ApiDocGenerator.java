package com.tanggo.odata4.generator;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.springframework.stereotype.Component;

@Component
public class ApiDocGenerator {
    
    public OpenAPI generateApiDoc(String edmxPath) {
        try {
            CsdlSchema schema = loadSchema(edmxPath);
            return new OpenAPI()
                .info(new Info()
                    .title("OData Service API")
                    .version("1.0.0")
                    .description("Generated API documentation from OData metadata"))
                .paths(generatePaths(schema))
                .components(generateComponents(schema));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate API documentation", e);
        }
    }

    private Paths generatePaths(CsdlSchema schema) {
        Paths paths = new Paths();
        schema.getEntityTypes().forEach(entityType -> {
            paths.addPathItem("/" + entityType.getName(), generateEntityPaths(entityType));
        });
        return paths;
    }

    private Components generateComponents(CsdlSchema schema) {
        Components components = new Components();
        schema.getEntityTypes().forEach(entityType -> {
            components.addSchemas(entityType.getName(), generateEntitySchema(entityType));
        });
        return components;
    }

    // 实现文档生成方法...
} 