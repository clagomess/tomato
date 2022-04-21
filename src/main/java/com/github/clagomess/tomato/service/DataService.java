package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.dto.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

@Slf4j
@Getter
public class DataService {
    private TomatoConfigDto configuration;
    private List<WorkspaceDto> workspaces = new ArrayList<>();

    @Setter
    private WorkspaceDto currentWorkspace;

    private DataService(){
        try {
            startLoad(); //@TODO: não deve ficar aqui
        }catch (Throwable e){
            log.error(DataService.class.getName(), e); //@TODO: melhorar e colocar um failback
        }
    }
    private static final DataService instance = new DataService();
    public synchronized static DataService getInstance(){
        return instance;
    }

    public CollectionDto getCollectionByResquestId(String requestId) {
        Optional<CollectionDto> optCollection = currentWorkspace.getCollections().stream()
                .filter(collection ->
                        collection.getRequests().stream()
                                .anyMatch(request -> request.getId().equals(requestId))
                ).findFirst();

        return optCollection.orElse(null);
    }

    protected String getTomatoHomeDir(){
        String path = System.getProperty("user.home") + File.separator + ".tomato";
        File file = new File(path);
        if(!file.isDirectory() && !file.mkdir()){
            throw new RuntimeException("Error on create tomato home dir at: " + path);
        }

        return path;
    }

    protected String getTomatoDir(String dirPath){
        String home = getTomatoHomeDir();

        String absoluteDirPath = home + File.separator + dirPath;
        File file = new File(absoluteDirPath);
        if(!file.isDirectory() && !file.mkdirs()){
            throw new RuntimeException("Error on create tomato dir at: " + absoluteDirPath);
        }

        return absoluteDirPath;
    }

    private String getWorkspaceDir(String workspaceId){
        return getTomatoDir(String.format(
                "data%sworkspace_%s",
                File.separator, workspaceId
        ));
    }

    private String getCollectionDir(String workspaceId, String collectionId){
        return getTomatoDir(String.format(
                "data%sworkspace_%s%scollection_%s",
                File.separator, workspaceId, File.separator, collectionId
        ));
    }

    private void writeFile(String filepath, Object content) throws IOException {
        String jsonContent = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(content);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            bw.write(jsonContent);
        }
    }

    public void writeFile(File file, byte[] content) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            int i = 0;
            while (i < content.length) {
                bw.write(content[i]);
                i++;
            }
        }
    }

    protected void saveTomatoConfig(TomatoConfigDto config) throws IOException {
        String homeDir = getTomatoHomeDir();
        writeFile(homeDir + File.separator + "tomato.config.json", config);
    }

    protected void saveWorkspace(List<WorkspaceDto> workspaces) throws IOException {
        String dataDir = getTomatoDir("data");
        writeFile(dataDir + File.separator + "workspaces.json", workspaces);
    }

    protected void saveCollection(String workspaceId, CollectionDto collection) throws IOException {
        String collectionFilename = getWorkspaceDir(workspaceId) + File.separator + "collection_" + collection.getId() + ".json";
        writeFile(collectionFilename, collection);
    }

    protected void saveEnvironment(String workspaceId, List<EnvironmentDto> environments) throws IOException {
        String environmentsFilename = getWorkspaceDir(workspaceId) + File.separator + "environments.json";
        writeFile(environmentsFilename, environments);
    }

    public void saveRequest(String workspaceId, String collectionId, RequestDto request) throws IOException {
        String collectionDir = getCollectionDir(workspaceId, collectionId);
        String requestFilename = collectionDir + File.separator + "request_" + request.getId() + ".json";
        writeFile(requestFilename, request);
    }

    private <T> T readFile(String filepath, TypeReference<T> type) throws IOException {
        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            int value;
            while ((value = br.read()) != -1) {
                sb.append((char) value);
            }
        }

        return new ObjectMapper().readValue(sb.toString(), type);
    }

    protected List<WorkspaceDto> readAllContent() throws IOException {
        String workspaceFile = getTomatoDir("data") + File.separator + "workspaces.json";
        if(!new File(workspaceFile).isFile()) return new ArrayList<>();

        List<WorkspaceDto> workspaces = readFile(workspaceFile, new TypeReference<List<WorkspaceDto>>(){});

        for(WorkspaceDto workspace : workspaces){
            File workspaceDir = new File(getWorkspaceDir(workspace.getId()));
            if(!workspaceDir.isDirectory()) continue;

            for(File collectionFile : Objects.requireNonNull(workspaceDir.listFiles())){
                if(!collectionFile.isFile() || !collectionFile.getName().startsWith("collection")) continue;
                CollectionDto collection = readFile(collectionFile.getAbsolutePath(), new TypeReference<CollectionDto>(){});

                // get requests
                File collectionDir = new File(getCollectionDir(workspace.getId(), collection.getId()));
                if(!collectionDir.isDirectory()) continue;
                for(File requestFile : Objects.requireNonNull(collectionDir.listFiles())){
                    if(!requestFile.isFile() || !requestFile.getName().startsWith("request")) continue;
                    RequestDto request = readFile(requestFile.getAbsolutePath(), new TypeReference<RequestDto>(){});
                    collection.getRequests().add(request);
                }

                // add collection
                workspace.getCollections().add(collection);
            }

            // getenvs
            String environmentFilename = workspaceDir + File.separator + "environments.json";
            List<EnvironmentDto> environmentList = readFile(environmentFilename, new TypeReference<List<EnvironmentDto>>(){});
            workspace.setEnvironments(environmentList);
        }

        return workspaces;
    }

    public void startLoad() throws IOException {
        this.workspaces = readAllContent();
        this.configuration = readFile(getTomatoHomeDir() + File.separator + "tomato.config.json", new TypeReference<TomatoConfigDto>(){});

        if(this.workspaces == null || this.workspaces.isEmpty()){
            this.workspaces = Collections.singletonList(new WorkspaceDto());
            //saveWorkspace(this.workspaces);
        }

        if(StringUtils.isBlank(this.configuration.getCurrentWorkspaceId())){
            this.currentWorkspace = this.workspaces.get(0);
            this.configuration.setCurrentWorkspaceId(this.workspaces.get(0).getId());
            //this.saveTomatoConfig(this.configuration);
        }else{
            this.currentWorkspace = this.workspaces.stream()
                    .filter(item -> item.getId().equals(this.configuration.getCurrentWorkspaceId()))
                    .findFirst()
                    .orElse(this.workspaces.get(0));
        }
    }
}
