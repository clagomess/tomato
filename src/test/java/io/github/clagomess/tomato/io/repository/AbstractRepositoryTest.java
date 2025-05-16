package io.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.clagomess.tomato.dto.data.ConfigurationDto;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AbstractRepositoryTest extends RepositoryStubs {
    private final AbstractRepository abstractRepository = Mockito.spy(new AbstractRepository(){});

    @Nested
    class write {
        @Test
        void whenExisting_setUpdateTime() throws IOException {
            var dto = new ConfigurationDto();
            dto.setCreateTime(LocalDateTime.now().minusDays(1));
            dto.setUpdateTime(LocalDateTime.now().minusDays(1));

            var file = new File(mockDataDir, "whenExisting_setUpdateTime.json");
            abstractRepository.writeFile(file, new TypeReference<>(){}, dto);

            var result = abstractRepository.readFile(
                    file,
                    new TypeReference<ConfigurationDto>(){}
            );

            Assertions.assertThat(result).isNotEmpty();
            Assertions.assertThat(result.get().getUpdateTime())
                    .isAfter(dto.getCreateTime());
        }

        @Test
        void whenTrySaveSameContent_doNothing() throws IOException {
            var date = LocalDateTime.now()
                    .minusDays(1)
                    .truncatedTo(ChronoUnit.SECONDS);
            var dto = new ConfigurationDto();
            dto.setCreateTime(date);
            dto.setUpdateTime(date);

            var file = new File(mockDataDir, "whenTrySaveSameContent_doNothing.json");
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                ObjectMapperUtil.getInstance()
                        .writerWithDefaultPrettyPrinter()
                        .writeValue(bw, dto);
            }

            // ----
            boolean result = abstractRepository.writeFile(file, new TypeReference<>(){}, dto);
            assertFalse(result);
        }
    }

    @Nested
    class move {
        @Test
        void whenInvalidTarget() throws IOException {
            var source = new File(mockDataDir, "move_whenInvalidTarget.json");
            abstractRepository.writeFile(source, new TypeReference<>(){}, new WorkspaceDto());

            assertThrows(IOException.class, () -> abstractRepository.move(
                    source,
                    new File(mockDataDir, "invalid-target")
            ));
        }

        @Test
        void whenInvalidSource() {
            var source = new File(mockDataDir, "move_whenInvalidSource.json");
            var target = new File(mockDataDir, "valid-target");
            assertTrue(target.mkdirs());

            assertThrows(IOException.class, () -> abstractRepository.move(
                    source,
                    target
            ));
        }

        @Test
        void allValid() throws IOException {
            var source = new File(mockDataDir, "move_allValid.json");
            abstractRepository.writeFile(source, new TypeReference<>(){}, new WorkspaceDto());
            var target = new File(mockDataDir, "valid-target");
            assertTrue(target.mkdirs());

            abstractRepository.move(source, target);

            Assertions.assertThat(new File(target, source.getName()))
                    .isFile();
        }
    }

    @Test
    public void deleteDirectory() throws IOException {
        assertTrue(new File(mockDataDir, "bbb").mkdirs());
        abstractRepository.writeFile(
                new File(mockDataDir, "bbb/xxx.json"),
                new TypeReference<>(){},
                new WorkspaceDto()
        );
        assertTrue(new File(mockDataDir, "aaa").mkdirs());
        abstractRepository.writeFile(
                new File(mockDataDir, "aaa.json"),
                new TypeReference<>(){},
                new WorkspaceDto()
        );

        abstractRepository.deleteDirectory(mockDataDir);
        Assertions.assertThat(mockDataDir)
                .doesNotExist();
    }

    @Test
    public void getHomeDir(){
        System.setProperty("user.home", "~");
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
