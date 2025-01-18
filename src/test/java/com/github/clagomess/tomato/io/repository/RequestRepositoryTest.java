package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestRepositoryTest {
    private final RequestRepository requestRepository = new RequestRepository();
    private final CollectionRepository collectionRepository = new CollectionRepository();

    private final File testData = new File(Objects.requireNonNull(getClass().getResource(
            "home/data"
    )).getFile());

    private File mockData;

    @BeforeEach
    public void setup(){
        mockData = new File("target", "datadir-" + RandomStringUtils.randomAlphanumeric(8));
        assertTrue(mockData.mkdirs());

        RequestRepository.cacheHead.evictAll();
        RequestRepository.cacheListFiles.evictAll();
    }

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
    public void save_whenBasePathIsDirectory_createNewFile() throws IOException {
        var result = requestRepository.save(mockData, new RequestDto());
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
        File file = requestRepository.save(mockData, new RequestDto());
        RequestHeadDto head = requestRepository.loadHead(file).orElseThrow();
        head.setPath(file);

        requestRepository.delete(head);

        Assertions.assertThat(file)
                .doesNotExist();
    }

    @Test
    public void move() throws IOException {
        // create source
        File file = requestRepository.save(mockData, new RequestDto());
        RequestHeadDto source = requestRepository.loadHead(file).orElseThrow();
        source.setPath(file);

        // create target
        var targetDir = collectionRepository.save(mockData, new CollectionDto());
        new RequestRepository().save(targetDir, new RequestDto());

        CollectionTreeDto targetTree = collectionRepository.load(targetDir).orElseThrow();
        targetTree.setPath(targetDir);

        // teste
        requestRepository.move(source, targetTree);

        Assertions.assertThat(new File(targetDir, source.getPath().getName()))
                .isFile();
    }
}
