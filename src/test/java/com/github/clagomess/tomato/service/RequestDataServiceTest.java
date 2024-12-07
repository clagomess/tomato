package com.github.clagomess.tomato.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class RequestDataServiceTest {
    private final RequestDataService requestDataService = RequestDataService.getInstance();


    private final File testHome = new File(getClass().getResource(
            "DataServiceTest/home"
    ).getFile());

    @Test
    public void getRequestList_whenHasResult_return(){

        var result = requestDataService.getRequestList(new File(
                testHome,
                "workspace-nPUaq0TC"
        ));

        Assertions.assertThat(result)
                .hasSize(1)
                .anyMatch(item -> item.getId().equals(
                        "G4A3BCPq"
                ))
                .anyMatch(item -> item.getName().equals(
                        "New Request"
                ))
        ;
    }
}
