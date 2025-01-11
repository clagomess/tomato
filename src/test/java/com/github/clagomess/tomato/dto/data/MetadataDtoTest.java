package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class MetadataDtoTest {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();
    private static class Foo extends MetadataDto {}

    @Test
    public void writeAndReadJson() throws JsonProcessingException {
        var metadata = new Foo();

        var json = mapper.writeValueAsString(metadata);
        log.info(json);

        var parsed = mapper.readValue(json, Foo.class);
        assertEquals(metadata.getId(), parsed.getId());
        assertEquals(
                metadata.getCreateTime().truncatedTo(ChronoUnit.SECONDS),
                parsed.getCreateTime());
        assertEquals(
                metadata.getUpdateTime().truncatedTo(ChronoUnit.SECONDS),
                parsed.getUpdateTime()
        );
    }

    @Test
    public void equalsHashCode(){
        var dtoA = new MetadataDto(){};
        dtoA.setId("aaa");

        var dtoB = new MetadataDto(){};
        dtoB.setId("aaa");

        Assertions.assertThat(dtoA)
                .isEqualTo(dtoB);
    }
}
