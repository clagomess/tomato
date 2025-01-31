package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.DataSessionDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkspaceRepositoryTest {
    private File mockDataDir;

    @BeforeEach
    public void setup(){
        mockDataDir = new File("target", "datadir-" + RandomStringUtils.secure().nextAlphanumeric(8));
        assertTrue(mockDataDir.mkdirs());

        // reset cache
        WorkspaceRepository.cacheList.evictAll();
        WorkspaceRepository.cacheLoad.evictAll();
    }

    @Test
    public void getWorkspaceDirectory_whenNotExists_create() throws IOException {
        Repository repositoryMock = Mockito.mock(Repository.class);
        Mockito.when(repositoryMock.createDirectoryIfNotExists(Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(repositoryMock.getDataDir())
                .thenReturn(mockDataDir);


        WorkspaceRepository workspaceDS = new WorkspaceRepository(
                repositoryMock,
                new DataSessionRepository()
        );

        var result =workspaceDS.getWorkspaceDirectory(
                RandomStringUtils.secure().nextAlphanumeric(8)
        );

        Assertions.assertThat(result).isDirectory();
    }

    @Test
    public void save_whenNotExists_CreateAndWriteDto() throws IOException {
        Repository repositoryMock = Mockito.mock(Repository.class);
        Mockito.when(repositoryMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.when(repositoryMock.createDirectoryIfNotExists(Mockito.any()))
                .thenCallRealMethod();
        Mockito.doCallRealMethod()
                .when(repositoryMock)
                .writeFile(Mockito.any(), Mockito.any());

        WorkspaceRepository workspaceDS = new WorkspaceRepository(
                repositoryMock,
                new DataSessionRepository()
        );

        var dto = new WorkspaceDto();
        dto.setName("OPA");

        workspaceDS.save(dto);

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

    @Test
    public void list_whenDataDirIsEmpty_ReturnDefault() throws IOException {
        Repository repositoryMock = Mockito.mock(Repository.class);
        Mockito.when(repositoryMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.when(repositoryMock.listFiles(Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(repositoryMock.readFile(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(repositoryMock.createDirectoryIfNotExists(Mockito.any()))
                .thenCallRealMethod();
        Mockito.doCallRealMethod()
                .when(repositoryMock)
                .writeFile(Mockito.any(), Mockito.any());

        WorkspaceRepository workspaceDS = new WorkspaceRepository(
                repositoryMock,
                new DataSessionRepository()
        );

        Assertions.assertThat(workspaceDS.list())
                .isNotEmpty()
                .allMatch(item -> item.getPath().isDirectory())
        ;
    }

    @Test
    public void getDataSessionWorkspace_whenNotDefined_thenGetFirst() throws IOException {
        var workspace = new WorkspaceDto();

        DataSessionRepository dataSessionRepositoryMock = Mockito.mock(DataSessionRepository.class);
        Mockito.when(dataSessionRepositoryMock.load())
                .thenReturn(new DataSessionDto());

        WorkspaceRepository workspaceDSMock = Mockito.mock(
                WorkspaceRepository.class,
                Mockito.withSettings().useConstructor(
                        new Repository(),
                        dataSessionRepositoryMock
                )
        );
        Mockito.when(workspaceDSMock.list())
                .thenReturn(List.of(workspace));
        Mockito.when(workspaceDSMock.getDataSessionWorkspace())
                .thenCallRealMethod();

        var result = workspaceDSMock.getDataSessionWorkspace();
        assertEquals(result.getId(), workspace.getId());
    }

    @Test
    public void getDataSessionWorkspace_whenDefined_thenResult() throws IOException {
        var workspace = new WorkspaceDto();
        var dataSession = new DataSessionDto();
        dataSession.setWorkspaceId(workspace.getId());

        DataSessionRepository dataSessionRepositoryMock = Mockito.mock(DataSessionRepository.class);
        Mockito.when(dataSessionRepositoryMock.load())
                .thenReturn(dataSession);

        WorkspaceRepository workspaceDS = Mockito.mock(
                WorkspaceRepository.class,
                Mockito.withSettings().useConstructor(
                        new Repository(),
                        dataSessionRepositoryMock
                )
        );
        Mockito.when(workspaceDS.list())
                .thenReturn(List.of(new WorkspaceDto(), workspace));
        Mockito.when(workspaceDS.getDataSessionWorkspace())
                .thenCallRealMethod();

        var result = workspaceDS.getDataSessionWorkspace();
        assertEquals(result.getId(), workspace.getId());
    }
}
