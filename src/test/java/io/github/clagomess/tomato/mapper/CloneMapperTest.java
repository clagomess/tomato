package io.github.clagomess.tomato.mapper;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

class CloneMapperTest {
    private final CloneMapper environmentMapper = CloneMapper.INSTANCE;

    @Test
    void environment(){
        var original = new EnvironmentDto();
        original.setEnvs(List.of(new EnvironmentItemDto()));

        var cloned = environmentMapper.clone(original);

        assertNotSame(original, cloned);
        assertNotSame(
                original.getEnvs().get(0),
                cloned.getEnvs().get(0)
        );
    }
}
