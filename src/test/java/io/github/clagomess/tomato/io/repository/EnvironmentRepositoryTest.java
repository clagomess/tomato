package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EnvironmentRepositoryTest extends RepositoryStubs {
    private final WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
    private final EnvironmentRepository environmentRepositoryMock = Mockito.spy(new EnvironmentRepository(
            workspaceRepositoryMock,
            new WorkspaceSessionRepository()
    ));

    @BeforeEach
    void setup(){
        // mock WorkspaceRepository
        Mockito.reset(workspaceRepositoryMock);

        // mock EnvironmentRepository
        Mockito.reset(environmentRepositoryMock);
    }

    @Test
    void getEnvironmentFile() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(mockDataDir);

        Mockito.when(workspaceRepositoryMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        var result = environmentRepositoryMock.getEnvironmentFile("AAA");
        Assertions.assertThat(result).hasFileName("environment-AAA.json");
    }


    @Test
    void load() throws IOException {
        var envFile = new File(testData, "workspace-nPUaq0TC/environment-7rZO7Z1T.json");

        Mockito.doReturn(envFile)
                .when(environmentRepositoryMock)
                .getEnvironmentFile(Mockito.anyString());

        var result = environmentRepositoryMock.load("7rZO7Z1T");
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    void load_whenMutipleLoad_waysReturnClone() throws IOException {
        var envFile = new File(testData, "workspace-nPUaq0TC/environment-7rZO7Z1T.json");

        Mockito.doReturn(envFile)
                .when(environmentRepositoryMock)
                .getEnvironmentFile(Mockito.anyString());

        var resultA = environmentRepositoryMock.load("7rZO7Z1T");
        resultA.ifPresent(item -> item.setName("FOO_BAR_ENV"));
        var resultB = environmentRepositoryMock.load("7rZO7Z1T");

        assertNotEquals(
                resultA.get().getName(),
                resultB.get().getName()
        );
    }

    @Test
    void loadHead() throws IOException {
        var envFile = new File(testData, "workspace-nPUaq0TC/environment-7rZO7Z1T.json");

        Mockito.doReturn(envFile)
                .when(environmentRepositoryMock)
                .getEnvironmentFile(Mockito.anyString());

        var result = environmentRepositoryMock.loadHead("7rZO7Z1T");
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    void save() throws IOException {
        var environment = new EnvironmentDto();
        environment.getEnvs().add(new EnvironmentItemDto("AAA", "BBB"));
        var envFile = new File(mockDataDir, "environment-"+environment.getId()+".json");

        Mockito.doReturn(envFile)
                .when(environmentRepositoryMock)
                .getEnvironmentFile(Mockito.anyString());

        var result = environmentRepositoryMock.save(environment);
        Assertions.assertThat(result).isFile();
    }

    @Test
    void delete() throws IOException {
        var environment = new EnvironmentDto();
        environment.getEnvs().add(new EnvironmentItemDto("AAA", "BBB"));
        var envFile = new File(mockDataDir, "environment-"+environment.getId()+".json");

        Mockito.doReturn(envFile)
                .when(environmentRepositoryMock)
                .getEnvironmentFile(Mockito.anyString());

        var result = environmentRepositoryMock.save(environment);
        Assertions.assertThat(result).isFile();

        environmentRepositoryMock.delete(environment);
        Assertions.assertThat(result).doesNotExist();
    }

    @Test
    void listHead() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File(testData, "workspace-nPUaq0TC"));

        Mockito.when(workspaceRepositoryMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        var result = environmentRepositoryMock.listHead();
        Assertions.assertThat(result).isNotEmpty();
    }
}
