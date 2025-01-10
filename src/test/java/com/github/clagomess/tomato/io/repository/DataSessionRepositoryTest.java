package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.DataSessionDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataSessionRepositoryTest {
    private final Repository repositoryMock = Mockito.mock(Repository.class);
    private final DataSessionRepository dataSessionRepositoryMock = Mockito.mock(
            DataSessionRepository.class,
            Mockito.withSettings().useConstructor(
                    repositoryMock
            )
    );

    private File mockDataDir;
    private File mockDataSessionFile;

    @BeforeEach
    public void setup() throws IOException {
        mockDataDir = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockDataDir.mkdirs());

        mockDataSessionFile = new File(mockDataDir, "data-session.json");

        // mock Repository
        Mockito.reset(repositoryMock);

        Mockito.doCallRealMethod()
                .when(repositoryMock)
                .writeFile(Mockito.any(), Mockito.any());

        Mockito.when(repositoryMock.readFile(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();

        // mock DataSessionRepository
        Mockito.reset(dataSessionRepositoryMock);

        Mockito.when(dataSessionRepositoryMock.getDataSessionFile())
                .thenReturn(mockDataSessionFile);

        // reset cache
        DataSessionRepository.cache.evictAll();
    }

    @Test
    public void getDataSessionFile() throws IOException {
        var dataSessionRepository = new DataSessionRepository();
        Assertions.assertThat(dataSessionRepository.getDataSessionFile())
                .hasFileName("data-session.json");
    }

    @Test
    public void save() throws IOException {
        Mockito.doCallRealMethod()
                .when(dataSessionRepositoryMock)
                .save(Mockito.any());

        // test
        var dto = new DataSessionDto();
        dto.setWorkspaceId(RandomStringUtils.randomAlphanumeric(8));

        dataSessionRepositoryMock.save(dto);

        assertTrue(mockDataSessionFile.isFile());
    }

    @Test
    public void load_whenNotExists_ReturnsAndCreateDefault() throws IOException {
        Mockito.when(dataSessionRepositoryMock.load())
                .thenCallRealMethod();

        Mockito.doCallRealMethod()
                .when(dataSessionRepositoryMock)
                .save(Mockito.any());

        // test
        var result = dataSessionRepositoryMock.load();
        Assertions.assertThat(mockDataSessionFile).isFile();
        Assertions.assertThat(result.getWorkspaceId()).isNull();
    }

    @Test
    public void load_whenExists_Returns() throws IOException {
        Mockito.doCallRealMethod()
                .when(dataSessionRepositoryMock)
                .save(Mockito.any());

        Mockito.when(dataSessionRepositoryMock.load())
                .thenCallRealMethod();

        // teste
        var dto = new DataSessionDto();
        dataSessionRepositoryMock.save(dto);

        var result = dataSessionRepositoryMock.load();

        assertEquals(dto.getId(), result.getId());
    }
}
