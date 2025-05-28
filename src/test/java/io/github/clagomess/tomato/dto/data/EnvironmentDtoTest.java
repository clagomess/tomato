package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.ENVIRONMENT;

@Slf4j
class EnvironmentDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(ENVIRONMENT);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    void toJson() throws JsonProcessingException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setName(RandomStringUtils.secure().nextAlphabetic(10));
        dto.getEnvs().add(new EnvironmentItemDto(
                RandomStringUtils.secure().nextAlphabetic(10),
                RandomStringUtils.secure().nextAlphabetic(10)
        ));

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    void equalsHashCode(){
        var dtoA = new EnvironmentDto();
        dtoA.setId("aaa");

        var dtoB = new EnvironmentDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }
}
