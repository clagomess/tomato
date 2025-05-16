package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.DataSessionDto;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataSessionRepositoryTest extends RepositoryStubs {
    private final ConfigurationRepository configurationRepositoryMock = Mockito.mock(ConfigurationRepository.class);
    private final DataSessionRepository dataSessionRepositoryMock = Mockito.spy(
            new DataSessionRepository(configurationRepositoryMock)
    );

    private File mockDataSessionFile;

    @BeforeEach
    public void setup() throws IOException {
        mockDataSessionFile = new File(mockDataDir, "data-session.json");

        // mock ConfigurationRepository
        Mockito.reset(configurationRepositoryMock);
        Mockito.when(configurationRepositoryMock.getDataDir())
                .thenReturn(mockDataDir);

        // mock DataSessionRepository
        Mockito.reset(dataSessionRepositoryMock);
        Mockito.when(dataSessionRepositoryMock.getDataSessionFile())
                .thenReturn(mockDataSessionFile);
    }

    @Test
    public void getDataSessionFile() throws IOException {
        Assertions.assertThat(dataSessionRepositoryMock.getDataSessionFile())
                .hasFileName("data-session.json");
    }

    @Test
    public void save() throws IOException {
        var dto = new DataSessionDto();
        dto.setWorkspaceId(RandomStringUtils.secure().nextAlphanumeric(8));

        dataSessionRepositoryMock.save(dto);

        assertTrue(mockDataSessionFile.isFile());
    }

    @Nested
    class load {
        @Test
        void whenNotExists_ReturnsAndCreateDefault() throws IOException {
            var result = dataSessionRepositoryMock.load();
            Assertions.assertThat(mockDataSessionFile).isFile();
            Assertions.assertThat(result.getWorkspaceId()).isNull();
        }

        @Test
        void whenExists_Returns() throws IOException {
            var dto = new DataSessionDto();

            try(BufferedWriter bw = new BufferedWriter(new FileWriter(mockDataSessionFile))) {
                ObjectMapperUtil.getInstance()
                        .writerWithDefaultPrettyPrinter()
                        .writeValue(bw, dto);
            }

            var result = dataSessionRepositoryMock.load();

            assertEquals(dto.getId(), result.getId());
        }
    }
}
