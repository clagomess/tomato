package com.github.clagomess.tomato.service;

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
public class RequestDataService {
    private RequestDataService() {}
    private static final RequestDataService instance = new RequestDataService();
    public synchronized static RequestDataService getInstance(){
        return instance;
    }

    private final DataService dataService = DataService.getInstance();

    public Optional<RequestDto> load(
            RequestHeadDto request
    ) throws IOException {
        return dataService.readFile(
                request.getPath(),
                new TypeReference<>() {}
        );
    }

    public File save(File basepath, RequestDto request) throws IOException {
        if(basepath.isDirectory()){
            var file = new File(basepath, String.format(
                    "request-%s.json",
                    request.getId()
            ));

            dataService.writeFile(file, request);
            return file;
        }else{
            dataService.writeFile(basepath, request);
            return basepath;
        }
    }

    public Stream<RequestHeadDto> getRequestList(
            CollectionTreeDto collectionParent
    ){
        return Arrays.stream(dataService.listFiles(collectionParent.getPath()))
                .filter(File::isFile)
                .filter(item -> item.getName().startsWith("request-"))
                .map(item -> {
                    try {
                        Optional<RequestHeadDto> result = dataService.readFile(
                                item,
                                new TypeReference<>() {}
                        );

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
}
