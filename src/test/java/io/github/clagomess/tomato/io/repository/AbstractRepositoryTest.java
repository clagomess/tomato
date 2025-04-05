package io.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.clagomess.tomato.dto.data.ConfigurationDto;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class AbstractRepositoryTest extends RepositoryStubs {
    private final AbstractRepository abstractRepository = Mockito.spy(new AbstractRepository(){});

    @Test
    public void read_e_write_File_json() throws IOException {
        var dto = new ConfigurationDto();
        dto.setDataDirectory(mockDataDir);
        dto.setCreateTime(LocalDateTime.now().minusDays(1));
        dto.setUpdateTime(LocalDateTime.now().minusDays(1));

        var file = new File(
                mockDataDir,
                String.format(
                        "read_e_write_File_json_%s.json",
                        RandomStringUtils.secure().nextAlphanumeric(8)
                )
        );

        // write
        abstractRepository.writeFile(file, dto);
        var result = abstractRepository.readFile(
                file,
                new TypeReference<ConfigurationDto>(){}
        );

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get().getUpdateTime())
                .isAfter(dto.getCreateTime());
    }

    @Test
    public void move_whenInvalidTarget() throws IOException {
        var source = new File(mockDataDir, "move_whenInvalidTarget.json");
        abstractRepository.writeFile(source, new WorkspaceDto());

        assertThrows(IOException.class, () -> abstractRepository.move(
                source,
                new File(mockDataDir, "invalid-target")
        ));
    }

    @Test
    public void move_whenInvalidSource() {
        var source = new File(mockDataDir, "move_whenInvalidSource.json");
        var target = new File(mockDataDir, "valid-target");
        assertTrue(target.mkdirs());

        assertThrows(IOException.class, () -> abstractRepository.move(
                source,
                target
        ));
    }

    @Test
    public void move_allValid() throws IOException {
        var source = new File(mockDataDir, "move_allValid.json");
        abstractRepository.writeFile(source, new WorkspaceDto());
        var target = new File(mockDataDir, "valid-target");
        assertTrue(target.mkdirs());

        abstractRepository.move(source, target);

        Assertions.assertThat(new File(target, source.getName()))
                .isFile();
    }

    @Test
    public void deleteDirectory() throws IOException {
        assertTrue(new File(mockDataDir, "bbb").mkdirs());
        abstractRepository.writeFile(
                new File(mockDataDir, "bbb/xxx.json"),
                new WorkspaceDto()
        );
        assertTrue(new File(mockDataDir, "aaa").mkdirs());
        abstractRepository.writeFile(
                new File(mockDataDir, "aaa.json"),
                new WorkspaceDto()
        );

        abstractRepository.deleteDirectory(mockDataDir);
        Assertions.assertThat(mockDataDir)
                .doesNotExist();
    }

    @Test
    public void getHomeDir(){
        assertThrows(AssertionError.class, abstractRepository::getHomeDir);
    }

    @Test
    public void listFiles_whenInvalidDirectory_returnEmpty() {
        Assertions.assertThat(
                abstractRepository.listFiles(null)
        ).isEmpty();

        Assertions.assertThat(
                abstractRepository.listFiles(new File(mockDataDir, "xyz"))
        ).isEmpty();
    }
}
