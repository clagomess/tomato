package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.data.DataSessionDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataSessionDataServiceTest {
    private File mockDataDir;

    @BeforeEach
    public void setMockDataDir(){
        mockDataDir = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockDataDir.mkdirs());
    }

    @Test
    public void saveDataSession() throws IOException {
        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());


        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance()).thenReturn(dataServiceMock);

            DataSessionDataService dataSessionDSMock = Mockito.mock(
                    DataSessionDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.doCallRealMethod()
                    .when(dataSessionDSMock)
                    .saveDataSession(Mockito.any());

            var dto = new DataSessionDto();
            dto.setWorkspaceId(RandomStringUtils.randomAlphanumeric(8));

            dataSessionDSMock.saveDataSession(dto);

            var result = new File(
                    mockDataDir,
                    "data-session.json"
            );

            assertTrue(result.isFile());
        }
    }

    @Test
    public void getDataSession_whenNotExists_ReturnsAndCreateDefault() throws IOException {
        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());


        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance()).thenReturn(dataServiceMock);

            DataSessionDataService dataSessionDSMock = Mockito.mock(
                    DataSessionDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(dataSessionDSMock.getDataSession())
                    .thenCallRealMethod();

            var result = dataSessionDSMock.getDataSession();
            Assertions.assertThat(result.getWorkspaceId()).isNull();
        }
    }

    @Test
    public void getDataSession_whenExists_Returns() throws IOException {
        var dto = new DataSessionDto();
        dto.setWorkspaceId(RandomStringUtils.randomAlphanumeric(8));

        DataService.getInstance().writeFile(new File(
                mockDataDir, "data-session.json"
        ), dto);

        DataService dataServiceMock = Mockito.mock(DataService.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.when(dataServiceMock.readFile(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();


        try(var msDataService = Mockito.mockStatic(DataService.class)) {
            msDataService.when(() -> DataService.getInstance()).thenReturn(dataServiceMock);

            DataSessionDataService dataSessionDSMock = Mockito.mock(
                    DataSessionDataService.class,
                    Mockito.withSettings().useConstructor()
            );
            Mockito.when(dataSessionDSMock.getDataSession())
                    .thenCallRealMethod();

            var result = dataSessionDSMock.getDataSession();
            Assertions.assertThat(result.getWorkspaceId()).isEqualTo(dto.getWorkspaceId());
        }
    }
}
