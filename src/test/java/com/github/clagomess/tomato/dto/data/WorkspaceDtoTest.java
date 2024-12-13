package com.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class WorkspaceDtoTest {
    @Test
    public void toJson() throws JsonProcessingException {
        WorkspaceDto dto = new WorkspaceDto();
        dto.setName(RandomStringUtils.randomAlphabetic(10));

        String val = ObjectMapperUtil.getInstance().writeValueAsString(dto);
        log.info("{}", val);
        Assertions.assertFalse(val.contains("collections"));
        Assertions.assertFalse(val.contains("environments"));
    }
}
