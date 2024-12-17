package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.DataSessionDto;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class DataSessionDataService {
    private final DataService dataService;

    public DataSessionDataService() {
        this(new DataService());
    }

    public void saveDataSession(DataSessionDto dto) throws IOException {
        File file = new File(
                dataService.getDataDir(),
                "data-session.json"
        );

        dataService.writeFile(file, dto);
    }

    public DataSessionDto getDataSession() throws IOException {
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
            saveDataSession(defaultDataSession);

            return defaultDataSession;
        }
    }
}
