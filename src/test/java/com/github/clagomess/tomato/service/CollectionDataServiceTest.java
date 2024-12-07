package com.github.clagomess.tomato.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

public class CollectionDataServiceTest {
    private final CollectionDataService collectionDataService = CollectionDataService.getInstance();

    private final File testHome = new File(getClass().getResource(
            "DataServiceTest/home"
    ).getFile());

    @Test
    public void getCollectionTree_whenHasResult_return(){
        var result = collectionDataService.getCollectionTree(null, new File(
                testHome,
                "workspace-nPUaq0TC"
        ));

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
    }

    @Test
    public void getWorkspaceCollectionTree_whenInvalidDirectory_returnEmpty() throws IOException {
        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(new File("xyz"));

        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance()).thenReturn(dataServiceMock);

            CollectionDataService collectionDSMock = Mockito.mock(
                    CollectionDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(collectionDSMock.getWorkspaceCollectionTree(Mockito.any()))
                    .thenCallRealMethod();

            var result = collectionDSMock.getWorkspaceCollectionTree(
                    RandomStringUtils.randomAlphanumeric(8)
            );
            Assertions.assertThat(result).isEmpty();
        }
    }

    @Test
    public void getWorkspaceCollectionTree_whenValidDirectory_returnTree() throws IOException {
        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(testHome);
        Mockito.when(dataServiceMock.listFiles(Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(dataServiceMock.readFile(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();


        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance()).thenReturn(dataServiceMock);

            CollectionDataService collectionDSMock = Mockito.mock(
                    CollectionDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(collectionDSMock.getCollectionTree(Mockito.any(), Mockito.any()))
                    .thenCallRealMethod();
            Mockito.when(collectionDSMock.getWorkspaceCollectionTree(Mockito.any()))
                    .thenCallRealMethod();

            var result = collectionDSMock.getWorkspaceCollectionTree(
                    "nPUaq0TC"
            );
            Assertions.assertThat(result).hasSize(1);
        }
    }
}
