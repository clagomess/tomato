package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.service.JsonSchemaService;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.CONFIGURATION;

@Slf4j
public class ConfigurationDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaService.getTomatoJsonSchema(CONFIGURATION);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson() throws JsonProcessingException {
        ConfigurationDto dto = new ConfigurationDto();
        dto.setDataDirectory(new File("aaaa"));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }
}
