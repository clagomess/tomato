package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.*;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

public class DataServiceTest {
    private static WorkspaceDto workspace;

    @BeforeAll
    public static void setup(){
        RequestDto request = new RequestDto();
        CollectionDto collection = new CollectionDto();
        collection.setName(RandomStringUtils.randomAlphabetic(10));
        collection.setRequests(Collections.singletonList(request));

        workspace = new WorkspaceDto();
        workspace.setName(RandomStringUtils.randomAlphabetic(10));
        workspace.setEnvironments(Collections.singletonList(new EnvironmentDto()));
        workspace.setCollections(Collections.singletonList(collection));
    }

    @Test
    public void getCollectionNameByResquestId(){
        UUID requestId = workspace.getCollections().get(0).getRequests().get(0).getId();
        val result = DataService.getInstance().getCollectionByResquestId(requestId);
        Assertions.assertEquals(workspace.getCollections().get(0).getName(), result);
    }

    @Test
    public void getTomatoHomeDir(){
        Assertions.assertNotNull(DataService.getInstance().getTomatoHomeDir());
    }

    @Test
    public void getTomatoDir(){
        Assertions.assertNotNull(DataService.getInstance().getTomatoDir("data"));
    }

    @Test
    public void saveTomatoConfig() throws IOException {
        val dto = new TomatoConfigDto();
        dto.setCurrentWorkspaceId(workspace.getId());
        DataService.getInstance().saveTomatoConfig(dto);
    }

    @Test
    public void saveWorkspace() throws IOException {
        val listDto = Collections.singletonList(workspace);
        DataService.getInstance().saveWorkspace(listDto);
    }

    @Test
    public void saveCollection() throws IOException {
        val dto = workspace.getCollections().get(0);
        DataService.getInstance().saveCollection(workspace.getId(), dto);
    }

    @Test
    public void saveEnvironment() throws IOException {
        val dto = workspace.getEnvironments();
        DataService.getInstance().saveEnvironment(workspace.getId(), dto);
    }

    @Test
    public void saveRequest() throws IOException {
        val dto = workspace.getCollections().get(0).getRequests().get(0);
        DataService.getInstance().saveRequest(workspace.getId(), workspace.getCollections().get(0).getId(), dto);
    }

    @Test
    public void readAllContent() throws IOException {
        DataService.getInstance().readAllContent();
    }
}
