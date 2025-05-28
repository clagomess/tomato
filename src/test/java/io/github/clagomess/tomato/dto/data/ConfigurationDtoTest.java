package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import io.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.CONFIGURATION;

@Slf4j
class ConfigurationDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(CONFIGURATION);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    void toJson() throws JsonProcessingException {
        ConfigurationDto dto = new ConfigurationDto();
        dto.setDataDirectory(new File("aaaa"));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    void equalsHashCode(){
        var dtoA = new ConfigurationDto();
        dtoA.setId("aaa");

        var dtoB = new ConfigurationDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }
}
