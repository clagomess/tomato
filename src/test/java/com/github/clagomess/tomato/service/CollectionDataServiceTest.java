package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionDataServiceTest {
    private final CollectionDataService collectionDataService = new CollectionDataService();

    private final File testData = new File(getClass().getResource(
            "DataServiceTest/home/data"
    ).getFile());

    @Test
    public void getCollectionChildrenTree_whenHasResult_return(){
        var collectionStart = new CollectionTreeDto();
        collectionStart.setPath(new File(
                testData,
                "workspace-nPUaq0TC"
        ));

        var result = collectionDataService.getCollectionChildrenTree(collectionStart).toList();

        Assertions.assertThat(result)
                .hasSize(1)
                .anyMatch(item -> item.getId().equals(
                        "us62qhbS"
                ))
                .anyMatch(item -> item.getName().equals(
                        "Collection-lvl1"
                ))
                .allMatch(item -> item.getPath() != null)
                .allMatch(item -> item.getChildren() != null)
                .allMatch(item -> item.getRequests() != null)
        ;

        collectionDataService.getCollectionChildrenTree(collectionStart).toList().get(0).getRequests().toList();

        var parent = result.get(0);
        Assertions.assertThat(parent.getRequests())
                .allMatch(item -> item.getParent() == parent);
    }

    @Test
    public void getWorkspaceCollectionTree() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setId("nPUaq0TC");
        workspaceDto.setName("ROOT");
        workspaceDto.setPath(new File("target"));

        WorkspaceDataService workspaceDataServiceMock = Mockito.mock(WorkspaceDataService.class);
        Mockito.when(workspaceDataServiceMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        CollectionDataService collectionDS = new CollectionDataService(
                new DataService(),
                new RequestDataService(),
                workspaceDataServiceMock
        );

        var result = collectionDS.getWorkspaceCollectionTree();

        assertEquals("nPUaq0TC", result.getId());
        assertEquals("ROOT", result.getName());
        Assertions.assertThat(result.getPath()).isDirectory();
    }
}
