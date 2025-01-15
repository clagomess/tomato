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
    private final Repository repository;
    protected static final CacheManager<String, DataSessionDto> cache = new CacheManager<>("dataSession");

    public DataSessionRepository() {
        this(new Repository());
    }

    protected File getDataSessionFile() throws IOException {
        return new File(
                repository.getDataDir(),
                "data-session.json"
        );
    }

    public void save(DataSessionDto dto) throws IOException {
        repository.writeFile(getDataSessionFile(), dto);
        cache.evict();
    }

    public DataSessionDto load() throws IOException {
        // @TODO: impl syncronized read/write. check other with same scenario
        return cache.get(() -> {
            Optional<DataSessionDto> dataSession = repository.readFile(
                    getDataSessionFile(),
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
