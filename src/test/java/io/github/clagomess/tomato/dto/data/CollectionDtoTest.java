package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import io.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.COLLECTION;

@Slf4j
class CollectionDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(COLLECTION);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    void toJson() throws JsonProcessingException {
        CollectionDto dto = new CollectionDto();
        dto.setName(RandomStringUtils.secure().nextAlphabetic(10));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    void equalsHashCode(){
        var dtoA = new CollectionDto();
        dtoA.setId("aaa");

        var dtoB = new CollectionDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }
}
