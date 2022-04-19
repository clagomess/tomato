package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.EnvironmentDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.dto.WorkspaceDto;
import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        Optional<CollectionDto> optCollection = currentWorkspace.getCollections().stream()
                .filter(collection ->
                        collection.getRequests().stream()
                                .anyMatch(request -> request.getId().equals(requestId))
                ).findFirst();

        return optCollection.map(CollectionDto::getName).orElse(null);
    }

    private void mock(){ //@TODO: must die
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
        BufferedWriter bw = new BufferedWriter(new FileWriter(filepath)); //@TODO: melhorar com try-gato
        bw.write(jsonContent);
        bw.close();
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

    protected void saveRequest(String workspaceId, String collectionId, RequestDto request) throws IOException {
        String collectionDir = getCollectionDir(workspaceId, collectionId);
        String requestFilename = collectionDir + File.separator + "request_" + request.getId() + ".json";
        writeFile(requestFilename, request);
    }

    private <T> T readFile(String filepath, TypeReference<T> type) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filepath)); //@TODO: melhorar com try-gato
        int value;
        while ((value = br.read()) != -1) {
            sb.append((char) value);
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

        return null;
    }
}
