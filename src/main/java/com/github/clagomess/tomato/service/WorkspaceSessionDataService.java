package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class WorkspaceSessionDataService {
    private final DataService dataService;
    private final WorkspaceDataService workspaceDataService;

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

    public Optional<WorkspaceSessionDto> load() throws IOException {
        return dataService.readFile(getWorkspaceSessionFile(), new TypeReference<>(){});
    }

    public File save(WorkspaceSessionDto dto) throws IOException {
        File filePath = getWorkspaceSessionFile();

        dataService.writeFile(filePath, dto);

        return filePath;
    }
}
