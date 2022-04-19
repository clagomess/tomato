package com.github.clagomess.tomato.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CollectionDtoTest {
    @Test
    public void toJson() throws JsonProcessingException {
        CollectionDto dto = new CollectionDto();
        dto.setName(RandomStringUtils.randomAlphabetic(10));

        String val = new ObjectMapper().writeValueAsString(dto);
        log.info("{}", val);
        Assertions.assertFalse(val.contains("requests"));
    }
}
