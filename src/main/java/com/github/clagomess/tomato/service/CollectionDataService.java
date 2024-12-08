package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.WorkspaceDto;
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

    protected Stream<CollectionTreeDto> getCollectionTree(
            CollectionTreeDto parent,
            File basepath
    ){
        return Arrays.stream(dataService.listFiles(basepath))
                .filter(File::isDirectory)
                .filter(item -> item.getName().startsWith("collection"))
                .map(item -> {
                    String id = item.getName().replace("collection-", "");
                    try {
                        Optional<CollectionTreeDto> optResult = dataService.readFile(
                                new File(
                                        item,
                                        String.format("collection-%s.json", id)
                                ),
                                new TypeReference<>() {}
                        );

                        optResult.ifPresent(result -> {
                            result.setParent(parent);
                            result.setPath(item);
                            result.setChildren(getCollectionTree(
                                    result,
                                    item
                            ));
                            result.setRequests(requestDataService.getRequestList(
                                    result,
                                    item
                            ));
                        });

                        return optResult.orElse(null);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }).filter(Objects::nonNull);
    }

    public CollectionTreeDto getWorkspaceCollectionTree() throws IOException {
        WorkspaceDto workspace = workspaceDataService.getDataSessionWorkspace();

        CollectionTreeDto root = new CollectionTreeDto();
        root.setId(workspace.getId());
        root.setName(workspace.getName());
        root.setPath(workspace.getPath());
        root.setChildren(getCollectionTree(root, root.getPath()));
        root.setRequests(requestDataService.getRequestList(root, root.getPath()));

        return root;
    }
}
