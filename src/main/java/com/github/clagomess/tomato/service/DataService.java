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
        String workspaceDir = getTomatoDir(String.format("data%sworkspace_%s", File.separator, workspaceId));
        String collectionFilename = workspaceDir + File.separator + "collection_" + collection.getId() + ".json";
        writeFile(collectionFilename, collection);
    }

    protected void saveEnvironment(String workspaceId, List<EnvironmentDto> environments) throws IOException {
        String workspaceDir = getTomatoDir(String.format("data%sworkspace_%s", File.separator, workspaceId));
        String environmentsFilename = workspaceDir + File.separator + "environments.json";
        writeFile(environmentsFilename, environments);
    }

    protected void saveRequest(String workspaceId, String collectionId, RequestDto request) throws IOException {
        String collectionDir = getTomatoDir(String.format(
                "data%sworkspace_%s%scollection_%s",
                File.separator, workspaceId, File.separator, collectionId
        ));
        String requestFilename = collectionDir + File.separator + "request_" + request.getId() + ".json";
        writeFile(requestFilename, request);
    }

    private StringBuilder readFile(String filepath) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filepath)); //@TODO: melhorar com try-gato
        int value;
        while ((value = br.read()) != -1) {
            sb.append((char) value);
        }
        return sb;
    }

    protected List<WorkspaceDto> readAllContent() throws IOException {
        List<WorkspaceDto> content = new ArrayList<>();

        String workspaceFile = getTomatoDir("data") + File.separator + "workspaces.json"; //@TODO: check exists

        String jsonContent = readFile(workspaceFile).toString();
        content = new ObjectMapper().readValue(jsonContent, new TypeReference<List<WorkspaceDto>>(){});

        // ------------------
        for(WorkspaceDto workspace : content){
            String workspaceDir = getTomatoDir(String.format("data%sworkspace_%s", File.separator, workspace.getId()));
            for(File collectionFile : new File(workspaceDir).listFiles()){
                if(!collectionFile.isFile() || !collectionFile.getName().startsWith("collection")) continue;
                String collCotent = readFile(collectionFile.getAbsolutePath()).toString();
                CollectionDto coll = new ObjectMapper().readValue(collCotent, CollectionDto.class);

                // ------ request
                String collectionDir = getTomatoDir(String.format(
                        "data%sworkspace_%s%scollection_%s",
                        File.separator, workspace.getId(), File.separator, coll.getId()
                ));
                for(File requestFile : new File(collectionDir).listFiles()){
                    if(!requestFile.isFile() || !requestFile.getName().startsWith("request")) continue;
                    String reqCotent = readFile(requestFile.getAbsolutePath()).toString();
                    RequestDto req = new ObjectMapper().readValue(reqCotent, RequestDto.class);
                    coll.getRequests().add(req);
                }

                //-- vrau
                workspace.getCollections().add(coll);
            }

            // env
            String envConte = readFile(workspaceDir + File.separator + "environments.json").toString();
            List<EnvironmentDto> env_aa = new ObjectMapper().readValue(envConte, new TypeReference<List<EnvironmentDto>>(){});
            workspace.setEnvironments(env_aa);
        }

        return null;
    }
}
