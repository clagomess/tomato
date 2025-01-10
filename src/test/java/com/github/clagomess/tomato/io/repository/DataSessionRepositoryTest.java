package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.DataSessionDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataSessionRepositoryTest {
    private File mockDataDir;

    @BeforeEach
    public void setMockDataDir(){
        mockDataDir = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockDataDir.mkdirs());
    }

    @Test
    public void save() throws IOException {
        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());


        DataSessionRepository dataSessionDS = new DataSessionRepository(
                dataServiceMock
        );

        var dto = new DataSessionDto();
        dto.setWorkspaceId(RandomStringUtils.randomAlphanumeric(8));

        dataSessionDS.save(dto);

        var result = new File(
                mockDataDir,
                "data-session.json"
        );

        assertTrue(result.isFile());
    }

    @Test
    public void load_whenNotExists_ReturnsAndCreateDefault() throws IOException {
        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());

        DataSessionRepository dataSessionDS = new DataSessionRepository(
                dataServiceMock
        );

        var result = dataSessionDS.load();
        Assertions.assertThat(result.getWorkspaceId()).isNull();
    }

    @Test
    public void load_whenExists_Returns() throws IOException {
        var dto = new DataSessionDto();
        dto.setWorkspaceId(RandomStringUtils.randomAlphanumeric(8));

        new Repository().writeFile(new File(
                mockDataDir, "data-session.json"
        ), dto);

        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.when(dataServiceMock.getDataDir())
                .thenReturn(mockDataDir);
        Mockito.when(dataServiceMock.readFile(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();


        DataSessionRepository dataSessionDS = new DataSessionRepository(
                dataServiceMock
        );

        var result = dataSessionDS.load();
        Assertions.assertThat(result.getWorkspaceId()).isEqualTo(dto.getWorkspaceId());
    }
}
