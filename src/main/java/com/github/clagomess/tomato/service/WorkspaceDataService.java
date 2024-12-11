package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.DataSessionDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class WorkspaceDataService {
    private WorkspaceDataService() {}
    private static final WorkspaceDataService instance = new WorkspaceDataService();
    public synchronized static WorkspaceDataService getInstance(){
        return instance;
    }

    private final DataService dataService = DataService.getInstance();
    private final DataSessionDataService dataSessionDataService = DataSessionDataService.getInstance();

    protected File getWorkspaceDirectory(String id) throws IOException {
        return dataService.createDirectoryIfNotExists(new File(
                dataService.getDataDir(),
                String.format("workspace-%s", id)
        ));
    }

    public void saveWorkspace(WorkspaceDto dto) throws IOException {
        File workspaceDir = getWorkspaceDirectory(dto.getId());

        dataService.writeFile(new File(
                workspaceDir,
                String.format(
                        "workspace-%s.json",
                        dto.getId()
                )
        ), dto);
    }

    public Stream<WorkspaceDto> listWorkspaces() throws IOException {
        File dataDir = dataService.getDataDir();

        if(Arrays.stream(dataService.listFiles(dataDir))
                .filter(File::isDirectory)
                .filter(item -> item.getName().startsWith("workspace"))
                .findFirst().isEmpty()
        ){
            var defaultWorkspace = new WorkspaceDto();
            defaultWorkspace.setName("Default Workspace");
            saveWorkspace(defaultWorkspace);
        }

        return Arrays.stream(dataService.listFiles(dataDir))
                .filter(File::isDirectory)
                .filter(item -> item.getName().startsWith("workspace"))
                .map(item -> {
                    String id = item.getName().replace("workspace-", "");

                    try{
                        Optional<WorkspaceDto> optResult = dataService.readFile(
                                new File(
                                        item,
                                        String.format("workspace-%s.json", id)
                                ),
                                new TypeReference<>() {}
                        );

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
        DataSessionDto dataSession = dataSessionDataService.getDataSession();

        if(dataSession.getWorkspaceId() == null){
            WorkspaceDto workspace = listWorkspaces().findFirst().orElseThrow();
            dataSession.setWorkspaceId(workspace.getId());

            dataSessionDataService.saveDataSession(dataSession);

            return workspace;
        }

        return listWorkspaces()
                .filter(item -> item.getId().equals(dataSession.getWorkspaceId()))
                .findFirst()
                .orElseThrow();
    }
}
