package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.util.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class EnvironmentRepository extends AbstractRepository {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceSessionRepository workspaceSessionRepository;
    protected static final CacheManager<String, Optional<EnvironmentDto>> cache = new CacheManager<>();

    public EnvironmentRepository() {
        this(
                new WorkspaceRepository(),
                new WorkspaceSessionRepository()
        );
    }

    protected File getEnvironmentFile(String id) throws IOException {
        WorkspaceDto workspace = workspaceRepository.getDataSessionWorkspace();

        return new File(workspace.getPath(), String.format(
                "environment-%s.json",
                id
        ));
    }

    public Optional<EnvironmentDto> load(String id) throws IOException {
        return cache.get(id, () -> readFile(
                getEnvironmentFile(id),
                new TypeReference<>(){}
        ));
    }

    protected Optional<EnvironmentHeadDto> loadHead(String id) throws IOException {
        return readFile(
                getEnvironmentFile(id),
                new TypeReference<>(){}
        );
    }

    public File save(EnvironmentDto environmentDto) throws IOException {
        File filePath = getEnvironmentFile(environmentDto.getId());

        writeFile(filePath, environmentDto);
        cache.evict(environmentDto.getId());
        cacheListHead.evict();

        return filePath;
    }

    public void delete(EnvironmentDto environment) throws IOException {
        File filePath = getEnvironmentFile(environment.getId());
        deleteFile(filePath);
        cache.evict(environment.getId());
        cacheListHead.evict();
    }

    protected static final CacheManager<String, List<EnvironmentHeadDto>> cacheListHead = new CacheManager<>("listHead");
    public List<EnvironmentHeadDto> listHead() throws IOException {
        WorkspaceDto workspace = workspaceRepository.getDataSessionWorkspace();

        return cacheListHead.get(() -> Arrays.stream(listFiles(workspace.getPath()))
                .filter(File::isFile)
                .filter(item -> item.getName().startsWith("environment"))
                .map(item -> {
                    String id = item.getName()
                            .replace("environment-", "")
                            .replace(".json", "");
                    try {
                        Optional<EnvironmentHeadDto> optResult = loadHead(id);
                        return optResult.orElse(null);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList()
        );
    }

    public Optional<EnvironmentDto> getWorkspaceSessionEnvironment() throws IOException {
        WorkspaceSessionDto session = workspaceSessionRepository.load();
        if(session.getEnvironmentId() == null) return Optional.empty();

        return load(session.getEnvironmentId());
    }
}
