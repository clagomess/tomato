package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.io.repository.CollectionRepository;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

public class PostmanConverterTest {
    @Test
    public void pumpCollection() throws IOException {
        var mockDataDir = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));

        PostmanConverter postmanConverter = new PostmanConverter();

        var postmanCollection = new File(getClass().getResource("PostmanConverterTest/postman.collection.v2.1.0.json")
                .getFile());

        postmanConverter.pumpCollection(
                mockDataDir,
                postmanCollection
        );
    }

    @Test
    public void pumpEnvironment() throws IOException {
        EnvironmentRepository environmentDSMock = Mockito.mock(
                EnvironmentRepository.class,
                Mockito.withSettings().useConstructor()
        );

        PostmanConverter postmanConverter = new PostmanConverter(
                new CollectionRepository(),
                new RequestRepository(),
                environmentDSMock
        );

        var postmanCollection = new File(getClass().getResource("PostmanConverterTest/postman.environment.json")
                .getFile());

        postmanConverter.pumpEnvironment(
                postmanCollection
        );
    }
}
