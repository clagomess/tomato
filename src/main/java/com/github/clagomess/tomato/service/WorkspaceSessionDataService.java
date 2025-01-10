package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import com.github.clagomess.tomato.util.CacheManager;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class WorkspaceSessionDataService {
    private final DataService dataService;
    private final WorkspaceDataService workspaceDataService;
    private static final CacheManager<String, WorkspaceSessionDto> cache = new CacheManager<>("workspaceSession");

    public WorkspaceSessionDataService() {
        this(
                new DataService(),
                new WorkspaceDataService()
        );
    }

    protected File getWorkspaceSessionFile() throws IOException {
        WorkspaceDto workspace = workspaceDataService.getDataSessionWorkspace();

        return new File(workspace.getPath(), "workspace-session.json");
    }

    public WorkspaceSessionDto load() throws IOException {
        return cache.get(() -> {
            Optional<WorkspaceSessionDto> opt = dataService.readFile(
                    getWorkspaceSessionFile(),
                    new TypeReference<>() {
                    }
            );

            return opt.orElseGet(WorkspaceSessionDto::new);
        });
    }

    public File save(WorkspaceSessionDto dto) throws IOException {
        File filePath = getWorkspaceSessionFile();

        dataService.writeFile(filePath, dto);
        cache.evict();

        return filePath;
    }
}
