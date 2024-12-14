package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.service.DataSessionDataService;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class DataSessionDtoTest {
    private final DataSessionDataService dataSessionDataService = DataSessionDataService.getInstance();
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson() throws JsonProcessingException {
        var dto = new DataSessionDto();
        dto.setWorkspaceId(RandomStringUtils.randomAlphabetic(10));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(dataSessionDataService.getJsonSchema().validate(
                mapper.readTree(json)
        )).isEmpty();
    }
}
