package io.github.clagomess.tomato.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

public class ObjectMapperUtil extends ObjectMapper {
    @Getter
    private static final ObjectMapperUtil instance = new ObjectMapperUtil();

    private ObjectMapperUtil() {
        registerModule(new JavaTimeModule());
    }
}
