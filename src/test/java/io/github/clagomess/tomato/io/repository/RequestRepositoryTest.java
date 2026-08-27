package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static io.github.clagomess.tomato.enums.BodyTypeEnum.BINARY;
import static io.github.clagomess.tomato.enums.BodyTypeEnum.MULTIPART_FORM;
import static io.github.clagomess.tomato.io.http.MediaType.APPLICATION_OCTET_STREAM_TYPE;
import static io.github.clagomess.tomato.io.repository.EnvironmentRepository.SYSENV_COLLECTION_FILE_DIR_KEY;
import static io.github.clagomess.tomato.util.FileUtils.COLLECTION_FILES_DIR;
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(result).isNotEmpty();
    }

    @Test
    void save_whenBasePathIsDirectory_createNewFile() throws IOException {
        var result = requestRepository.save(mockDataDir, new RequestDto());
        assertThat(result).isFile();
    }

    @Test
    void delete() throws IOException {
        File file = requestRepository.save(mockDataDir, new RequestDto());
        RequestHeadDto head = requestRepository.loadHead(file).orElseThrow();
        head.setPath(file);

        requestRepository.delete(head);

        assertThat(file)
                .doesNotExist();
    }

    @Nested
    class Move {
        private CollectionTreeDto target;

        private File createCollectionFile(File collectionDir) throws IOException {
            File dir = new File(collectionDir, COLLECTION_FILES_DIR);
            assertThat(dir.mkdirs()).isTrue();

            File file = new File(dir, "file-%s.bin".formatted(RandomStringUtils.secure().nextAlphanumeric(8)));
            Files.write(file.toPath(), new byte[]{1, 2, 3});
            return file;
        }

        private RequestHeadDto createRequestHead(RequestDto request) throws IOException {
            File requestFile = requestRepository.save(mockDataDir, request);
            RequestHeadDto source = requestRepository.loadHead(requestFile).orElseThrow();
            source.setPath(requestFile);
            return source;
        }

        @BeforeEach
        void setup() throws IOException {
            // create target
            var targetDir = collectionRepository.save(mockDataDir, new CollectionDto());
            requestRepository.save(targetDir, new RequestDto());
            target = collectionRepository.loadTree(targetDir).orElseThrow();
            target.setPath(targetDir);
        }

        @Test
        void whenMoveRequest() throws IOException {
            RequestHeadDto source = createRequestHead(new RequestDto());
            requestRepository.move(source, target);

            assertThat(new File(target.getPath(), source.getPath().getName()))
                    .isFile();
        }

        @Test
        void whenMultipartiBodyHasCollectionsFiles() throws IOException {
            var fileA = Files.createTempFile("tomato-test-temp-", ".bin");
            var fileB = createCollectionFile(mockDataDir);
            var fileBValue = "{{%s}}/%s".formatted(SYSENV_COLLECTION_FILE_DIR_KEY, fileB.getName());

            var request = new RequestDto();
            request.getBody().setType(MULTIPART_FORM);
            request.getBody().setMultiPartForm(List.of(
                    new FileKeyValueItemDto(FILE, "file-1", fileA.toString(), APPLICATION_OCTET_STREAM_TYPE, true),
                    new FileKeyValueItemDto(FILE, "file-2", fileA.toString(), APPLICATION_OCTET_STREAM_TYPE, false),
                    new FileKeyValueItemDto(FILE, "file-3", fileBValue, APPLICATION_OCTET_STREAM_TYPE, true),
                    new FileKeyValueItemDto(FILE, "file-4", fileBValue, APPLICATION_OCTET_STREAM_TYPE, false)
            ));

            RequestHeadDto source = createRequestHead(request);
            requestRepository.move(source, target);

            assertThat(new File(target.getPath(), source.getPath().getName())).isFile();
            assertThat(fileA.toFile()).isFile();
            assertThat(fileB).doesNotExist();
            assertThat(new File(
                    new File(target.getPath(), COLLECTION_FILES_DIR),
                    fileB.getName()
            )).isFile();
        }

        @Test
        void whenBinaryBodyHasCollectionsFiles() throws IOException {
            var file = createCollectionFile(mockDataDir);
            var fileValue = "{{%s}}/%s".formatted(SYSENV_COLLECTION_FILE_DIR_KEY, file.getName());

            var request = new RequestDto();
            request.getBody().setType(BINARY);
            request.getBody().getBinary().setFile(fileValue);

            RequestHeadDto source = createRequestHead(request);
            requestRepository.move(source, target);

            assertThat(new File(target.getPath(), source.getPath().getName())).isFile();
            assertThat(file).doesNotExist();
            assertThat(new File(
                    new File(target.getPath(), COLLECTION_FILES_DIR),
                    file.getName()
            )).isFile();
        }

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

            assertThat(file.setLastModified(lastModified
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

            assertThat(result.id()).isEqualTo("AAAAAAAA");
            assertThat(result.name()).isEqualTo("my-request");
            assertThat(result.fileName())
                    .isEqualTo(head.getPath().getAbsolutePath());
            assertThat(result.fileSize()).isEqualTo("512B");
            assertThat(result.fileLastModified())
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

            assertThat(result.fileSize()).isEqualTo("2.00KB");
        }
    }
}
