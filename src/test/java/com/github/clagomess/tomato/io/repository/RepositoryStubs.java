package com.github.clagomess.tomato.io.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class RepositoryStubs {
    protected final File testData = new File(Objects.requireNonNull(getClass().getResource(
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
        RequestRepository.cacheHead.evictAll();
        RequestRepository.cacheListFiles.evictAll();
        WorkspaceRepository.cacheList.evictAll();
        WorkspaceRepository.cacheLoad.evictAll();
        WorkspaceSessionRepository.cache.evictAll();
    }
}
