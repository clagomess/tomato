package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.CollectionTreeDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class RequestDataService {
    private RequestDataService() {}
    private static final RequestDataService instance = new RequestDataService();
    public synchronized static RequestDataService getInstance(){
        return instance;
    }

    private final DataService dataService = DataService.getInstance();

    protected Stream<CollectionTreeDto.Request> getRequestList(
            File basepath
    ){
        return Arrays.stream(dataService.listFiles(basepath))
                .filter(File::isFile)
                .filter(item -> item.getName().startsWith("request-"))
                .map(item -> {
                    try {
                        return dataService.readFile(
                                item,
                                new TypeReference<CollectionTreeDto.Request>(){}
                        ).orElse(null);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }).filter(Objects::nonNull);
    }
}
