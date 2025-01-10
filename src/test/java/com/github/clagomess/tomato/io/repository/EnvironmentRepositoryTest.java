package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentRepositoryTest {

    private final File testData = new File(Objects.requireNonNull(getClass().getResource(
            "home/data"
    )).getFile());

    private File mockData;

    @BeforeEach
    public void setMockDataDir(){
        mockData = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockData.mkdirs());

        // reset cache
        EnvironmentRepository.cache.evictAll();
    }

    @Test
    public void getEnvironmentFile() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File("target"));

        WorkspaceRepository workspaceDataServiceMock = Mockito.mock(WorkspaceRepository.class);
        Mockito.when(workspaceDataServiceMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        EnvironmentRepository environmentDS = new EnvironmentRepository(
                new Repository(),
                workspaceDataServiceMock,
                new WorkspaceSessionRepository()
        );

        var result = environmentDS.getEnvironmentFile("AAA");
        Assertions.assertThat(result).hasFileName("environment-AAA.json");
    }

    @Test
    public void load() throws IOException {
        var envFile = new File(testData, "workspace-nPUaq0TC/environment-7rZO7Z1T.json");

        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .readFile(Mockito.any(), Mockito.any());

        EnvironmentRepository environmentDSMock = Mockito.mock(
                EnvironmentRepository.class,
                Mockito.withSettings().useConstructor()
        );
        Mockito.when(environmentDSMock.getEnvironmentFile(Mockito.any()))
                .thenReturn(envFile);
        Mockito.when(environmentDSMock.load(Mockito.any()))
                .thenCallRealMethod();

        var result = environmentDSMock.load("7rZO7Z1T");
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void save() throws IOException {
        var environment = new EnvironmentDto();
        environment.getEnvs().add(new EnvironmentDto.Env("AAA", "BBB"));
        var envFile = new File(mockData, "environment-"+environment.getId()+".json");

        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());

        EnvironmentRepository environmentDSMock = Mockito.mock(
                EnvironmentRepository.class,
                Mockito.withSettings().useConstructor()
        );
        Mockito.when(environmentDSMock.getEnvironmentFile(Mockito.any()))
                .thenReturn(envFile);
        Mockito.when(environmentDSMock.save(Mockito.any()))
                .thenCallRealMethod();

        var result = environmentDSMock.save(environment);
        Assertions.assertThat(result).isFile();
    }

    @Test
    public void list() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File(testData, "workspace-nPUaq0TC"));

        WorkspaceRepository workspaceDataServiceMock = Mockito.mock(WorkspaceRepository.class);
        Mockito.when(workspaceDataServiceMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        EnvironmentRepository environmentDS = new EnvironmentRepository(
                new Repository(),
                workspaceDataServiceMock,
                new WorkspaceSessionRepository()
        );

        var result = environmentDS.list();
        Assertions.assertThat(result).isNotEmpty();
    }
}
