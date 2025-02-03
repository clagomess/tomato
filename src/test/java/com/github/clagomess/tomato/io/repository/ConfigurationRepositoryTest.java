package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.ConfigurationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationRepositoryTest extends RepositoryStubs {
    private final ConfigurationRepository configurationRepositoryMock = Mockito.spy(new ConfigurationRepository());

    @BeforeEach
    public void setup() throws IOException {
        // mock ConfigurationRepository
        Mockito.reset(configurationRepositoryMock);
        Mockito.doReturn(mockHomeDir)
                .when(configurationRepositoryMock)
                .getHomeDir();
    }

    @Test
    public void getConfigurationFile(){
        var result = configurationRepositoryMock.getConfigurationFile();
        assertEquals(
                new File(mockHomeDir, "configuration.json").getAbsolutePath(),
                result.getAbsolutePath()
        );
    }

    @Test
    public void load_whenNotExists_ReturnsAndCreateDefault() throws IOException {
        var result = configurationRepositoryMock.load();

        assertEquals(
                mockDataDir.getAbsolutePath(),
                result.getDataDirectory().getAbsolutePath()
        );
    }

    @Test
    public void load_whenExists_Returns() throws IOException {
        var dto = new ConfigurationDto();
        dto.setDataDirectory(mockDataDir);

        configurationRepositoryMock.writeFile(new File(
                mockHomeDir, "configuration.json"
        ), dto);

        // test
        var result = configurationRepositoryMock.load();
        assertEquals(
                mockDataDir.getAbsolutePath(),
                result.getDataDirectory().getAbsolutePath()
        );
    }

    @Test
    public void save() throws IOException {
        var dto = new ConfigurationDto();
        configurationRepositoryMock.save(dto);
    }

    @Test
    public void getDataDir_whenNotExists_CreateAndReturnDirectory() throws IOException {
        var dto = new ConfigurationDto();
        dto.setDataDirectory(mockDataDir);

        Mockito.doReturn(dto)
                .when(configurationRepositoryMock)
                .load();

        var result = configurationRepositoryMock.getDataDir();
        assertTrue(result.isDirectory());
    }
}
