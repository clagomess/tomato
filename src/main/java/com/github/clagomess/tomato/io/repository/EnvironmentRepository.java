package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import com.github.clagomess.tomato.util.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class EnvironmentRepository {
    private final Repository dataService;
    private final WorkspaceRepository workspaceDataService;
    private final WorkspaceSessionRepository workspaceSessionDataService;
    private static final CacheManager<String, Optional<EnvironmentDto>> cache = new CacheManager<>();

    public EnvironmentRepository() {
        this(
                new Repository(),
                new WorkspaceRepository(),
                new WorkspaceSessionRepository()
        );
    }

    protected File getEnvironmentFile(String id) throws IOException {
        WorkspaceDto workspace = workspaceDataService.getDataSessionWorkspace();

        return new File(workspace.getPath(), String.format(
                "environment-%s.json",
                id
        ));
    }

    public Optional<EnvironmentDto> load(String id) throws IOException {
        return cache.get(id, () -> dataService.readFile(
                getEnvironmentFile(id),
                new TypeReference<>(){}
        ));
    }

    public File save(EnvironmentDto environmentDto) throws IOException {
        File filePath = getEnvironmentFile(environmentDto.getId());

        dataService.writeFile(filePath, environmentDto);
        cache.evict(environmentDto.getId());

        return filePath;
    }

    public Stream<EnvironmentDto> list() throws IOException {
        WorkspaceDto workspace = workspaceDataService.getDataSessionWorkspace();

        return Arrays.stream(dataService.listFiles(workspace.getPath()))
                .filter(File::isFile)
                .filter(item -> item.getName().startsWith("environment"))
                .map(item -> {
                    String id = item.getName()
                            .replace("environment-", "")
                            .replace(".json", "");
                    try {
                        Optional<EnvironmentDto> optResult = load(id);
                        return optResult.orElse(null);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }).filter(Objects::nonNull);
    }

    public Optional<EnvironmentDto> getWorkspaceSessionEnvironment() throws IOException {
        WorkspaceSessionDto session = workspaceSessionDataService.load();
        if(session.getEnvironmentId() == null) return Optional.empty();

        return load(session.getEnvironmentId());
    }
}
