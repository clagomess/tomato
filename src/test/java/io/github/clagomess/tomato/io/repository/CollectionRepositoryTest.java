package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Assertions.assertThat(collectionRepositoryMock.getCollectionFilePath(testData, "foo"))
                .hasFileName("collection-foo.json");
    }

    @Test
    void load() throws IOException {
        var tree = new CollectionTreeDto();
        tree.setPath(new File(testData, "workspace-JNtdCPvw/collection-a70uf9Xv"));
        tree.setId("a70uf9Xv");

        var result = collectionRepositoryMock.load(tree);
        assertEquals(tree.getId(), result.orElseThrow().getId());
    }

    @Test
    void loadTree() throws IOException {
        var collectionDir = new File(testData, "workspace-JNtdCPvw/collection-a70uf9Xv");

        var result = collectionRepositoryMock.loadTree(collectionDir);
        assertEquals("a70uf9Xv", result.orElseThrow().getId());
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
}
