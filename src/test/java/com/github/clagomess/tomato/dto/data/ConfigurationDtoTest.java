package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.service.DataService;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
public class ConfigurationDtoTest {
    private final DataService collectionDataService = DataService.getInstance();
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();

    @Test
    public void toJson() throws JsonProcessingException {
        ConfigurationDto dto = new ConfigurationDto();
        dto.setDataDirectory(new File("aaaa"));

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(collectionDataService.getJsonSchema().validate(
                mapper.readTree(json)
        )).isEmpty();
    }
}
