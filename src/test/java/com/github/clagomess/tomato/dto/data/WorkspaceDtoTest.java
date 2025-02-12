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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.clagomess.tomato.enums.TomatoJsonSchemaEnum.WORKSPACE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class WorkspaceDtoTest {
    private final JsonSchema jsonSchema = JsonSchemaBuilder.getTomatoJsonSchema(WORKSPACE);
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson() throws JsonProcessingException {
        WorkspaceDto dto = new WorkspaceDto();
        dto.setName(RandomStringUtils.secure().nextAlphabetic(10));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(jsonSchema.validate(
                mapper.readTree(json)
        )).isEmpty();
    }

    @Test
    public void equalsHashCode(){
        var dtoA = new WorkspaceDto();
        dtoA.setId("aaa");

        var dtoB = new WorkspaceDto();
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }

    @Test
    public void sort(){
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
