package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.DataSessionDto;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class DataSessionDataService {
    private DataSessionDataService() {}
    private static final DataSessionDataService instance = new DataSessionDataService();
    public synchronized static DataSessionDataService getInstance(){
        return instance;
    }

    private final DataService dataService = DataService.getInstance();

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
