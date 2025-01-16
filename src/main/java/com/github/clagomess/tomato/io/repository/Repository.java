package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.ConfigurationDto;
import com.github.clagomess.tomato.dto.data.MetadataDto;
import com.github.clagomess.tomato.exception.DirectoryCreateException;
import com.github.clagomess.tomato.util.CacheManager;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class Repository { //@TODO: make package private

    protected File createDirectoryIfNotExists(File path){
        if(path.isDirectory()){
            log.debug("DIR: {}", path);
            return path;
        }

        if(!path.mkdirs()){
            throw new DirectoryCreateException(path);
        }

        log.debug("MKDIR: {}", path);
        return path;
    }

    protected <T> Optional<T> readFile(File filepath, TypeReference<T> type) throws IOException {
        log.debug("READ: {}", filepath);

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

    protected static final CacheManager<String, File> cacheHomeDir = new CacheManager<>("homeDir");
    protected File getHomeDir(){
        return cacheHomeDir.get(() -> createDirectoryIfNotExists(new File(
                System.getProperty("user.home"),
                ".tomato"
        )));
    }

    protected static final CacheManager<String, ConfigurationDto> cacheConfiguration = new CacheManager<>("configuration");
    protected ConfigurationDto getConfiguration() throws IOException {
        return cacheConfiguration.get(() -> {
            var file = new File(
                    getHomeDir(),
                    "configuration.json"
            );

            Optional<ConfigurationDto> configuration = readFile(
                    file,
                    new TypeReference<>() {
                    }
            );

            if (configuration.isPresent()) {
                return configuration.get();
            } else {
                var defaultConfiguration = new ConfigurationDto();
                defaultConfiguration.setDataDirectory(new File(
                        getHomeDir(),
                        "data"
                ));

                writeFile(file, defaultConfiguration);

                return defaultConfiguration;
            }
        });
    }

    protected static final CacheManager<String, File> cacheDatadir = new CacheManager<>("dataDir");
    protected File getDataDir() throws IOException {
        return cacheDatadir.get(() -> createDirectoryIfNotExists(
                getConfiguration().getDataDirectory()
        ));
    }

    protected File[] listFiles(File basepath){
        log.debug("LIST-DIR: {}", basepath);

        if(basepath == null || !basepath.isDirectory()) return new File[0];

        return Objects.requireNonNullElseGet(
                basepath.listFiles(),
                () -> new File[0]
        );
    }

    public void move(File source, File target) throws IOException {
        log.debug("MOVE: {} -> {}", source, target);

        if(!target.isDirectory()) throw new IOException(target + " is not a directory");

        if(!source.renameTo(new File(target, source.getName()))){
            throw new IOException(String.format(
                    "Fail to move %s to %s",
                    source,
                    target
            ));
        }
    }
}
