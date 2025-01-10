package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.DataSessionDto;
import com.github.clagomess.tomato.util.CacheManager;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class DataSessionRepository {
    private final Repository dataService;
    private static final CacheManager<String, DataSessionDto> cache = new CacheManager<>("dataSession");

    public DataSessionRepository() {
        this(new Repository());
    }

    public void save(DataSessionDto dto) throws IOException {
        File file = new File(
                dataService.getDataDir(),
                "data-session.json"
        );

        dataService.writeFile(file, dto);
        cache.evict();
    }

    public DataSessionDto load() throws IOException {
        return cache.get(() -> {
            File file = new File(
                    dataService.getDataDir(),
                    "data-session.json"
            );

            Optional<DataSessionDto> dataSession = dataService.readFile(
                    file,
                    new TypeReference<>(){}
            );

            if(dataSession.isPresent()){
                return dataSession.get();
            }else{
                var defaultDataSession = new DataSessionDto();
                save(defaultDataSession);

                return defaultDataSession;
            }
        });
    }
}
