package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkspaceSessionDataServiceTest {

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
    public void getWorkspaceSessionFile() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setPath(new File("target"));

        WorkspaceDataService workspaceDataServiceMock = Mockito.mock(WorkspaceDataService.class);
        Mockito.when(workspaceDataServiceMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        WorkspaceSessionDataService workspaceSessionDS = new WorkspaceSessionDataService(
                new DataService(),
                workspaceDataServiceMock
        );

        var result = workspaceSessionDS.getWorkspaceSessionFile();
        Assertions.assertThat(result).hasFileName("workspace-session.json");
    }

    @Test
    public void load() throws IOException {
        var envFile = new File(testHome, "workspace-nPUaq0TC/workspace-session.json");

        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .readFile(Mockito.any(), Mockito.any());

        WorkspaceSessionDataService workspaceSessionDSMock = Mockito.mock(
                WorkspaceSessionDataService.class,
                Mockito.withSettings().useConstructor()
        );
        Mockito.when(workspaceSessionDSMock.getWorkspaceSessionFile())
                .thenReturn(envFile);
        Mockito.when(workspaceSessionDSMock.load())
                .thenCallRealMethod();

        var result = workspaceSessionDSMock.load();
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void save() throws IOException {
        var envFile = new File(mockHome, "workspace-session.json");

        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());

        WorkspaceSessionDataService workspaceSessionDSMock = Mockito.mock(
                WorkspaceSessionDataService.class,
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
