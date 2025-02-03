package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.ConfigurationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class RepositoryTest {
    private final AbstractRepository abstractRepository = Mockito.spy(new AbstractRepository(){});

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
    public void getHomeDir(){
        assertThrows(AssertionError.class, abstractRepository::getHomeDir);
    }

    @Test
    public void listFiles_whenInvalidDirectory_returnEmpty() {
        Assertions.assertThat(
                abstractRepository.listFiles(null)
        ).isEmpty();

        Assertions.assertThat(
                abstractRepository.listFiles(new File("xyz"))
        ).isEmpty();
    }
}
