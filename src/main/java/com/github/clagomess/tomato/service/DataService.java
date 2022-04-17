package com.github.clagomess.tomato.service;

import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.EnvironmentDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.dto.WorkspaceDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DataService {
    private List<WorkspaceDto> workspaces = new ArrayList<>();
    private WorkspaceDto currentWorkspace;

    private DataService(){
        mock();
    }
    private static final DataService instance = new DataService();
    public synchronized static DataService getInstance(){
        return instance;
    }

    public String getCollectionNameByResquestId(String requestId){
        return "AOBOA"; //@TODO: implements
    }

    private void mock(){
        WorkspaceDto workspace = new WorkspaceDto();
        workspace.setName("Tomato");

        List<EnvironmentDto> environmentDtoList = new ArrayList<>();
        environmentDtoList.add(new EnvironmentDto("Desenvolvimento"));
        environmentDtoList.add(new EnvironmentDto("Homologação"));
        environmentDtoList.add(new EnvironmentDto("Produção"));

        workspace.setEnvironments(environmentDtoList);

        List<RequestDto> requestDtos = new ArrayList<>();
        requestDtos.add(new RequestDto("/api/fooo"));
        requestDtos.add(new RequestDto("/api/bar"));
        requestDtos.add(new RequestDto("/api/aboa"));

        List<CollectionDto> collectionDtos = new ArrayList<>();
        collectionDtos.add(new CollectionDto("FOO", requestDtos));
        collectionDtos.add(new CollectionDto("BAR", requestDtos));
        workspace.setCollections(collectionDtos);

        workspaces.add(workspace);
        currentWorkspace = workspace;
    }
}
