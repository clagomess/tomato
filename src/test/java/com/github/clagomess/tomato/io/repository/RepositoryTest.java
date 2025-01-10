package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.ConfigurationDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class RepositoryTest {
    private final Repository dataService = new Repository();

    @BeforeEach
    public void setup(){
        Repository.cacheHomeDir.evictAll();
        Repository.cacheConfiguration.evictAll();
        Repository.cacheDatadir.evictAll();
    }

    @Test
    public void read_e_write_File_json() throws IOException {
        var dto = new ConfigurationDto();
        dto.setDataDirectory(new File("target"));
        dto.setCreateTime(LocalDateTime.now().minusDays(1));
        dto.setUpdateTime(LocalDateTime.now().minusDays(1));

        var file = new File(
                "target",
                String.format(
                        "read_e_write_File_json_%s.json",
                        RandomStringUtils.randomAlphanumeric(8)
                )
        );

        // write
        dataService.writeFile(file, dto);
        var result = dataService.readFile(
                file,
                new TypeReference<ConfigurationDto>(){}
        );

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get().getUpdateTime())
                .isAfter(dto.getCreateTime());
    }

    private File mockHome;

    @BeforeEach
    public void setMockDataDir(){
        mockHome = new File("target", "home-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockHome.mkdirs());
    }

    @Test
    public void getHomeDir(){
        assertNotNull(dataService.getHomeDir());
    }

    @Test
    public void getConfiguration_whenNotExists_ReturnsAndCreateDefault() throws IOException {
        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.when(dataServiceMock.getHomeDir())
                .thenReturn(mockHome);
        Mockito.doCallRealMethod()
                .when(dataServiceMock)
                .writeFile(Mockito.any(), Mockito.any());
        Mockito.when(dataServiceMock.getConfiguration())
                .thenCallRealMethod();

        var result = dataServiceMock.getConfiguration();
        Assertions.assertThat(result.getDataDirectory().getAbsolutePath())
                .contains(mockHome + File.separator + "data");
    }

    @Test
    public void getConfiguration_whenExists_Returns() throws IOException {
        var dto = new ConfigurationDto();
        dto.setDataDirectory(new File(mockHome, "data-foo"));

        dataService.writeFile(new File(
                mockHome, "configuration.json"
        ), dto);

        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.when(dataServiceMock.getHomeDir())
                .thenReturn(mockHome);
        Mockito.when(dataServiceMock.readFile(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(dataServiceMock.getConfiguration())
                .thenCallRealMethod();

        var result = dataServiceMock.getConfiguration();
        Assertions.assertThat(result.getDataDirectory().getAbsolutePath())
                .contains(mockHome + File.separator + "data-foo");
    }

    @Test
    public void getDataDir_whenNotExists_CreateAndReturnDirectory() throws IOException {
        var dto = new ConfigurationDto();
        dto.setDataDirectory(new File(mockHome, "data"));

        Repository dataServiceMock = Mockito.mock(Repository.class);
        Mockito.when(dataServiceMock.getConfiguration())
                .thenReturn(dto);
        Mockito.when(dataServiceMock.createDirectoryIfNotExists(Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(dataServiceMock.getDataDir())
                .thenCallRealMethod();

        var result = dataServiceMock.getDataDir();
        assertTrue(result.isDirectory());
    }

    @Test
    public void listFiles_whenInvalidDirectory_returnEmpty() {
        Assertions.assertThat(
                dataService.listFiles(null)
        ).isEmpty();

        Assertions.assertThat(
                dataService.listFiles(new File("xyz"))
        ).isEmpty();
    }

    @Test
    public void move_whenMoveFile() throws IOException {
        dataService.writeFile(new File(
                mockHome, "foo.json"
        ), new RequestDto());

        var dest = new File(mockHome, "dest");
        assertTrue(dest.mkdir());

        dataService.move(
                new File(mockHome, "foo.json"),
                dest
        );

        Assertions.assertThat(new File(dest, "foo.json"))
                .isFile();
    }

    @Test
    public void move_whenMoveDir() throws IOException {
        var dir = new File(mockHome, "foo");
        assertTrue(dir.mkdir());

        var dest = new File(mockHome, "dest");
        assertTrue(dest.mkdir());

        dataService.move(
                dir,
                dest
        );

        Assertions.assertThat(new File(dest, "foo"))
                .isDirectory();
    }

    @Test
    public void delete_whenDeleteFile() throws IOException {
        var file = new File(mockHome, "foo.json");

        dataService.writeFile(file, new RequestDto());

        dataService.delete(file);

        Assertions.assertThat(file)
                .doesNotExist();
    }

    @Test
    public void delete_whenDeleteDir() throws IOException {
        var dir = new File(mockHome, "dir");
        assertTrue(dir.mkdir());
        var file = new File(mockHome, "foo.json");

        dataService.writeFile(file, new RequestDto());

        dataService.delete(dir);

        Assertions.assertThat(dir)
                .doesNotExist();
    }
}
