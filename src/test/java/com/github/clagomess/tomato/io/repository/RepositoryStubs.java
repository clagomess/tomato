package com.github.clagomess.tomato.io.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class RepositoryStubs {
    protected final File testData = new File(Objects.requireNonNull(RepositoryStubs.class.getResource(
            "home/data"
    )).getFile());

    protected File mockHomeDir;
    protected File mockDataDir;

    @BeforeEach
    public void setupDirs() {
        mockHomeDir = new File("target", "home-" + RandomStringUtils.secure().nextAlphanumeric(8));
        assertTrue(mockHomeDir.mkdirs());

        mockDataDir = new File(mockHomeDir, "data");
        assertTrue(mockDataDir.mkdirs());
    }

    @BeforeEach
    public void cleanCache() {
        CollectionRepository.cacheCollection.evictAll();
        CollectionRepository.cacheCollectionTree.evictAll();
        CollectionRepository.cacheListFiles.evictAll();
        ConfigurationRepository.cache.evictAll();
        DataSessionRepository.cache.evictAll();
        EnvironmentRepository.cache.evictAll();
        EnvironmentRepository.cacheListHead.evictAll();
        RequestRepository.cacheHead.evictAll();
        RequestRepository.cacheListFiles.evictAll();
        WorkspaceRepository.cacheList.evictAll();
        WorkspaceRepository.cacheLoad.evictAll();
        WorkspaceSessionRepository.cache.evictAll();
    }

    @AfterEach
    public void deleteDirs() throws IOException {
        try(Stream<Path> paths = Files.walk(mockHomeDir.toPath())){
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(item -> assertTrue(item.delete()));
        }
    }
}
