package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.service.EnvironmentDataService;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class EnvironmentDtoTest {
    private final EnvironmentDataService environmentDataService = EnvironmentDataService.getInstance();
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson() throws JsonProcessingException {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setName(RandomStringUtils.randomAlphabetic(10));
        dto.getEnvs().add(new EnvironmentDto.Env(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        ));

        var json = mapper.writeValueAsString(dto);
        log.info(json);

        Assertions.assertThat(environmentDataService.getJsonSchema().validate(
                mapper.readTree(json)
        )).isEmpty();
    }
}
