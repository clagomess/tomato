package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class RequestDataServiceTest {
    private final RequestDataService requestDataService = RequestDataService.getInstance();


    private final File testHome = new File(getClass().getResource(
            "DataServiceTest/home"
    ).getFile());

    @Test
    public void load() throws IOException {
        var request = new CollectionTreeDto.Request();
        request.setPath(new File(
                testHome,
                "workspace-nPUaq0TC/request-G4A3BCPq.json"
        ));

        var result = requestDataService.load(request);
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void getRequestList_whenHasResult_return(){
        var result = requestDataService.getRequestList(new File(
                testHome,
                "workspace-nPUaq0TC"
        ));

        Assertions.assertThat(result)
                .hasSize(1)
                .allMatch(item -> item.getPath() != null)
                .allMatch(item -> item.getPath().isFile())
                .anyMatch(item -> item.getId().equals(
                        "G4A3BCPq"
                ))
                .anyMatch(item -> item.getName().equals(
                        "New Request"
                ))
        ;
    }
}
