package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.CollectionDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.util.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class CollectionRepository {
    private final Repository dataService;
    private final RequestRepository requestDataService;
    private final WorkspaceRepository workspaceDataService;

    public CollectionRepository() {
        this(
                new Repository(),
                new RequestRepository(),
                new WorkspaceRepository()
        );
    }

    protected File getCollectionFilePath(File collectionDir, String id){
        return new File(collectionDir, String.format(
                "collection-%s.json",
                id
        ));
    }

    protected static final CacheManager<String, Optional<CollectionDto>> cacheCollection = new CacheManager<>();
    public Optional<CollectionDto> load(CollectionTreeDto collection) throws IOException {
        return cacheCollection.get(collection.getId(), () -> dataService.readFile(getCollectionFilePath(
                collection.getPath(),
                collection.getId()
        ), new TypeReference<>(){}));
    }

    protected static final CacheManager<String, Optional<CollectionTreeDto>> cacheCollectionTree = new CacheManager<>();
    protected Optional<CollectionTreeDto> load(File collectionDir) throws IOException {
        String id = collectionDir.getName().replace("collection-", "");

        return cacheCollectionTree.get(id, () -> dataService.readFile(
                getCollectionFilePath(collectionDir, id),
                new TypeReference<>() {}
        ));
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
        cacheCollection.evict(collection.getId());
        cacheCollectionTree.evict(collection.getId());
        cacheListFiles.evict(parentPath);
        cacheListFiles.evict(collectionDir);

        return collectionDir;
    }

    protected static final CacheManager<File, List<File>> cacheListFiles = new CacheManager<>();
    protected List<File> listFiles(File rootPath) {
        return cacheListFiles.get(rootPath, () ->
                Arrays.stream(dataService.listFiles(rootPath))
                        .filter(File::isDirectory)
                        .filter(item -> item.getName().startsWith("collection"))
                        .toList()
        );
    }

    public Stream<CollectionTreeDto> getCollectionChildrenTree(
            CollectionTreeDto collectionStart
    ){
        return listFiles(collectionStart.getPath()).stream()
                .map(item -> {
                    try {
                        Optional<CollectionTreeDto> optResult = load(item);

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
