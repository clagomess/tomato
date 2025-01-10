package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkspaceSessionRepositoryTest {

    private final File testData = new File(getClass().getResource(
            "home/data"
    ).getFile());

    private File mockData;

    @BeforeEach
    public void setMockDataDir(){
        mockData = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockData.mkdirs());
    }

    @Test
    public void getWorkspaceSessionFile() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File("target"));

        WorkspaceRepository workspaceDataServiceMock = Mockito.mock(WorkspaceRepository.class);
        Mockito.when(workspaceDataServiceMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        WorkspaceSessionRepository workspaceSessionDS = new WorkspaceSessionRepository(
                new Repository(),
                workspaceDataServiceMock
        );

        var result = workspaceSessionDS.getWorkspaceSessionFile();
        Assertions.assertThat(result).hasFileName("workspace-session.json");
    }

    @Test
    public void load() throws IOException {
        var envFile = new File(testData, "workspace-nPUaq0TC/workspace-session.json");

        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .readFile(Mockito.any(), Mockito.any());

        WorkspaceSessionRepository workspaceSessionDSMock = Mockito.mock(
                WorkspaceSessionRepository.class,
                Mockito.withSettings().useConstructor()
        );
        Mockito.when(workspaceSessionDSMock.getWorkspaceSessionFile())
                .thenReturn(envFile);
        Mockito.when(workspaceSessionDSMock.load())
                .thenCallRealMethod();

        var result = workspaceSessionDSMock.load();
        assertEquals("ELQkYBrD", result.getId());
    }

    @Test
    public void save() throws IOException {
        var envFile = new File(mockData, "workspace-session.json");

        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());

        WorkspaceSessionRepository workspaceSessionDSMock = Mockito.mock(
                WorkspaceSessionRepository.class,
                Mockito.withSettings().useConstructor()
        );
        Mockito.when(workspaceSessionDSMock.getWorkspaceSessionFile())
                .thenReturn(envFile);
        Mockito.when(workspaceSessionDSMock.save(Mockito.any()))
                .thenCallRealMethod();

        var result = workspaceSessionDSMock.save(new WorkspaceSessionDto());
        Assertions.assertThat(result).isFile();
    }
}
