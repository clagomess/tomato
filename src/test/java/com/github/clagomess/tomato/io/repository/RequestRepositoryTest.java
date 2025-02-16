package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RequestRepositoryTest extends RepositoryStubs {
    private final CollectionRepository collectionRepository = Mockito.spy(new CollectionRepository());
    private final RequestRepository requestRepository = Mockito.spy(new RequestRepository());

    @Test
    public void load() throws IOException {
        var request = new RequestHeadDto();
        request.setPath(new File(
                testData,
                "workspace-nPUaq0TC/request-G4A3BCPq.json"
        ));

        var result = requestRepository.load(request);
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void loadHeadFull() throws IOException {
        var file = new File(
                testData,
                "workspace-nPUaq0TC/collection-us62qhbS/request-xwNJ3kyc.json"
        );

        var result = requestRepository.loadHeadFull(file);
        Assertions.assertThat(result).isNotEmpty();
        assertNotNull(result.get().getPath());
        assertNotNull(result.get().getParent());
    }

    @Test
    public void loadHeadFull_whenInvalid_returnsEmpty() throws IOException {
        var file = new File(
                testData,
                "workspace-nPUaq0TC/xxx.json"
        );

        var result = requestRepository.loadHeadFull(file);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void save_whenBasePathIsDirectory_createNewFile() throws IOException {
        var result = requestRepository.save(mockDataDir, new RequestDto());
        Assertions.assertThat(result).isFile();
    }

    @Test
    public void getRequestList_whenHasResult_return(){
        var collectionParent = new CollectionTreeDto();
        collectionParent.setPath(new File(
                testData,
                "workspace-nPUaq0TC"
        ));

        var result = requestRepository.getRequestList(collectionParent);

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

    @Test
    public void delete() throws IOException {
        File file = requestRepository.save(mockDataDir, new RequestDto());
        RequestHeadDto head = requestRepository.loadHead(file).orElseThrow();
        head.setPath(file);

        requestRepository.delete(head);

        Assertions.assertThat(file)
                .doesNotExist();
    }

    @Test
    public void move() throws IOException {
        // create source
        File file = requestRepository.save(mockDataDir, new RequestDto());
        RequestHeadDto source = requestRepository.loadHead(file).orElseThrow();
        source.setPath(file);

        // create target
        var targetDir = collectionRepository.save(mockDataDir, new CollectionDto());
        new RequestRepository().save(targetDir, new RequestDto());

        CollectionTreeDto targetTree = collectionRepository.load(targetDir).orElseThrow();
        targetTree.setPath(targetDir);

        // teste
        requestRepository.move(source, targetTree);

        Assertions.assertThat(new File(targetDir, source.getPath().getName()))
                .isFile();
    }
}
