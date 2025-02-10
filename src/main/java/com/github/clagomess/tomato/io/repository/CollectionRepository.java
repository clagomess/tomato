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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class CollectionRepository extends AbstractRepository {
    private final RequestRepository requestRepository;
    private final WorkspaceRepository workspaceRepository;

    public CollectionRepository() {
        this(
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
        return cacheCollection.get(collection.getId(), () -> readFile(getCollectionFilePath(
                collection.getPath(),
                collection.getId()
        ), new TypeReference<>(){}));
    }

    protected static final CacheManager<String, Optional<CollectionTreeDto>> cacheCollectionTree = new CacheManager<>();
    protected Optional<CollectionTreeDto> load(File collectionDir) throws IOException {
        String id = collectionDir.getName().replace("collection-", "");

        return cacheCollectionTree.get(id, () -> readFile(
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
        var collectionDir = createDirectoryIfNotExists(new File(
                parentPath,
                String.format("collection-%s", collection.getId())
        ));

        var collectionFile = getCollectionFilePath(
                collectionDir,
                collection.getId()
        );

        writeFile(collectionFile, collection);
        cacheEvict(collection.getId(), collectionDir);

        return collectionDir;
    }

    protected static final CacheManager<File, List<File>> cacheListFiles = new CacheManager<>();
    protected List<File> listCollectionFiles(File rootPath) {
        return cacheListFiles.get(rootPath, () ->
                Arrays.stream(listFiles(rootPath))
                        .filter(File::isDirectory)
                        .filter(item -> item.getName().startsWith("collection"))
                        .toList()
        );
    }

    public Stream<CollectionTreeDto> getCollectionChildrenTree(
            CollectionTreeDto collectionStart
    ){
        return listCollectionFiles(collectionStart.getPath()).stream()
                .map(item -> {
                    try {
                        Optional<CollectionTreeDto> optResult = load(item);

                        optResult.ifPresent(result -> {
                            result.setParent(collectionStart);
                            result.setPath(item);
                            result.setChildren(this::getCollectionChildrenTree);
                            result.setRequests(requestRepository::getRequestList);
                        });

                        return optResult.orElse(null);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted();
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
        WorkspaceDto workspace = workspaceRepository.getDataSessionWorkspace();

        CollectionTreeDto root = new CollectionTreeDto();
        root.setId(workspace.getId());
        root.setName(workspace.getName());
        root.setPath(workspace.getPath());
        root.setChildren(this::getCollectionChildrenTree);
        root.setRequests(requestRepository::getRequestList);

        return root;
    }

    private void cacheEvict(String id, File collectionDir) {
        cacheCollection.evict(id);
        cacheCollectionTree.evict(id);
        cacheListFiles.evict(collectionDir.getParentFile());
        cacheListFiles.evict(collectionDir);
    }

    public void move(
            CollectionTreeDto source,
            CollectionTreeDto target
    ) throws IOException {
        log.debug("MOVE: {} -> {}", source.getPath(), target.getPath());

        try(Stream<Path> paths = Files.walk(source.getPath().toPath())){
            paths.map(Path::toFile).forEach(requestRepository::cacheEvict);
        }

        try(Stream<Path> paths = Files.walk(target.getPath().toPath())){
            paths.map(Path::toFile).forEach(requestRepository::cacheEvict);
        }

        if(!source.getPath().renameTo(new File(target.getPath(), source.getPath().getName()))){
            throw new IOException(String.format(
                    "Fail to move %s to %s",
                    source.getPath(),
                    target.getPath()
            ));
        }

        cacheEvict(source.getId(), source.getPath());
        cacheEvict(target.getId(), target.getPath());
    }

    public void delete(CollectionTreeDto tree) throws IOException {
        log.info("TO-DELETE: {}", tree.getPath());

        try(Stream<Path> paths = Files.walk(tree.getPath().toPath())){
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(item -> {
                        try {
                            deleteFile(item);
                            requestRepository.cacheEvict(item);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        cacheEvict(tree.getId(), tree.getPath());
    }
}
