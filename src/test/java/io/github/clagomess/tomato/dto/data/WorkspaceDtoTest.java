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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.WORKSPACE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class WorkspaceDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(WORKSPACE);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    void toJson() throws JsonProcessingException {
        WorkspaceDto dto = new WorkspaceDto();
        dto.setName(RandomStringUtils.secure().nextAlphabetic(10));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    void equalsHashCode(){
        var dtoA = new WorkspaceDto();
        dtoA.setId("aaa");

        var dtoB = new WorkspaceDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }

    @Test
    void sort(){
        var a = new WorkspaceDto();
        a.setName("aaa");

        var b = new WorkspaceDto();
        b.setName("bbb");

        List<WorkspaceDto> list = new ArrayList<>(2);
        list.add(b);
        list.add(a);

        Collections.sort(list);

        assertEquals("aaa", list.get(0).getName());
    }
}
