package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.MetadataDto;
import com.github.clagomess.tomato.enums.PostmanJsonSchemaEnum;
import com.github.clagomess.tomato.enums.TomatoJsonSchemaEnum;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

public class JsonSchemaService {
    private JsonSchemaService(){}

    public static JsonSchema getTomatoJsonSchema(TomatoJsonSchemaEnum schemaEnum) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(
                SpecVersion.VersionFlag.V7
        );

        return factory.getSchema(MetadataDto.class.getResourceAsStream(
                schemaEnum.getResourceFile()
        ));
    }

    public static JsonSchema getPostmanJsonSchema(PostmanJsonSchemaEnum schemaEnum){
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(
                SpecVersion.VersionFlag.V4
        );

        return factory.getSchema(DumpPumpService.class.getResourceAsStream(
                schemaEnum.getResourceFile()
        ));
    }
}
