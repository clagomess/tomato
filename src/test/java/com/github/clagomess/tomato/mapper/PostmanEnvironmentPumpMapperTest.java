package com.github.clagomess.tomato.mapper;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.external.PostmanEnvironmentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostmanEnvironmentPumpMapperTest {
    private final PostmanEnvironmentPumpMapper pumpMapper = PostmanEnvironmentPumpMapper.INSTANCE;

    @Test
    public void toEnvironmentDto(){
        var valueA = new PostmanEnvironmentDto.Value();
        valueA.setKey("mKey");
        valueA.setValue("mValue");

        var source = new PostmanEnvironmentDto();
        source.setName("foo");
        source.setValues(List.of(valueA));

        EnvironmentDto result = pumpMapper.toEnvironmentDto(source);
        assertEquals("foo", result.getName());
        Assertions.assertThat(result.getEnvs()).hasSize(1);
        assertEquals("mKey", result.getEnvs().get(0).getKey());
        assertEquals("mValue", result.getEnvs().get(0).getValue());
    }

    @Test
    public void map(){
        var valueA = new PostmanEnvironmentDto.Value();
        valueA.setKey("mKey");
        valueA.setValue("mValue");

        var result = pumpMapper.map(valueA);
        assertEquals("mKey", result.getKey());
        assertEquals("mValue", result.getValue());
    }
}
