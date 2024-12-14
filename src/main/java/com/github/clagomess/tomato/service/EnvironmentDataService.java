package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentDataService {
    private EnvironmentDataService() {}
    private static final EnvironmentDataService instance = new EnvironmentDataService();
    public synchronized static EnvironmentDataService getInstance(){
        return instance;
    }

    private final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(
            SpecVersion.VersionFlag.V7
    );

    @Getter
    private final JsonSchema jsonSchema = factory.getSchema(
            EnvironmentDto.class.getResourceAsStream(
                    "environment.schema.json"
            )
    );
}
