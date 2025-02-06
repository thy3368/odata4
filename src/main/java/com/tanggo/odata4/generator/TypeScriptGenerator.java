package com.tanggo.odata4.generator;

import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.springframework.stereotype.Component;

@Component
public class TypeScriptGenerator {
    
    public void generateFromMetadata(String edmxPath) {
        try {
            CsdlSchema schema = loadSchema(edmxPath);
            generateInterfaces(schema);
            generateApiClient(schema);
            generateHooks(schema);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TypeScript code", e);
        }
    }

    private void generateInterfaces(CsdlSchema schema) {
        StringBuilder code = new StringBuilder();
        code.append("// Generated TypeScript Interfaces\n\n");

        schema.getEntityTypes().forEach(entityType -> {
            code.append(generateInterface(entityType));
            code.append("\n\n");
        });

        writeToFile(code.toString(), "models.ts");
    }

    private void generateApiClient(CsdlSchema schema) {
        StringBuilder code = new StringBuilder();
        code.append("// Generated API Client\n\n");
        code.append("import { request } from 'umi';\n");
        code.append("import type { TableParams } from './models';\n\n");

        schema.getEntityTypes().forEach(entityType -> {
            code.append(generateApiMethods(entityType));
            code.append("\n\n");
        });

        writeToFile(code.toString(), "api.ts");
    }

    private void generateHooks(CsdlSchema schema) {
        StringBuilder code = new StringBuilder();
        code.append("// Generated React Hooks\n\n");
        code.append("import { useRequest } from 'ahooks';\n");
        code.append("import * as api from './api';\n\n");

        schema.getEntityTypes().forEach(entityType -> {
            code.append(generateHook(entityType));
            code.append("\n\n");
        });

        writeToFile(code.toString(), "hooks.ts");
    }

    // 实现代码生成方法...
} 