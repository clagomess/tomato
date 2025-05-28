package io.github.clagomess.tomato.io.repository;

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

    protected File mockUserHomeDir;
    protected File mockHomeDir;
    protected File mockDataDir;

    @BeforeEach
    void setupDirs() {
        mockUserHomeDir = new File(
                "target",
                "home-" + RandomStringUtils.secure().nextAlphanumeric(8)
        );

        mockHomeDir = new File(mockUserHomeDir, ".tomato");
        assertTrue(mockHomeDir.mkdirs());

        mockDataDir = new File(mockHomeDir, "data");
        assertTrue(mockDataDir.mkdirs());

        System.setProperty("user.home", mockUserHomeDir.getAbsolutePath());
    }

    @BeforeEach
    void cleanCache() {
        ConfigurationRepository.cache.evictAll();
        ConfigurationRepository.cacheHomeDir.evictAll();
        ConfigurationRepository.cacheDatadir.evictAll();
        DataSessionRepository.cache.evictAll();
        EnvironmentRepository.cache.evictAll();
        WorkspaceRepository.cacheList.evictAll();
        WorkspaceSessionRepository.cache.evictAll();
    }

    @AfterEach
    void deleteDirs() throws IOException {
        try(Stream<Path> paths = Files.walk(mockUserHomeDir.toPath())){
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(item -> assertTrue(item.delete()));
        }
    }
}
