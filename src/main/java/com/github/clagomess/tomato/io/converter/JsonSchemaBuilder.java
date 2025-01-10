package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.dto.data.MetadataDto;
import com.github.clagomess.tomato.enums.PostmanJsonSchemaEnum;
import com.github.clagomess.tomato.enums.TomatoJsonSchemaEnum;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

public class JsonSchemaBuilder {
    private JsonSchemaBuilder(){}

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

        return factory.getSchema(PostmanConverter.class.getResourceAsStream(
                schemaEnum.getResourceFile()
        ));
    }
}
