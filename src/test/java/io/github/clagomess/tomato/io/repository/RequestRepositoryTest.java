package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

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
}
