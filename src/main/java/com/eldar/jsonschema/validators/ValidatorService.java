package com.eldar.jsonschema.validators;

import com.eldar.jsonschema.exception.UnProcessableObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import com.eldar.jsonschema.exception.LoadingFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ValidatorService {
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    protected static final JsonSchemaFactory FACTORY = JsonSchemaFactory
            .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909))
            .addMetaSchema(JsonMetaSchema.getV4())
            .addMetaSchema(JsonMetaSchema.getV6())
            .addMetaSchema(JsonMetaSchema.getV7())
            .addMetaSchema(JsonMetaSchema.getV201909())
            .build();

    private final Map<String, JsonSchema> schemaCache;

    public ValidatorService() {
        schemaCache = new ConcurrentHashMap<>();
    }

    public void validate(String schemaAsString, Object payload) throws JsonSchemaException {
        log.debug("Validating using schema: {}", schemaAsString);

        JsonSchema schema = buildSchemaFromString(schemaAsString);
        validate(schema, objectMapper.valueToTree(payload));
    }

    private void validate(JsonSchema schema, JsonNode payload) {
        Set<ValidationMessage> validationResults = schema.validate(payload);
        if (CollectionUtils.isEmpty(validationResults)) {
            return;
        }
        log.warn("Validation Error: {}", validationResults);
        throw new UnProcessableObject(validationResults, "Validation Error: ");
    }

    private JsonSchema buildSchemaFromString(String schemaAsString) {
        return schemaCache.computeIfAbsent(schemaAsString, newSchema -> {
            try {
                SchemaValidatorsConfig config = new SchemaValidatorsConfig();
                config.setTypeLoose(true);
                config.setFailFast(false);
                return FACTORY.getSchema(schemaAsString, config);
            } catch (Exception e) {
                log.warn("Failed to load schema: {}", schemaAsString, e);
                throw new LoadingFailedException("Failed to load schema: " + schemaAsString, e);
            }
        });
    }
}
