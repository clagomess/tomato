package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

public class EnvironmentRepositoryTest extends RepositoryStubs {
    private final WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
    private final EnvironmentRepository environmentRepositoryMock = Mockito.spy(new EnvironmentRepository(
            workspaceRepositoryMock,
            new WorkspaceSessionRepository()
    ));

    @BeforeEach
    public void setup(){
        // mock WorkspaceRepository
        Mockito.reset(workspaceRepositoryMock);

        // mock EnvironmentRepository
        Mockito.reset(environmentRepositoryMock);
    }

    @Test
    public void getEnvironmentFile() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File("target"));

        Mockito.when(workspaceRepositoryMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        var result = environmentRepositoryMock.getEnvironmentFile("AAA");
        Assertions.assertThat(result).hasFileName("environment-AAA.json");
    }


    @Test
    public void load() throws IOException {
        var envFile = new File(testData, "workspace-nPUaq0TC/environment-7rZO7Z1T.json");

        Mockito.doReturn(envFile)
                .when(environmentRepositoryMock)
                .getEnvironmentFile(Mockito.anyString());

        var result = environmentRepositoryMock.load("7rZO7Z1T");
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void save() throws IOException {
        var environment = new EnvironmentDto();
        environment.getEnvs().add(new KeyValueItemDto("AAA", "BBB"));
        var envFile = new File(mockDataDir, "environment-"+environment.getId()+".json");

        Mockito.doReturn(envFile)
                .when(environmentRepositoryMock)
                .getEnvironmentFile(Mockito.anyString());

        var result = environmentRepositoryMock.save(environment);
        Assertions.assertThat(result).isFile();
    }

    @Test
    public void list() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File(testData, "workspace-nPUaq0TC"));

        Mockito.when(workspaceRepositoryMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        var result = environmentRepositoryMock.list();
        Assertions.assertThat(result).isNotEmpty();
    }
}
