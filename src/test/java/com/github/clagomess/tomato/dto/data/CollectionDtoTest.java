package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.COLLECTION;

@Slf4j
public class CollectionDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(COLLECTION);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson() throws JsonProcessingException {
        CollectionDto dto = new CollectionDto();
        dto.setName(RandomStringUtils.randomAlphabetic(10));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void equalsHashCode(){
        var dtoA = new CollectionDto();
        dtoA.setId("aaa");

        var dtoB = new CollectionDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }
}
