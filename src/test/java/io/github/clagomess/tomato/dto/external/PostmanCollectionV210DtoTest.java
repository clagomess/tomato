package io.github.clagomess.tomato.dto.external;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import io.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Set;

import static io.github.clagomess.tomato.enums.PostmanJsonSchemaEnum.COLLECTION;

@Slf4j
class PostmanCollectionV210DtoTest {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getPostmanJsonSchema(COLLECTION);

    @ParameterizedTest
    @ValueSource(strings = {
            "auth.postman.collection.v2.1.0.json",
            "without-auth.postman.collection.v2.1.0.json",
            "body-raw.postman.collection.v2.1.0.json",
            "formdata-file.postman.collection.v2.1.0.json",
            "queryparam.postman.collection.v2.1.0.json",
    })
    void pumpDumpTest(String filename) throws IOException {
        var url = getClass().getResourceAsStream(String.format(
                "PostmanCollectionV210DtoTest/%s",
                filename
        ));
        var result = mapper.readValue(url, PostmanCollectionV210Dto.class);

        // dump validate
        var resultDump = mapper.writeValueAsString(result);

        Set<ValidationMessage> validations = jsonSchema.validate(
                mapper.readTree(resultDump)
        );

        Assertions.assertThat(validations).isEmpty();
    }
}
