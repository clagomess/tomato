package com.github.clagomess.tomato.io.converter;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PostmanConverterTest extends RepositoryStubs {
    @Test
    public void pumpCollection() throws IOException {
        PostmanConverter postmanConverter = new PostmanConverter();

        var postmanCollection = new File(Objects.requireNonNull(getClass()
                        .getResource("PostmanConverterTest/postman.collection.v2.1.0.json"))
                .getFile());

        postmanConverter.pumpCollection(
                mockDataDir,
                postmanCollection
        );
    }

    @Test
    public void pumpEnvironment() throws IOException {
        EnvironmentRepository environmentDSMock = Mockito.mock(
                EnvironmentRepository.class,
                Mockito.withSettings().useConstructor()
        );

        PostmanConverter postmanConverter = new PostmanConverter(
                new CollectionRepository(),
                new RequestRepository(),
                environmentDSMock
        );

        var postmanCollection = new File(Objects.requireNonNull(getClass()
                        .getResource("PostmanConverterTest/postman.environment.json"))
                .getFile());

        postmanConverter.pumpEnvironment(
                postmanCollection
        );
    }

    @Nested
    class Dump {
        private final WorkspaceRepository workspaceRepository = Mockito.spy(new WorkspaceRepository());
        private final CollectionRepository collectionRepository = Mockito.spy(new CollectionRepository());
        private final RequestRepository requestRepository = Mockito.spy(new RequestRepository());
        private final TreeRepository treeRepository = Mockito.spy(new TreeRepository(
                workspaceRepository,
                collectionRepository,
                requestRepository
        ));
        private final EnvironmentRepository environmentRepository = Mockito.spy(new EnvironmentRepository(
                workspaceRepository,
                new WorkspaceSessionRepository()
        ));

        @BeforeEach
        public void setup() throws IOException {
            Mockito.reset(workspaceRepository);

            var workspace = new WorkspaceDto();
            workspace.setName("ROOT");
            workspace.setPath(new File(testData, "workspace-JNtdCPvw"));

            Mockito.doReturn(workspace)
                    .when(workspaceRepository)
                    .getDataSessionWorkspace();
        }

        @Test
        public void dumpCollection() throws IOException {
            var rootTree = treeRepository.getWorkspaceCollectionTree();

            var resultCollectionFile = new File(mockHomeDir, "postman.collection.json");
            var postmanConverter = new PostmanConverter();
            postmanConverter.dumpCollection(
                    resultCollectionFile,
                    rootTree
            );

            Assertions.assertThat(resultCollectionFile).exists();
        }

        @Test
        public void dumpEnvironment() throws IOException {
            var resultFile = new File(mockHomeDir, "postman.environment.json");
            var postmanConverter = new PostmanConverter(
                    collectionRepository,
                    requestRepository,
                    environmentRepository
            );
            postmanConverter.dumpEnvironment(
                    resultFile,
                    "KmZxncfJ"
            );

            Assertions.assertThat(resultFile).exists();
        }
    }
}
