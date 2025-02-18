package com.github.clagomess.tomato.io.repository;

import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class TreeRepository {
    private final WorkspaceRepository workspaceRepository;
    private final CollectionRepository collectionRepository;
    private final RequestRepository requestRepository;

    public TreeRepository() {
        workspaceRepository = new WorkspaceRepository();
        collectionRepository = new CollectionRepository();
        requestRepository = new RequestRepository();
    }

    public Optional<RequestHeadDto> loadRequestHead(File file) throws IOException {
        var result = requestRepository.loadHead(file);
        if(result.isEmpty()) return Optional.empty();

        result.get().setPath(file);
        result.get().setParent(getCollectionParentTree(file.getParentFile()));

        return result;
    }

    public Stream<RequestHeadDto> getRequestList(
            CollectionTreeDto collectionParent
    ){
        return requestRepository.listRequestFiles(collectionParent.getPath())
                .map(item -> {
                    try {
                        Optional<RequestHeadDto> result = requestRepository.loadHead(item);

                        if(result.isPresent()){
                            result.get().setParent(collectionParent);
                            result.get().setPath(item);
                            return result.get();
                        }else{
                            return null;
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                ;
    }

    public CollectionTreeDto getCollectionParentTree(File dir) throws IOException {
        if(!dir.isDirectory()) throw new IOException("not a dir");

        if(dir.getName().contains("collection-")){
            var result = collectionRepository.loadTree(dir).orElseThrow();
            result.setParent(getCollectionParentTree(dir.getParentFile()));
            result.setPath(dir);
            result.setChildren(this::getCollectionChildrenTree);
            result.setRequests(this::getRequestList);

            return result;
        }else{
            return getWorkspaceCollectionTree();
        }
    }

    public Stream<CollectionTreeDto> getCollectionChildrenTree(
            CollectionTreeDto collectionStart
    ){
        return collectionRepository.listCollectionFiles(collectionStart.getPath())
                .map(item -> {
                    try {
                        Optional<CollectionTreeDto> optResult = collectionRepository.loadTree(item);

                        optResult.ifPresent(result -> {
                            result.setParent(collectionStart);
                            result.setPath(item);
                            result.setChildren(this::getCollectionChildrenTree);
                            result.setRequests(this::getRequestList);
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

    public CollectionTreeDto getWorkspaceCollectionTree() throws IOException {
        WorkspaceDto workspace = workspaceRepository.getDataSessionWorkspace();

        CollectionTreeDto root = new CollectionTreeDto();
        root.setId(workspace.getId());
        root.setName(workspace.getName());
        root.setPath(workspace.getPath());
        root.setChildren(this::getCollectionChildrenTree);
        root.setRequests(this::getRequestList);

        return root;
    }
}
