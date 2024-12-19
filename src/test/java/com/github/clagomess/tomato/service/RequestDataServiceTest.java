package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestDataServiceTest {
    private final RequestDataService requestDataService = new RequestDataService();


    private final File testData = new File(getClass().getResource(
            "DataServiceTest/home/data"
    ).getFile());

    @Test
    public void load() throws IOException {
        var request = new RequestHeadDto();
        request.setPath(new File(
                testData,
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
        var collectionParent = new CollectionTreeDto();
        collectionParent.setPath(new File(
                testData,
                "workspace-nPUaq0TC"
        ));

        var result = requestDataService.getRequestList(collectionParent);

        Assertions.assertThat(result)
                .hasSize(1)
                .allMatch(item -> item.getPath() != null)
                .allMatch(item -> item.getPath().isFile())
                .anyMatch(item -> item.getId().equals(
                        "G4A3BCPq"
                ))
                .anyMatch(item -> item.getName().equals(
                        "/sample-root"
                ))
        ;
    }
}
