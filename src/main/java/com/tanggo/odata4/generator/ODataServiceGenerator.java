package com.tanggo.odata4.generator;

import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.springframework.stereotype.Component;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ODataServiceGenerator {
    
    public void generateFromMetadata(String edmxPath) {
        try (InputStream is = Files.newInputStream(Path.of(edmxPath))) {
            JAXBContext context = JAXBContext.newInstance(CsdlSchema.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            CsdlSchema schema = (CsdlSchema) unmarshaller.unmarshal(is);
            
            generateEntityProcessors(schema);
            generateRepositories(schema);
            generateServices(schema);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate OData service", e);
        }
    }

    private void generateEntityProcessors(CsdlSchema schema) {
        // 为每个实体类型生成处理器
        schema.getEntityTypes().forEach(entityType -> {
            String processorCode = generateProcessorCode(entityType);
            writeToFile(processorCode, entityType.getName() + "Processor.java");
        });
    }

    private void generateRepositories(CsdlSchema schema) {
        // 为每个实体类型生成仓库
        schema.getEntityTypes().forEach(entityType -> {
            String repositoryCode = generateRepositoryCode(entityType);
            writeToFile(repositoryCode, entityType.getName() + "Repository.java");
        });
    }

    private void generateServices(CsdlSchema schema) {
        // 为每个实体类型生成服务
        schema.getEntityTypes().forEach(entityType -> {
            String serviceCode = generateServiceCode(entityType);
            writeToFile(serviceCode, entityType.getName() + "Service.java");
        });
    }

    // 实现代码生成方法...
} 