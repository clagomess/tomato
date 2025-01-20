package com.github.clagomess.tomato.dto.external;

import com.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static com.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.ENVIRONMENT;

public class PostmanEnvironmentDtoTest {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getPostmanJsonSchema(ENVIRONMENT);

    @Test
    public void schema() throws IOException {
        var url = getClass().getResourceAsStream(String.format(
                "PostmanEnvironmentDtoTest/%s",
                "postman.environment.json"
        ));
        var result = mapper.readValue(url, PostmanEnvironmentDto.class);

        // dump validate
        var resultDump = mapper.writeValueAsString(result);

        Set<ValidationMessage> validations = jsonSchema.validate(
                mapper.readTree(resultDump)
        );

        Assertions.assertThat(validations).isEmpty();
    }
}
