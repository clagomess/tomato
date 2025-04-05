package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TreeRepositoryTest extends RepositoryStubs {
    private final ConfigurationRepository configurationRepository = Mockito.mock(ConfigurationRepository.class);
    private final DataSessionRepository dataSessionRepository = Mockito.spy(
            new DataSessionRepository(configurationRepository)
    );
    private final WorkspaceRepository workspaceRepository = Mockito.spy(new WorkspaceRepository(
            configurationRepository,
            dataSessionRepository
    ));
    private final CollectionRepository collectionRepository = Mockito.spy(CollectionRepository.class);
    private final RequestRepository requestRepository = Mockito.spy(RequestRepository.class);
    private final TreeRepository treeRepository = Mockito.spy(new TreeRepository(
            workspaceRepository,
            collectionRepository,
            requestRepository
    ));

    @BeforeEach
    public void setup() throws IOException {
        Mockito.reset(configurationRepository);
        Mockito.reset(dataSessionRepository);
        Mockito.reset(workspaceRepository);
        Mockito.reset(collectionRepository);
        Mockito.reset(requestRepository);
        Mockito.reset(treeRepository);

        Mockito.when(configurationRepository.getDataDir())
                .thenReturn(mockDataDir);
    }

    @Nested
    class LoadRequestHead {
        @Test
        public void loadHeadFull() throws IOException {
            var file = new File(
                    testData,
                    "workspace-nPUaq0TC/collection-us62qhbS/request-xwNJ3kyc.json"
            );

            var result = treeRepository.loadRequestHead(file);
            Assertions.assertThat(result).isNotEmpty();
            assertNotNull(result.get().getPath());
            assertNotNull(result.get().getParent());
        }

        @Test
        public void whenInvalid_returnsEmpty() throws IOException {
            var file = new File(
                    testData,
                    "workspace-nPUaq0TC/xxx.json"
            );

            var result = treeRepository.loadRequestHead(file);
            Assertions.assertThat(result).isEmpty();
        }
    }

    @Test
    public void getRequestList_whenHasResult_return(){
        var collectionParent = new CollectionTreeDto();
        collectionParent.setPath(new File(
                testData,
                "workspace-nPUaq0TC"
        ));

        var result = treeRepository.getRequestList(collectionParent);

        Assertions.assertThat(result)
                .hasSize(1)
                .allMatch(item -> item.getPath() != null)
                .allMatch(item -> item.getPath().isFile())
                .anyMatch(item -> item.getId().equals(
                        "G4A3BCPq"
                ))
                .anyMatch(item -> item.getName().equals(
                        "/sample-root"
                ))
        ;
    }

    @Nested
    class GetCollectionParentTree {
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

            var result = treeRepository.getCollectionParentTree(file);
            assertNotNull(result.getPath());
            assertNotNull(result.getChildren());
            assertNotNull(result.getRequests());
            assertEquals("s8tPaxvJ", result.getId());
            assertEquals("us62qhbS", result.getParent().getId());
            assertEquals("nPUaq0TC", result.getParent().getParent().getId());
        }

        @Test
        public void whenRoot_returnWorkspace() throws IOException {
            var workspace = new WorkspaceDto();
            workspace.setId("nPUaq0TC");
            var file = new File(testData, "workspace-nPUaq0TC");

            Mockito.doReturn(workspace)
                    .when(workspaceRepository)
                    .getDataSessionWorkspace();

            var result = treeRepository.getCollectionParentTree(file);
            assertEquals("nPUaq0TC", result.getId());
        }

        @Test
        public void whenNotDir_throws() {
            var file = new File(testData, "workspace-nPUaq0TC/environment-7rZO7Z1T.json");

            assertThrows(
                    IOException.class,
                    () -> treeRepository.getCollectionParentTree(file)
            );
        }
    }

    @Test
    public void getCollectionChildrenTree_whenHasResult_return(){
        var collectionStart = new CollectionTreeDto();
        collectionStart.setPath(new File(
                testData,
                "workspace-nPUaq0TC"
        ));

        var result = treeRepository.getCollectionChildrenTree(collectionStart).toList();

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

        treeRepository.getCollectionChildrenTree(collectionStart).toList().get(0).getRequests().toList();

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

        Mockito.when(workspaceRepository.getDataSessionWorkspace())
                .thenReturn(workspaceDto);

        var result = treeRepository.getWorkspaceCollectionTree();

        assertEquals("nPUaq0TC", result.getId());
        assertEquals("ROOT", result.getName());
        Assertions.assertThat(result.getPath()).isDirectory();
    }
}
