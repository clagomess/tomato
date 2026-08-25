package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;

class RequestRepositoryTest extends RepositoryStubs {
    private final CollectionRepository collectionRepository = Mockito.spy(new CollectionRepository());
    private final RequestRepository requestRepository = Mockito.spy(new RequestRepository());

    @Test
    void load() throws IOException {
        var request = new RequestHeadDto();
        request.setPath(new File(
                testData,
                "workspace-nPUaq0TC/request-G4A3BCPq.json"
        ));

        var result = requestRepository.load(request);
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    void save_whenBasePathIsDirectory_createNewFile() throws IOException {
        var result = requestRepository.save(mockDataDir, new RequestDto());
        Assertions.assertThat(result).isFile();
    }

    @Test
    void delete() throws IOException {
        File file = requestRepository.save(mockDataDir, new RequestDto());
        RequestHeadDto head = requestRepository.loadHead(file).orElseThrow();
        head.setPath(file);

        requestRepository.delete(head);

        Assertions.assertThat(file)
                .doesNotExist();
    }

    @Test
    void move() throws IOException {
        // create source
        File file = requestRepository.save(mockDataDir, new RequestDto());
        RequestHeadDto source = requestRepository.loadHead(file).orElseThrow();
        source.setPath(file);

        // create target
        var targetDir = collectionRepository.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(targetDir, new RequestDto());

        CollectionTreeDto targetTree = collectionRepository.loadTree(targetDir).orElseThrow();
        targetTree.setPath(targetDir);

        // teste
        requestRepository.move(source, targetTree);

        Assertions.assertThat(new File(targetDir, source.getPath().getName()))
                .isFile();
    }

    @Nested
    class properties {
        private RequestHeadDto createRequestHead(
                String name,
                long fileSize,
                LocalDateTime lastModified
        ) throws IOException {
            File file = new File(mockDataDir, "request-AAAAAAAA.json");
            Files.write(file.toPath(), new byte[(int) fileSize]);

            Assertions.assertThat(file.setLastModified(lastModified
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )).isTrue();

            var head = new RequestHeadDto();
            head.setId(new TomatoID("AAAAAAAA"));
            head.setName(name);
            head.setPath(file);

            return head;
        }

        @Test
        void returnsRequestProperties() throws IOException {
            var head = createRequestHead(
                    "my-request",
                    512L,
                    LocalDateTime.of(2025, 1, 2, 3, 4, 5)
            );

            var result = requestRepository.properties(head);

            Assertions.assertThat(result.id()).isEqualTo("AAAAAAAA");
            Assertions.assertThat(result.name()).isEqualTo("my-request");
            Assertions.assertThat(result.fileName())
                    .isEqualTo(head.getPath().getAbsolutePath());
            Assertions.assertThat(result.fileSize()).isEqualTo("512B");
            Assertions.assertThat(result.fileLastModified())
                    .isEqualTo("2025-01-02 03:04:05");
        }

        @Test
        void whenFileSizeIsKilobytes_formatHumanReadable() throws IOException {
            var head = createRequestHead(
                    "my-request",
                    2048L,
                    LocalDateTime.of(2025, 1, 2, 3, 4, 5)
            );

            var result = requestRepository.properties(head);

            Assertions.assertThat(result.fileSize()).isEqualTo("2.00KB");
        }
    }
}
