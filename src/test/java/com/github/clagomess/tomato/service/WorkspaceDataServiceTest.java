package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.DataSessionDto;
import com.github.clagomess.tomato.dto.WorkspaceDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkspaceDataServiceTest {
    private File mockDataDir;

    @BeforeEach
    public void setMockDataDir(){
        mockDataDir = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockDataDir.mkdirs());
    }

    @Test
    public void getWorkspaceDirectory_whenNotExists_create() throws IOException {
        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.when(dataServiceMock.createDirectoryIfNotExists(Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);


        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance()).thenReturn(dataServiceMock);

            WorkspaceDataService workspaceDSMock = Mockito.mock(
                    WorkspaceDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.doCallRealMethod()
                    .when(workspaceDSMock)
                    .getWorkspaceDirectory(Mockito.any());

            var result =workspaceDSMock.getWorkspaceDirectory(
                    RandomStringUtils.randomAlphanumeric(8)
            );

            Assertions.assertThat(result).isDirectory();
        }
    }

    @Test
    public void saveWorkspace_whenNotExists_CreateAndWriteDto() throws IOException {
        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.when(dataServiceMock.createDirectoryIfNotExists(Mockito.any()))
                .thenCallRealMethod();
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());

        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance()).thenReturn(dataServiceMock);

            WorkspaceDataService workspaceDSMock = Mockito.mock(
                    WorkspaceDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(workspaceDSMock.getWorkspaceDirectory(Mockito.any()))
                    .thenCallRealMethod();
            Mockito.doCallRealMethod()
                    .when(workspaceDSMock)
                    .saveWorkspace(Mockito.any());

            var dto = new WorkspaceDto();
            dto.setName("OPA");

            workspaceDSMock.saveWorkspace(dto);

            var result = new File(
                    mockDataDir,
                    String.format(
                            "workspace-%s/workspace-%s.json",
                            dto.getId(),
                            dto.getId()
                    )
            );

            assertTrue(result.isFile());
        }
    }

    @Test
    public void listWorkspaces_whenDataDirIsEmpty_ReturnDefault() throws IOException {
        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.when(dataServiceMock.listFiles(Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(dataServiceMock.readFile(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(dataServiceMock.createDirectoryIfNotExists(Mockito.any()))
                .thenCallRealMethod();
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());

        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance()).thenReturn(dataServiceMock);

            WorkspaceDataService workspaceDSMock = Mockito.mock(
                    WorkspaceDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(workspaceDSMock.getWorkspaceDirectory(Mockito.any()))
                    .thenCallRealMethod();
            Mockito.doCallRealMethod()
                    .when(workspaceDSMock)
                    .saveWorkspace(Mockito.any());
            Mockito.when(workspaceDSMock.listWorkspaces())
                    .thenCallRealMethod();

            Assertions.assertThat(workspaceDSMock.listWorkspaces())
                    .isNotEmpty()
                    .allMatch(item -> item.getPath().isDirectory())
            ;
        }
    }

    @Test
    public void getDataSessionWorkspace_whenNotDefined_thenGetFirst() throws IOException {
        var workspace = new WorkspaceDto();

        DataSessionDataService dataSessionDataServiceMock = Mockito.mock(DataSessionDataService.class);
        Mockito.when(dataSessionDataServiceMock.getDataSession())
                .thenReturn(new DataSessionDto());

        try(var msDataSessionDataService = Mockito.mockStatic(DataSessionDataService.class)) {
            msDataSessionDataService.when(() -> DataSessionDataService.getInstance())
                    .thenReturn(dataSessionDataServiceMock);

            WorkspaceDataService workspaceDSMock = Mockito.mock(
                    WorkspaceDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(workspaceDSMock.listWorkspaces())
                    .thenReturn(Stream.of(workspace));
            Mockito.when(workspaceDSMock.getDataSessionWorkspace())
                    .thenCallRealMethod();

            var result = workspaceDSMock.getDataSessionWorkspace();
            assertEquals(result.getId(), workspace.getId());
        }
    }

    @Test
    public void getDataSessionWorkspace_whenDefined_thenResult() throws IOException {
        var workspace = new WorkspaceDto();
        var dataSession = new DataSessionDto();
        dataSession.setWorkspaceId(workspace.getId());

        DataSessionDataService dataSessionDataServiceMock = Mockito.mock(DataSessionDataService.class);
        Mockito.when(dataSessionDataServiceMock.getDataSession())
                .thenReturn(dataSession);

        try(var msDataSessionDataService = Mockito.mockStatic(DataSessionDataService.class)) {
            msDataSessionDataService.when(() -> DataSessionDataService.getInstance())
                    .thenReturn(dataSessionDataServiceMock);

            WorkspaceDataService workspaceDSMock = Mockito.mock(
                    WorkspaceDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(workspaceDSMock.listWorkspaces())
                    .thenReturn(Stream.of(new WorkspaceDto(), workspace));
            Mockito.when(workspaceDSMock.getDataSessionWorkspace())
                    .thenCallRealMethod();

            var result = workspaceDSMock.getDataSessionWorkspace();
            assertEquals(result.getId(), workspace.getId());
        }
    }
}
