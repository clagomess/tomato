package com.github.clagomess.tomato.io.converter;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class PostmanConverterTest {
    private final PostmanConverter postmanConverter = new PostmanConverter();

    @Test
    public void pumpPostmanCollection() throws IOException {
        var mockDataDir = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));

        var postmanCollection = new File(getClass().getResource("PostmanConverterTest/postman.collection.v2.1.0.json")
                .getFile());

        postmanConverter.pumpPostmanCollection(
                mockDataDir,
                postmanCollection
        );
    }
}
