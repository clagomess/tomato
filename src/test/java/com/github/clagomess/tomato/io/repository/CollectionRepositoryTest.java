package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionRepositoryTest extends RepositoryStubs {
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    private final WorkspaceRepository workspaceRepository = Mockito.mock(WorkspaceRepository.class);
    private final CollectionRepository collectionRepositoryMock = Mockito.spy(new CollectionRepository(
            requestRepository,
            workspaceRepository
    ));

    @BeforeEach
    public void setup() {
        Mockito.reset(requestRepository);
        Mockito.reset(workspaceRepository);
        Mockito.reset(collectionRepositoryMock);
    }

    @Test
    public void getCollectionFilePath(){
        Assertions.assertThat(collectionRepositoryMock.getCollectionFilePath(testData, "foo"))
                .hasFileName("collection-foo.json");
    }

    @Test
    public void load_FromTree() throws IOException {
        var tree = new CollectionTreeDto();
        tree.setPath(new File(testData, "workspace-JNtdCPvw/collection-a70uf9Xv"));
        tree.setId("a70uf9Xv");

        var result = collectionRepositoryMock.load(tree);
        assertEquals(tree.getId(), result.orElseThrow().getId());
    }

    @Test
    public void load_FromDir() throws IOException {
        var collectionDir = new File(testData, "workspace-JNtdCPvw/collection-a70uf9Xv");

        var result = collectionRepositoryMock.load(collectionDir);
        assertEquals("a70uf9Xv", result.orElseThrow().getId());
    }

    @Test
    public void save_whenNew_create() throws IOException {
        var collection = new CollectionDto();

        var result = collectionRepositoryMock.save(mockDataDir, collection);
        Assertions.assertThat(result).isDirectory();
    }

    @Test
    public void listCollectionFiles(){
        var result = collectionRepositoryMock.listCollectionFiles(new File(testData, "workspace-JNtdCPvw"));
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void getCollectionParentTree() throws IOException {
        var workspace = new WorkspaceDto();
        workspace.setId("nPUaq0TC");
        var file = new File(
                testData,
                "workspace-nPUaq0TC/collection-us62qhbS/collection-s8tPaxvJ"
        );

        Mockito.doReturn(workspace)
                .when(workspaceRepository)
                .getDataSessionWorkspace();

        var result = collectionRepositoryMock.getCollectionParentTree(file);
        assertNotNull(result.getPath());
        assertNotNull(result.getChildren());
        assertNotNull(result.getRequests());
        assertEquals("s8tPaxvJ", result.getId());
        assertEquals("us62qhbS", result.getParent().getId());
        assertEquals("nPUaq0TC", result.getParent().getParent().getId());
    }

    @Test
    public void getCollectionParentTree_whenRoot_returnWorkspace() throws IOException {
        var workspace = new WorkspaceDto();
        workspace.setId("nPUaq0TC");
        var file = new File(testData, "workspace-nPUaq0TC");

        Mockito.doReturn(workspace)
                .when(workspaceRepository)
                .getDataSessionWorkspace();

        var result = collectionRepositoryMock.getCollectionParentTree(file);
        assertEquals("nPUaq0TC", result.getId());
    }

    @Test
    public void getCollectionParentTree_whenNotDir_throws() {
        var file = new File(testData, "workspace-nPUaq0TC/environment-7rZO7Z1T.json");

        assertThrows(
                IOException.class,
                () -> collectionRepositoryMock.getCollectionParentTree(file)
        );
    }

    @Test
    public void getCollectionChildrenTree_whenHasResult_return(){
        var collectionStart = new CollectionTreeDto();
        collectionStart.setPath(new File(
                testData,
                "workspace-nPUaq0TC"
        ));

        var result = collectionRepositoryMock.getCollectionChildrenTree(collectionStart).toList();

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

        collectionRepositoryMock.getCollectionChildrenTree(collectionStart).toList().get(0).getRequests().toList();

        var parent = result.get(0);
        Assertions.assertThat(parent.getRequests())
                .allMatch(item -> item.getParent() == parent);
    }

    @Test
    public void getWorkspaceCollectionTree() throws IOException {
        WorkspaceDto workspaceDto = new WorkspaceDto();
        workspaceDto.setId("nPUaq0TC");
        workspaceDto.setName("ROOT");
        workspaceDto.setPath(mockDataDir);

        WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
        Mockito.when(workspaceRepositoryMock.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        CollectionRepository collectionDS = new CollectionRepository(
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
        var dir = collectionRepositoryMock.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(dir, new RequestDto());

        CollectionTreeDto tree = collectionRepositoryMock.load(dir).orElseThrow();
        tree.setPath(dir);

        collectionRepositoryMock.delete(tree);

        Assertions.assertThat(dir)
                .doesNotExist();
    }

    @Test
    public void move() throws IOException {
        // create source
        var sourceDir = collectionRepositoryMock.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(sourceDir, new RequestDto());

        CollectionTreeDto sourceTree = collectionRepositoryMock.load(sourceDir).orElseThrow();
        sourceTree.setPath(sourceDir);

        // create target
        var targetDir = collectionRepositoryMock.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(targetDir, new RequestDto());

        CollectionTreeDto targetTree = collectionRepositoryMock.load(targetDir).orElseThrow();
        targetTree.setPath(targetDir);

        // test
        collectionRepositoryMock.move(sourceTree, targetTree);

        Assertions.assertThat(new File(targetDir, sourceDir.getName()))
                .isDirectory();
    }
}
