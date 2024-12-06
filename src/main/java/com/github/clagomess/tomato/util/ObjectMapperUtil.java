package com.github.clagomess.tomato.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

public class ObjectMapperUtil extends ObjectMapper {
    @Getter
    private static final ObjectMapperUtil instance = new ObjectMapperUtil(){{
        registerModule(new JavaTimeModule());
    }};

    private ObjectMapperUtil() {}
}
