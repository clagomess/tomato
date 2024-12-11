package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionDataServiceTest {
    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();

    private final File testHome = new File(getClass().getResource(
            "DataServiceTest/home"
    ).getFile());

    @Test
    public void getCollectionChildrenTree_whenHasResult_return(){
        var result = collectionDataService.getCollectionChildrenTree(null, new File(
                testHome,
                "workspace-nPUaq0TC"
        )).toList();

        Assertions.assertThat(result)
                .hasSize(1)
                .anyMatch(item -> item.getId().equals(
                        "us62qhbS"
                ))
                .anyMatch(item -> item.getName().equals(
                        "a9sd8fkajnn"
                ))
                .allMatch(item -> item.getPath() != null)
                .allMatch(item -> item.getChildren() != null)
                .allMatch(item -> item.getRequests() != null)
        ;

        collectionDataService.getCollectionChildrenTree(null, new File(
                testHome,
                "workspace-nPUaq0TC"
        )).toList().get(0).getRequests().toList();

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

        try(var msWorkspaceDataService = Mockito.mockStatic(WorkspaceDataService.class)) {
            msWorkspaceDataService.when(() -> WorkspaceDataService.getInstance())
                    .thenReturn(workspaceDataServiceMock);

            CollectionDataService collectionDSMock = Mockito.mock(
                    CollectionDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(collectionDSMock.getWorkspaceCollectionTree())
                    .thenCallRealMethod();

            var result = collectionDSMock.getWorkspaceCollectionTree();

            assertEquals("nPUaq0TC", result.getId());
            assertEquals("ROOT", result.getName());
            Assertions.assertThat(result.getPath()).isDirectory();
        }
    }
}
