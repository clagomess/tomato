package io.github.clagomess.tomato.mapper;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostmanEnvironmentDumpMapperTest {
    private final PostmanEnvironmentDumpMapper dumpMapper = PostmanEnvironmentDumpMapper.INSTANCE;

    @Test
    public void toEnvironmentDto(){
        var source = new EnvironmentDto();
        source.setName("foo");
        source.setEnvs(List.of(new KeyValueItemDto("mKey", "mValue")));

        PostmanEnvironmentDto result = dumpMapper.toEnvironmentDto(source);
        assertEquals("foo", result.getName());
        Assertions.assertThat(result.getValues()).hasSize(1);
        assertEquals("mKey", result.getValues().get(0).getKey());
        assertEquals("mValue", result.getValues().get(0).getValue());
    }

    @Test
    public void map(){
        var valueA = new KeyValueItemDto("mKey", "mValue");

        PostmanEnvironmentDto.Value result = dumpMapper.map(valueA);
        assertEquals("mKey", result.getKey());
        assertEquals("mValue", result.getValue());
    }
}
