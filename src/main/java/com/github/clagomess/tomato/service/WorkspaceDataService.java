package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.DataSessionDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.util.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class WorkspaceDataService {
    private final DataService dataService;
    private final DataSessionDataService dataSessionDataService;

    public WorkspaceDataService() {
        this(
                new DataService(),
                new DataSessionDataService()
        );
    }

    protected File getWorkspaceDirectory(String id) throws IOException {
        return dataService.createDirectoryIfNotExists(new File(
                dataService.getDataDir(),
                String.format("workspace-%s", id)
        ));
    }

    public void save(WorkspaceDto dto) throws IOException {
        File workspaceDir = getWorkspaceDirectory(dto.getId());

        dataService.writeFile(new File(
                workspaceDir,
                String.format(
                        "workspace-%s.json",
                        dto.getId()
                )
        ), dto);

        cachelistDirectories.evict();
        cache.evict(dto.getId());
    }

    private static final CacheManager<String, List<File>> cachelistDirectories = new CacheManager<>("workspaceDirs");
    private List<File> listDirectories() throws IOException {
        return cachelistDirectories.get(() -> {
            File dataDir = dataService.getDataDir();

            List<File> result = Arrays.stream(dataService.listFiles(dataDir))
                    .filter(File::isDirectory)
                    .filter(item -> item.getName().startsWith("workspace"))
                    .toList();

            if (result.isEmpty()) {
                var defaultWorkspace = new WorkspaceDto();
                defaultWorkspace.setName("Default Workspace");
                save(defaultWorkspace);

                return List.of(getWorkspaceDirectory(defaultWorkspace.getId()));
            }

            return result;
        });
    }

    private static final CacheManager<String, Optional<WorkspaceDto>> cache = new CacheManager<>();
    public Optional<WorkspaceDto> load(File workspaceDir) throws IOException {
        String id = workspaceDir.getName().replace("workspace-", "");

        return cache.get(id, () -> dataService.readFile(
                new File(
                        workspaceDir,
                        String.format("workspace-%s.json", id)
                ),
                new TypeReference<>() {}
        ));
    }

    public Stream<WorkspaceDto> list() throws IOException {
        return listDirectories().stream()
                .map(item -> {
                    try{
                        Optional<WorkspaceDto> optResult = load(item);

                        if(optResult.isPresent()){
                            optResult.get().setPath(item);
                            return optResult.get();
                        }

                        return null;
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    public WorkspaceDto getDataSessionWorkspace() throws IOException {
        DataSessionDto dataSession = dataSessionDataService.load();

        if(dataSession.getWorkspaceId() == null){
            WorkspaceDto workspace = list().findFirst().orElseThrow();
            dataSession.setWorkspaceId(workspace.getId());

            dataSessionDataService.save(dataSession);

            return workspace;
        }

        return list()
                .filter(item -> item.getId().equals(dataSession.getWorkspaceId()))
                .findFirst()
                .orElseThrow();
    }
}
