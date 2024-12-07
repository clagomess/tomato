package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.ConfigurationDto;
import com.github.clagomess.tomato.dto.MetadataDto;
import com.github.clagomess.tomato.exception.DirectoryCreateException;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Getter
public class DataService {
    private DataService(){}
    private static final DataService instance = new DataService();
    public synchronized static DataService getInstance(){
        return instance;
    }

    protected <T> Optional<T> readFile(File filepath, TypeReference<T> type) throws IOException {
        log.info("READ: {}", filepath);

        if(!filepath.isFile()) return Optional.empty();

        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            return Optional.of(
                    ObjectMapperUtil.getInstance()
                            .readValue(br, type)
            );
        }
    }

    protected <T extends MetadataDto> void writeFile(
            File filepath,
            T content
    ) throws IOException {
        log.info("WRITE: {}", filepath);

        content.setUpdateTime(LocalDateTime.now());

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            ObjectMapperUtil.getInstance()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(bw, content);
        }
    }

    protected File getHomeDir(){
        File homeDir = new File(System.getProperty("user.home"), ".tomato");
        log.info("HOME-DIR: {}", homeDir);

        if(!homeDir.isDirectory() && !homeDir.mkdir()){
            throw new DirectoryCreateException(homeDir);
        }

        return homeDir;
    }

    protected ConfigurationDto getConfiguration() throws IOException {
        var file = new File(
                getHomeDir(),
                "configuration.json"
        );

        Optional<ConfigurationDto> configuration = readFile(
                file,
                new TypeReference<>(){}
        );

        if(configuration.isPresent()){
            return configuration.get();
        }else{
            var defaultConfiguration = new ConfigurationDto();
            defaultConfiguration.setDataDirectory(new File(
                    getHomeDir(),
                    "data"
            ));

            writeFile(file, defaultConfiguration);

            return defaultConfiguration;
        }
    }

    protected File getDataDir() throws IOException {
        File dataDir = getConfiguration().getDataDirectory();

        if(!dataDir.isDirectory() && !dataDir.mkdir()){
            throw new DirectoryCreateException(dataDir);
        }

        return dataDir;
    }

    protected File[] listFiles(File basepath){
        if(basepath == null || !basepath.isDirectory()) return new File[0];

        return Objects.requireNonNullElseGet(
                basepath.listFiles(),
                () -> new File[0]
        );
    }
}
