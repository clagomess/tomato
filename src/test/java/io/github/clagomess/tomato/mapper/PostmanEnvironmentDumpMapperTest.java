package io.github.clagomess.tomato.mapper;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PostmanEnvironmentDumpMapperTest {
    private final PostmanEnvironmentDumpMapper dumpMapper = PostmanEnvironmentDumpMapper.INSTANCE;

    @Test
    void toEnvironmentDto(){
        var source = new EnvironmentDto();
        source.setName("foo");
        source.setEnvs(List.of(new EnvironmentItemDto("mKey", "mValue")));

        PostmanEnvironmentDto result = dumpMapper.toEnvironmentDto(source);
        assertEquals("foo", result.getName());
        Assertions.assertThat(result.getValues()).hasSize(1);
        assertEquals("mKey", result.getValues().get(0).getKey());
        assertEquals("mValue", result.getValues().get(0).getValue());
    }

    @Nested
    class map {
        @Test
        void when_type_TEXT(){
            var valueA = new EnvironmentItemDto("mKey", "mValue");

            PostmanEnvironmentDto.Value result = dumpMapper.map(valueA);
            assertEquals("mKey", result.getKey());
            assertEquals("mValue", result.getValue());
        }

        @Test
        void when_type_SECRET(){
            var valueA = new EnvironmentItemDto(
                    SECRET,
                    UUID.randomUUID(),
                    "mKey",
                    null
            );

            PostmanEnvironmentDto.Value result = dumpMapper.map(valueA);
            assertEquals("mKey", result.getKey());
            assertEquals("***", result.getValue());
        }
    }
}
