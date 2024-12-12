package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class CollectionDataService {
    private CollectionDataService() {}
    private static final CollectionDataService instance = new CollectionDataService();
    public synchronized static CollectionDataService getInstance(){
        return instance;
    }

    private final DataService dataService = DataService.getInstance();
    private final RequestDataService requestDataService = RequestDataService.getInstance();
    private final WorkspaceDataService workspaceDataService = WorkspaceDataService.getInstance();

    private File getCollectionFilePath(File collectionDir, String id){
        return new File(collectionDir, String.format(
                "collection-%s.json",
                id
        ));
    }

    public Optional<CollectionDto> load(CollectionTreeDto collection) throws IOException {
        return dataService.readFile(getCollectionFilePath(
                collection.getPath(),
                collection.getId()
        ), new TypeReference<>(){});
    }

    /**
     * @param parentPath example: workspace-xyz/
     * @param collection will write into workspace-xyz/<b>collection-zzz/collection-zzz.json</b>
     * @return collectionDir
     */
    public File save(
            File parentPath,
            CollectionDto collection
    ) throws IOException {
        var collectionDir = dataService.createDirectoryIfNotExists(new File(
                parentPath,
                String.format("collection-%s", collection.getId())
        ));

        var collectionFile = getCollectionFilePath(
                collectionDir,
                collection.getId()
        );

        dataService.writeFile(collectionFile, collection);

        return collectionDir;
    }

    public Stream<CollectionTreeDto> getCollectionChildrenTree(
            CollectionTreeDto collectionStart
    ){
        return Arrays.stream(dataService.listFiles(collectionStart.getPath()))
                .filter(File::isDirectory)
                .filter(item -> item.getName().startsWith("collection"))
                .map(item -> {
                    String id = item.getName().replace("collection-", "");
                    try {
                        Optional<CollectionTreeDto> optResult = dataService.readFile(
                                getCollectionFilePath(item, id),
                                new TypeReference<>() {}
                        );

                        optResult.ifPresent(result -> {
                            result.setParent(collectionStart);
                            result.setPath(item);
                            result.setChildren(this::getCollectionChildrenTree);
                            result.setRequests(requestDataService::getRequestList);
                        });

                        return optResult.orElse(null);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }).filter(Objects::nonNull);
    }

    public CollectionTreeDto getCollectionRootTree(
            CollectionTreeDto parent,
            String id
    ){
        return getCollectionChildrenTree(
                parent
        )
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    public CollectionTreeDto getWorkspaceCollectionTree() throws IOException {
        WorkspaceDto workspace = workspaceDataService.getDataSessionWorkspace();

        CollectionTreeDto root = new CollectionTreeDto();
        root.setId(workspace.getId());
        root.setName(workspace.getName());
        root.setPath(workspace.getPath());
        root.setChildren(this::getCollectionChildrenTree);
        root.setRequests(requestDataService::getRequestList);

        return root;
    }
}
