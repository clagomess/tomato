package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import io.github.clagomess.tomato.io.converter.JsonSchemaBuilder;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.WORKSPACE_SESSION;

class WorkspaceSessionDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(WORKSPACE_SESSION);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    void toJson() throws JsonProcessingException {
        WorkspaceSessionDto dto = new WorkspaceSessionDto();
        dto.setEnvironmentId(RandomStringUtils.secure().nextAlphabetic(10));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    void equalsHashCode(){
        var dtoA = new WorkspaceSessionDto();
        dtoA.setId("aaa");

        var dtoB = new WorkspaceSessionDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }

    @Nested
    class Request {
        @Test
        void equalsHashCode(){
            var dtoA = new WorkspaceSessionDto.Request();
            dtoA.setFilepath("target/request-xpt.json");

            var dtoB = new WorkspaceSessionDto.Request();
            dtoB.setFilepath("target/request-xpt.json");

            Assertions.assertThat(dtoA)
                    .isEqualTo(dtoB);
        }
    }
}
