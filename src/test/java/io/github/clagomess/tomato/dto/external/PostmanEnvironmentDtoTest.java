package io.github.clagomess.tomato.dto.external;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import io.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static io.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.ENVIRONMENT;

class PostmanEnvironmentDtoTest {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getPostmanJsonSchema(ENVIRONMENT);

    @Test
    void schema() throws IOException {
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
