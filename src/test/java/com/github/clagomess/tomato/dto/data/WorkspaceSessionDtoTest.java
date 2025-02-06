package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import com.networknt.schema.JsonSchema;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.WORKSPACE_SESSION;

public class WorkspaceSessionDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(WORKSPACE_SESSION);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson() throws JsonProcessingException {
        WorkspaceSessionDto dto = new WorkspaceSessionDto();
        dto.setEnvironmentId(RandomStringUtils.secure().nextAlphabetic(10));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void equalsHashCode(){
        var dtoA = new WorkspaceSessionDto();
        dtoA.setId("aaa");

        var dtoB = new WorkspaceSessionDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }
}
