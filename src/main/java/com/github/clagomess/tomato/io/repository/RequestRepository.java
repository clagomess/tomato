package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
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
public class RequestRepository {
    private final Repository repository;

    public RequestRepository() {
        this(new Repository());
    }

    public Optional<RequestDto> load(
            RequestHeadDto request
    ) throws IOException {
        return repository.readFile(
                request.getPath(),
                new TypeReference<>() {}
        );
    }

    protected static final CacheManager<File, Optional<RequestHeadDto>> cacheHead = new CacheManager<>();
    protected Optional<RequestHeadDto> loadHead(File file) throws IOException {
        return cacheHead.get(file, () -> repository.readFile(
                file,
                new TypeReference<>() {}
        ));
    }

    public File save(File basepath, RequestDto request) throws IOException {
        File requestFile;

        if(basepath.isDirectory()){
            requestFile = new File(basepath, String.format(
                    "request-%s.json",
                    request.getId()
            ));
        }else{
            requestFile = basepath;
        }

        repository.writeFile(requestFile, request);
        cacheEvict(requestFile);

        return requestFile;
    }

    protected static final CacheManager<File, List<File>> cacheListFiles = new CacheManager<>();
    private List<File> listFiles(File rootPath) {
        return cacheListFiles.get(rootPath, () ->
                Arrays.stream(repository.listFiles(rootPath))
                        .filter(File::isFile)
                        .filter(item -> item.getName().startsWith("request-"))
                        .toList()
        );
    }

    public Stream<RequestHeadDto> getRequestList(
            CollectionTreeDto collectionParent
    ){
        return listFiles(collectionParent.getPath()).stream()
                .map(item -> {
                    try {
                        Optional<RequestHeadDto> result = loadHead(item);

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
                }).filter(Objects::nonNull);
    }

    public void cacheEvict(File requestFile) {
        cacheHead.evict(requestFile);
        cacheListFiles.evict(requestFile.getParentFile());
    }

    public void delete(RequestHeadDto head) throws IOException {
        log.debug("DELETE: {}", head.getPath());

        if(!head.getPath().delete()){
            throw new IOException(head.getPath() + " cannot be deleted");
        }

        cacheEvict(head.getPath());
    }

    public void move(
            RequestHeadDto source,
            CollectionTreeDto target
    ) throws IOException {
        log.debug("MOVE: {} -> {}", source.getPath(), target.getPath());

        if(!source.getPath().renameTo(new File(target.getPath(), source.getPath().getName()))){
            throw new IOException(String.format(
                    "Fail to move %s to %s",
                    source,
                    target
            ));
        }

        cacheEvict(source.getPath());
        cacheEvict(target.getPath());
    }
}
