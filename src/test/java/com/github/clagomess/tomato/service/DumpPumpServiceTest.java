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
        var postmanCollection = new File("C:\\Users\\claudio\\Downloads\\SISCON-API.postman_collection.json");

        dumpPumpService.pumpPostmanCollection(
                mockDataDir,
                postmanCollection
        );
    }
}
