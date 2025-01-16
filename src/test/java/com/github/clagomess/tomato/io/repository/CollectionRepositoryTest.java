package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionRepositoryTest {
    private final CollectionRepository collectionRepository = new CollectionRepository();

    private final File testData = new File(Objects.requireNonNull(getClass().getResource(
            "home/data"
    )).getFile());

    private File mockDataDir;

    @BeforeEach
    public void setup() {
        mockDataDir = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockDataDir.mkdirs());

        // reset cache
        CollectionRepository.cacheCollection.evictAll();
        CollectionRepository.cacheCollectionTree.evictAll();
        CollectionRepository.cacheListFiles.evictAll();
    }

    @Test
    public void getCollectionFilePath(){
        Assertions.assertThat(collectionRepository.getCollectionFilePath(testData, "foo"))
                .hasFileName("collection-foo.json");
    }

    @Test
    public void load_FromTree() throws IOException {
        var tree = new CollectionTreeDto();
        tree.setPath(new File(testData, "workspace-JNtdCPvw/collection-a70uf9Xv"));
        tree.setId("a70uf9Xv");

        var result = collectionRepository.load(tree);
        assertEquals(tree.getId(), result.orElseThrow().getId());
    }

    @Test
    public void load_FromDir() throws IOException {
        var collectionDir = new File(testData, "workspace-JNtdCPvw/collection-a70uf9Xv");

        var result = collectionRepository.load(collectionDir);
        assertEquals("a70uf9Xv", result.orElseThrow().getId());
    }

    @Test
    public void save_whenNew_create() throws IOException {
        var collection = new CollectionDto();

        var result = collectionRepository.save(mockDataDir, collection);
        Assertions.assertThat(result).isDirectory();
    }

    @Test
    public void listFiles(){
        var result = collectionRepository.listFiles(new File(testData, "workspace-JNtdCPvw"));
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void getCollectionChildrenTree_whenHasResult_return(){
        var collectionStart = new CollectionTreeDto();
        collectionStart.setPath(new File(
                testData,
                "workspace-nPUaq0TC"
        ));

        var result = collectionRepository.getCollectionChildrenTree(collectionStart).toList();

        Assertions.assertThat(result)
                .hasSize(1)
                .anyMatch(item -> item.getId().equals(
                        "us62qhbS"
                ))
                .anyMatch(item -> item.getName().equals(
                        "Collection-lvl1"
                ))
                .allMatch(item -> item.getPath() != null)
                .allMatch(item -> item.getChildren() != null)
                .allMatch(item -> item.getRequests() != null)
        ;

        collectionRepository.getCollectionChildrenTree(collectionStart).toList().get(0).getRequests().toList();

        var parent = result.get(0);
        Assertions.assertThat(parent.getRequests())
                .allMatch(item -> item.getParent() == parent);
    }

    @Test
    public void getWorkspaceCollectionTree() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setId("nPUaq0TC");
        workspaceDto.setName("ROOT");
        workspaceDto.setPath(new File("target"));

        WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
        Mockito.when(workspaceRepositoryMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        CollectionRepository collectionDS = new CollectionRepository(
                new Repository(),
                new RequestRepository(),
                workspaceRepositoryMock
        );

        var result = collectionDS.getWorkspaceCollectionTree();

        assertEquals("nPUaq0TC", result.getId());
        assertEquals("ROOT", result.getName());
        Assertions.assertThat(result.getPath()).isDirectory();
    }

    @Test
    public void delete() throws IOException {
        var dir = collectionRepository.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(dir, new RequestDto());

        CollectionTreeDto tree = collectionRepository.load(dir).orElseThrow();
        tree.setPath(dir);

        collectionRepository.delete(tree);

        Assertions.assertThat(dir)
                .doesNotExist();
    }

    @Test
    public void move() throws IOException {
        // create source
        var sourceDir = collectionRepository.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(sourceDir, new RequestDto());

        CollectionTreeDto sourceTree = collectionRepository.load(sourceDir).orElseThrow();
        sourceTree.setPath(sourceDir);

        // create target
        var targetDir = collectionRepository.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(targetDir, new RequestDto());

        CollectionTreeDto targetTree = collectionRepository.load(targetDir).orElseThrow();
        targetTree.setPath(targetDir);

        // test
        collectionRepository.move(sourceTree, targetTree);

        Assertions.assertThat(new File(targetDir, sourceDir.getName()))
                .isDirectory();
    }
}
