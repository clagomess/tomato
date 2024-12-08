package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.RequestDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void save_whenBasePathIsDirectory_createNewFile() throws IOException {
        var mockHome = new File("target", "home-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockHome.mkdirs());

        var result = requestDataService.save(mockHome, new RequestDto());
        Assertions.assertThat(result).isFile();
    }

    @Test
    public void getRequestList_whenHasResult_return(){
        var result = requestDataService.getRequestList(null, new File(
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
