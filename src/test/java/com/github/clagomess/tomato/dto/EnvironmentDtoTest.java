package com.github.clagomess.tomato.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class EnvironmentDtoTest {
    @Test
    public void toJson() throws JsonProcessingException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setName(RandomStringUtils.randomAlphabetic(10));
        dto.getEnvs().add(new EnvironmentDto.Env(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)));

        String val = ObjectMapperUtil.getInstance().writeValueAsString(dto);
        log.info("{}", val);
        Assertions.assertEquals(dto.getName(), dto.toString());
    }
}
