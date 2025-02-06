package com.tanggo.odata4.generator;

import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.springframework.stereotype.Component;

@Component
public class ValidationGenerator {
    
    public void generateValidation(String edmxPath) {
        try {
            CsdlSchema schema = loadSchema(edmxPath);
            generateValidators(schema);
            generateConstraints(schema);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate validation", e);
        }
    }

    private void generateValidators(CsdlSchema schema) {
        schema.getEntityTypes().forEach(entityType -> {
            String validatorCode = generateValidatorCode(entityType);
            writeToFile(validatorCode, entityType.getName() + "Validator.java");
        });
    }

    private void generateConstraints(CsdlSchema schema) {
        schema.getEntityTypes().forEach(entityType -> {
            String constraintsCode = generateConstraintsCode(entityType);
            writeToFile(constraintsCode, entityType.getName() + "Constraints.java");
        });
    }

    // 实现验证生成方法...
} 