package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.service.JsonSchemaService;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.DATA_SESSION;

@Slf4j
public class DataSessionDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaService.getTomatoJsonSchema(DATA_SESSION);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson() throws JsonProcessingException {
        var dto = new DataSessionDto();
        dto.setWorkspaceId(RandomStringUtils.randomAlphabetic(10));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }
}
