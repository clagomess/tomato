package io.github.clagomess.tomato.io.converter;

import io.github.clagomess.tomato.dto.data.MetadataDto;
import io.github.clagomess.tomato.enums.PostmanJsonSchemaEnum;
import io.github.clagomess.tomato.enums.TomatoJsonSchemaEnum;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;

public class JsonSchemaBuilder {
    private JsonSchemaBuilder(){}

    public static Schema getTomatoJsonSchema(TomatoJsonSchemaEnum schemaEnum) {
        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(
                SpecificationVersion.DRAFT_7
        );

        return registry.getSchema(MetadataDto.class.getResourceAsStream(
                schemaEnum.getResourceFile()
        ));
    }

    public static Schema getPostmanJsonSchema(PostmanJsonSchemaEnum schemaEnum){
        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(
                SpecificationVersion.DRAFT_7
        );

        return registry.getSchema(PostmanConverter.class.getResourceAsStream(
                schemaEnum.getResourceFile()
        ));
    }
}
