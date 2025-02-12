package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class RequestRepository extends AbstractRepository {

    public Optional<RequestDto> load(
            RequestHeadDto request
    ) throws IOException {
        return readFile(
                request.getPath(),
                new TypeReference<>() {}
        );
    }

    protected Optional<RequestHeadDto> loadHead(File file) throws IOException {
        return readFile(file, new TypeReference<>() {});
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

        writeFile(requestFile, request);

        return requestFile;
    }

    private Stream<File> listRequestFiles(File rootPath) {
        return Arrays.stream(listFiles(rootPath)).parallel()
                .filter(File::isFile)
                .filter(item -> item.getName().startsWith("request-"));
    }

    public Stream<RequestHeadDto> getRequestList(
            CollectionTreeDto collectionParent
    ){
        return listRequestFiles(collectionParent.getPath())
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
                })
                .filter(Objects::nonNull)
                .sorted()
                ;
    }

    public void delete(RequestHeadDto head) throws IOException {
        deleteFile(head.getPath());
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
    }
}
