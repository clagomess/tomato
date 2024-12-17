package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentDataServiceTest {

    private final File testHome = new File(getClass().getResource(
            "DataServiceTest/home"
    ).getFile());

    private File mockHome;

    @BeforeEach
    public void setMockDataDir(){
        mockHome = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockHome.mkdirs());
    }

    @Test
    public void getEnvironmentFile() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File("target"));

        WorkspaceDataService workspaceDataServiceMock = Mockito.mock(WorkspaceDataService.class);
        Mockito.when(workspaceDataServiceMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        try(var msWorkspaceDataService = Mockito.mockStatic(WorkspaceDataService.class)) {
            msWorkspaceDataService.when(() -> WorkspaceDataService.getInstance())
                    .thenReturn(workspaceDataServiceMock);

            EnvironmentDataService environmentDSMock = Mockito.mock(
                    EnvironmentDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(environmentDSMock.getEnvironmentFile(Mockito.any()))
                    .thenCallRealMethod();

            var result = environmentDSMock.getEnvironmentFile("AAA");
            Assertions.assertThat(result).hasFileName("environment-AAA.json");
        }
    }

    @Test
    public void load() throws IOException {
        var envFile = new File(testHome, "workspace-nPUaq0TC/environment-7rZO7Z1T.json");

        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .readFile(Mockito.any(), Mockito.any());

        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance())
                    .thenReturn(dataServiceMock);

            EnvironmentDataService environmentDSMock = Mockito.mock(
                    EnvironmentDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(environmentDSMock.getEnvironmentFile(Mockito.any()))
                    .thenReturn(envFile);
            Mockito.when(environmentDSMock.load(Mockito.any()))
                    .thenCallRealMethod();

            var result = environmentDSMock.load("7rZO7Z1T");
            Assertions.assertThat(result).isNotEmpty();
        }
    }

    @Test
    public void save() throws IOException {
        var environment = new EnvironmentDto();
        environment.getEnvs().add(new EnvironmentDto.Env("AAA", "BBB"));
        var envFile = new File(mockHome, "environment-"+environment.getId()+".json");

        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());

        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance())
                    .thenReturn(dataServiceMock);

            EnvironmentDataService environmentDSMock = Mockito.mock(
                    EnvironmentDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(environmentDSMock.getEnvironmentFile(Mockito.any()))
                    .thenReturn(envFile);
            Mockito.when(environmentDSMock.save(Mockito.any()))
                    .thenCallRealMethod();

            var result = environmentDSMock.save(environment);
            Assertions.assertThat(result).isFile();
        }
    }

    @Test
    public void list() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File(testHome, "workspace-nPUaq0TC"));

        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .listFiles(Mockito.any());
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .readFile(Mockito.any(), Mockito.any());

        WorkspaceDataService workspaceDataServiceMock = Mockito.mock(WorkspaceDataService.class);
        Mockito.when(workspaceDataServiceMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        try(
                var msDataService = Mockito.mockStatic(DataService.class);
                var msWorkspaceDataService = Mockito.mockStatic(WorkspaceDataService.class)
        ) {
            msDataService.when(() -> DataService.getInstance())
                    .thenReturn(dataServiceMock);
            msWorkspaceDataService.when(() -> WorkspaceDataService.getInstance())
                    .thenReturn(workspaceDataServiceMock);

            EnvironmentDataService environmentDSMock = Mockito.mock(
                    EnvironmentDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.doCallRealMethod()
                    .when(environmentDSMock)
                    .list();
            Mockito.when(environmentDSMock.load(Mockito.any()))
                    .thenCallRealMethod();
            Mockito.when(environmentDSMock.getEnvironmentFile(Mockito.any()))
                    .thenCallRealMethod();

            var result = environmentDSMock.list();
            Assertions.assertThat(result).isNotEmpty();
        }
    }
}
