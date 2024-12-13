package com.github.clagomess.tomato.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class DumpPumpServiceTest {
    private final DumpPumpService dumpPumpService = DumpPumpService.getInstance();

    @Test
    public void pumpPostmanCollection() throws IOException {
        var mockDataDir = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));

        var postmanCollection = new File(getClass().getResource("DumpPumpServiceTest/postman.collection.v2.1.0.json")
                .getFile());

        dumpPumpService.pumpPostmanCollection(
                mockDataDir,
                postmanCollection
        );
    }
}
