package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionRepositoryTest extends RepositoryStubs {
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    private final WorkspaceRepository workspaceRepository = Mockito.mock(WorkspaceRepository.class);
    private final CollectionRepository collectionRepositoryMock = Mockito.spy(new CollectionRepository());

    @BeforeEach
    void setup() {
        Mockito.reset(requestRepository);
        Mockito.reset(workspaceRepository);
        Mockito.reset(collectionRepositoryMock);
    }

    @Test
    void getCollectionFilePath(){
        Assertions.assertThat(collectionRepositoryMock.getCollectionFilePath(testData, new TomatoID("aaaaaaaa")))
                .hasFileName("collection-aaaaaaaa.json");
    }

    @Test
    void load() throws IOException {
        var tree = new CollectionTreeDto();
        tree.setPath(new File(testData, "workspace-JNtdCPvw/collection-a70uf9Xv"));
        tree.setId(new TomatoID("a70uf9Xv"));

        var result = collectionRepositoryMock.load(tree);
        assertEquals(tree.getId(), result.orElseThrow().getId());
    }

    @Test
    void loadTree() throws IOException {
        var collectionDir = new File(testData, "workspace-JNtdCPvw/collection-a70uf9Xv");

        var result = collectionRepositoryMock.loadTree(collectionDir);
        assertEquals(new TomatoID("a70uf9Xv"), result.orElseThrow().getId());
    }

    @Test
    void save_whenNew_create() throws IOException {
        var collection = new CollectionDto();

        var result = collectionRepositoryMock.save(mockDataDir, collection);
        Assertions.assertThat(result).isDirectory();
    }

    @Test
    void listCollectionFiles(){
        var result = collectionRepositoryMock.listCollectionFiles(new File(testData, "workspace-JNtdCPvw"));
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    void delete() throws IOException {
        var dir = collectionRepositoryMock.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(dir, new RequestDto());

        CollectionTreeDto tree = collectionRepositoryMock.loadTree(dir).orElseThrow();
        tree.setPath(dir);

        collectionRepositoryMock.delete(tree);

        Assertions.assertThat(dir)
                .doesNotExist();
    }

    @Test
    void move() throws IOException {
        // create source
        var sourceDir = collectionRepositoryMock.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(sourceDir, new RequestDto());

        CollectionTreeDto sourceTree = collectionRepositoryMock.loadTree(sourceDir).orElseThrow();
        sourceTree.setPath(sourceDir);

        // create target
        var targetDir = collectionRepositoryMock.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(targetDir, new RequestDto());

        CollectionTreeDto targetTree = collectionRepositoryMock.loadTree(targetDir).orElseThrow();
        targetTree.setPath(targetDir);

        // test
        collectionRepositoryMock.move(sourceTree, targetTree);

        Assertions.assertThat(new File(targetDir, sourceDir.getName()))
                .isDirectory();
    }

    @Nested
    class properties {
        private static final LocalDateTime LAST_MODIFIED = LocalDateTime.of(
                2025, 1, 2, 3, 4, 5
        );

        private CollectionTreeDto createCollectionTree(File collectionDir){
            assertTrue(collectionDir.setLastModified(LAST_MODIFIED
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            ));

            var tree = new CollectionTreeDto();
            tree.setId(new TomatoID("AAAAAAAA"));
            tree.setName("my-collection");
            tree.setPath(collectionDir);

            return tree;
        }

        @Test
        void whenHasNestedCollection_sumAllFiles() throws IOException {
            var collectionDir = new File(mockDataDir, "collection-AAAAAAAA");
            assertTrue(collectionDir.mkdirs());
            Files.write(
                    new File(collectionDir, "collection-AAAAAAAA.json").toPath(),
                    new byte[100]
            );
            Files.write(
                    new File(collectionDir, "request-BBBBBBBB.json").toPath(),
                    new byte[200]
            );

            var nestedDir = new File(collectionDir, "collection-CCCCCCCC");
            assertTrue(nestedDir.mkdirs());
            Files.write(
                    new File(nestedDir, "collection-CCCCCCCC.json").toPath(),
                    new byte[212]
            );

            var tree = createCollectionTree(collectionDir);

            var result = collectionRepositoryMock.properties(tree);

            Assertions.assertThat(result.id()).isEqualTo("AAAAAAAA");
            Assertions.assertThat(result.name()).isEqualTo("my-collection");
            Assertions.assertThat(result.fileName())
                    .isEqualTo(collectionDir.getAbsolutePath());
            Assertions.assertThat(result.fileSize()).isEqualTo("512B");
            Assertions.assertThat(result.fileCount()).isEqualTo("3");
            Assertions.assertThat(result.fileLastModified())
                    .isEqualTo("2025-01-02 03:04:05");
        }

        @Test
        void whenCollectionIsEmpty_returnZeroedSizeAndCount() {
            var collectionDir = new File(mockDataDir, "collection-AAAAAAAA");
            assertTrue(collectionDir.mkdirs());

            var tree = createCollectionTree(collectionDir);

            var result = collectionRepositoryMock.properties(tree);

            Assertions.assertThat(result.fileSize()).isEqualTo("0B");
            Assertions.assertThat(result.fileCount()).isEqualTo("0");
        }
    }
}
