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
    private final Repository repository = new Repository();

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
        repository.writeFile(file, dto);
        var result = repository.readFile(
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
        assertNotNull(repository.getHomeDir());
    }

    @Test
    public void getConfiguration_whenNotExists_ReturnsAndCreateDefault() throws IOException {
        Repository repositoryMock = Mockito.mock(Repository.class);
        Mockito.when(repositoryMock.getHomeDir())
                .thenReturn(mockHome);
        Mockito.doCallRealMethod()
                .when(repositoryMock)
                .writeFile(Mockito.any(), Mockito.any());
        Mockito.when(repositoryMock.getConfiguration())
                .thenCallRealMethod();

        var result = repositoryMock.getConfiguration();
        Assertions.assertThat(result.getDataDirectory().getAbsolutePath())
                .contains(mockHome + File.separator + "data");
    }

    @Test
    public void getConfiguration_whenExists_Returns() throws IOException {
        var dto = new ConfigurationDto();
        dto.setDataDirectory(new File(mockHome, "data-foo"));

        repository.writeFile(new File(
                mockHome, "configuration.json"
        ), dto);

        Repository repositoryMock = Mockito.mock(Repository.class);
        Mockito.when(repositoryMock.getHomeDir())
                .thenReturn(mockHome);
        Mockito.when(repositoryMock.readFile(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(repositoryMock.getConfiguration())
                .thenCallRealMethod();

        var result = repositoryMock.getConfiguration();
        Assertions.assertThat(result.getDataDirectory().getAbsolutePath())
                .contains(mockHome + File.separator + "data-foo");
    }

    @Test
    public void getDataDir_whenNotExists_CreateAndReturnDirectory() throws IOException {
        var dto = new ConfigurationDto();
        dto.setDataDirectory(new File(mockHome, "data"));

        Repository repositoryMock = Mockito.mock(Repository.class);
        Mockito.when(repositoryMock.getConfiguration())
                .thenReturn(dto);
        Mockito.when(repositoryMock.createDirectoryIfNotExists(Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(repositoryMock.getDataDir())
                .thenCallRealMethod();

        var result = repositoryMock.getDataDir();
        assertTrue(result.isDirectory());
    }

    @Test
    public void listFiles_whenInvalidDirectory_returnEmpty() {
        Assertions.assertThat(
                repository.listFiles(null)
        ).isEmpty();

        Assertions.assertThat(
                repository.listFiles(new File("xyz"))
        ).isEmpty();
    }

    @Test
    public void move_whenMoveFile() throws IOException {
        repository.writeFile(new File(
                mockHome, "foo.json"
        ), new RequestDto());

        var dest = new File(mockHome, "dest");
        assertTrue(dest.mkdir());

        repository.move(
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

        repository.move(
                dir,
                dest
        );

        Assertions.assertThat(new File(dest, "foo"))
                .isDirectory();
    }

    @Test
    public void delete_whenDeleteFile() throws IOException {
        var file = new File(mockHome, "foo.json");

        repository.writeFile(file, new RequestDto());

        repository.delete(file);

        Assertions.assertThat(file)
                .doesNotExist();
    }

    @Test
    public void delete_whenDeleteDir() throws IOException {
        var dir = new File(mockHome, "dir");
        assertTrue(dir.mkdir());
        var file = new File(mockHome, "foo.json");

        repository.writeFile(file, new RequestDto());

        repository.delete(dir);

        Assertions.assertThat(dir)
                .doesNotExist();
    }
}
