package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkspaceSessionRepositoryTest extends RepositoryStubs {
    private final WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
    private final WorkspaceSessionRepository workspaceSessionRepositoryMock = Mockito.spy(
            new WorkspaceSessionRepository(workspaceRepositoryMock)
    );

    @BeforeEach
    public void setup(){
        // mock WorkspaceRepository
        Mockito.reset(workspaceRepositoryMock);

        // mock WorkspaceSessionRepository
        Mockito.reset(workspaceSessionRepositoryMock);
    }

    @Test
    public void getWorkspaceSessionFile() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(mockDataDir);

        Mockito.when(workspaceRepositoryMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        var result = workspaceSessionRepositoryMock.getWorkspaceSessionFile();
        Assertions.assertThat(result).hasFileName("workspace-session.json");
    }

    @Test
    public void load() throws IOException {
        var envFile = new File(testData, "workspace-nPUaq0TC/workspace-session.json");

        Mockito.doReturn(envFile)
                .when(workspaceSessionRepositoryMock)
                .getWorkspaceSessionFile();

        var result = workspaceSessionRepositoryMock.load();
        assertEquals("ELQkYBrD", result.getId());
    }

    @Test
    public void save() throws IOException {
        var envFile = new File(mockDataDir, "workspace-session.json");

        Mockito.doReturn(envFile)
                .when(workspaceSessionRepositoryMock)
                .getWorkspaceSessionFile();

        var result = workspaceSessionRepositoryMock.save(new WorkspaceSessionDto());
        Assertions.assertThat(result).isFile();
    }
}
