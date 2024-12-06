package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.*;
import com.github.clagomess.tomato.exception.DirectoryCreateException;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
@Getter
public class DataService {
    private TomatoConfigDto configuration;
    private List<WorkspaceDto> workspaces = new ArrayList<>();

    @Setter
    private WorkspaceDto currentWorkspace;

    private DataService(){}
    private static final DataService instance = new DataService();
    public synchronized static DataService getInstance(){
        return instance;
    }

    public CollectionDto getCollectionByResquestId(UUID requestId) {
        Optional<CollectionDto> optCollection = currentWorkspace.getCollections().stream()
                .filter(collection ->
                        collection.getRequests().stream()
                                .anyMatch(request -> request.getId().equals(requestId))
                ).findFirst();

        return optCollection.orElse(null);
    }

    protected File getTomatoHomeDir(){
        File homeDir = new File(System.getProperty("user.home"), ".tomato");

        if(!homeDir.isDirectory() && !homeDir.mkdir()){
            throw new DirectoryCreateException(homeDir);
        }

        return homeDir;
    }

    protected File getTomatoDataDir(){
        File dataDir = new File(
                getTomatoHomeDir(),
                "data"
        );

        if(!dataDir.isDirectory() && !dataDir.mkdir()){
            throw new DirectoryCreateException(dataDir);
        }

        return dataDir;
    }

    private File getWorkspaceDir(UUID workspaceId){
        File workspaceDir = new File(
                getTomatoDataDir(),
                String.format(
                        "workspace_%s",
                        workspaceId
                )
        );

        if(!workspaceDir.isDirectory() && !workspaceDir.mkdir()){
            throw new DirectoryCreateException(workspaceDir);
        }

        return workspaceDir;
    }

    private File getCollectionDir(UUID workspaceId, UUID collectionId){
        File collectionDir = new File(
                getWorkspaceDir(workspaceId),
                String.format(
                        "collection_%s",
                        collectionId
                )
        );

        if(!collectionDir.isDirectory() && !collectionDir.mkdir()){
            throw new DirectoryCreateException(collectionDir);
        }

        return collectionDir;
    }

    private void writeFile(File filepath, Object content) throws IOException {
        log.info("WRITE: {}", filepath);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            ObjectMapperUtil.getInstance()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(bw, content);
        }
    }

    public void writeFile(File filepath, byte[] content) throws IOException {
        log.info("WRITE: {}", filepath);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            int i = 0;
            while (i < content.length) {
                bw.write(content[i]); //@TODO: performance issue
                i++;
            }
        }
    }

    protected void saveConfig(TomatoConfigDto config) throws IOException {
        writeFile(new File(
                getTomatoHomeDir(),
                "tomato.config.json"
        ), config);
    }

    protected void saveWorkspace(List<WorkspaceDto> workspaces) throws IOException {
        writeFile(new File(
                getTomatoDataDir(),
                "workspaces.json"
        ), workspaces);
    }

    protected void saveCollection(
            UUID workspaceId,
            CollectionDto collection
    ) throws IOException {
        File collectionFilename = new File(
                getCollectionDir(workspaceId, collection.getId()),
                String.format(
                        "collection_%s.json",
                        collection.getId()
                )
        );

        writeFile(collectionFilename, collection);
    }

    protected void saveEnvironment(
            UUID workspaceId,
            List<EnvironmentDto> environments
    ) throws IOException {
        File environmentsFilename = new File(
                getWorkspaceDir(workspaceId),
                "environments.json"
        );

        writeFile(environmentsFilename, environments);
    }

    public void saveRequest(
            UUID workspaceId,
            UUID collectionId,
            RequestDto request
    ) throws IOException {
        File requestFilename = new File(
                getCollectionDir(workspaceId, collectionId),
                String.format(
                        "request_%s.json",
                        request.getId()
                )
        );

        writeFile(requestFilename, request);
    }

    private <T> Optional<T> readFile(File filepath, TypeReference<T> type) throws IOException {
        log.info("READ: {}", filepath);

        if(!filepath.isFile()) return Optional.empty();

        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            return Optional.of(
                    ObjectMapperUtil.getInstance()
                            .readValue(br, type)
            );
        }
    }

    protected List<WorkspaceDto> readAllContent() throws IOException {
        File workspaceFile = new File(
                getTomatoDataDir(),
                "workspaces.json"
        );

        Optional<List<WorkspaceDto>> workspaces = readFile(
                workspaceFile,
                new TypeReference<List<WorkspaceDto>>(){}
        );
        if(workspaces.isEmpty()) return List.of();

        for(WorkspaceDto workspace : workspaces.get()){
            File workspaceDir = getWorkspaceDir(workspace.getId());
            if(!workspaceDir.isDirectory()) continue;

            for(File collectionFile : Objects.requireNonNull(workspaceDir.listFiles())){
                if(!collectionFile.isFile() || !collectionFile.getName().startsWith("collection")) continue;
                Optional<CollectionDto> collection = readFile(
                        collectionFile,
                        new TypeReference<CollectionDto>(){}
                );
                if(collection.isEmpty()) continue;

                // get requests
                File collectionDir = getCollectionDir(
                        workspace.getId(),
                        collection.get().getId()
                );
                if(!collectionDir.isDirectory()) continue;
                for(File requestFile : Objects.requireNonNull(collectionDir.listFiles())){
                    if(!requestFile.isFile() || !requestFile.getName().startsWith("request")) continue;
                    Optional<RequestDto> request = readFile(
                            requestFile,
                            new TypeReference<RequestDto>(){}
                    );
                    if(request.isEmpty()) continue;
                    collection.get().getRequests().add(request.get());
                }

                // add collection
                workspace.getCollections().add(collection.get());
            }

            // getenvs
            File environmentFilename = new File(
                    workspaceDir,
                    "environments.json"
            );

            readFile(
                    environmentFilename,
                    new TypeReference<List<EnvironmentDto>>(){}
            ).ifPresent(workspace::setEnvironments);
        }

        return workspaces.get();
    }

    public void startLoad() throws IOException {
        this.workspaces = readAllContent();
        this.configuration = readFile(
                new File(
                        getTomatoHomeDir(),
                        "tomato.config.json"
                ),
                new TypeReference<TomatoConfigDto>(){}
        ).orElse(new TomatoConfigDto());

        if(this.workspaces == null || this.workspaces.isEmpty()){
            this.workspaces = Collections.singletonList(new WorkspaceDto());
            saveWorkspace(this.workspaces);
        }

        if(this.configuration.getCurrentWorkspaceId() == null){
            this.currentWorkspace = this.workspaces.get(0);
            this.configuration.setCurrentWorkspaceId(this.workspaces.get(0).getId());
            this.saveConfig(this.configuration);
        }else{
            this.currentWorkspace = this.workspaces.stream()
                    .filter(item -> item.getId().equals(this.configuration.getCurrentWorkspaceId()))
                    .findFirst()
                    .orElse(this.workspaces.get(0));
        }
    }
}
