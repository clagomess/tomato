package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
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
abstract class AbstractRepository {
    private final String msgNotAllowedOnTest = "No allowed outside dir in test mode";

    protected File createDirectoryIfNotExists(File path){
        assert path.getAbsolutePath().contains("target") : msgNotAllowedOnTest;

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
        assert filepath.getAbsolutePath().contains("target") : msgNotAllowedOnTest;

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
        assert filepath.getAbsolutePath().contains("target") : msgNotAllowedOnTest;

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

    protected File[] listFiles(File basepath){
        log.debug("LIST-DIR: {}", basepath);

        if(basepath == null || !basepath.isDirectory()) return new File[0];

        assert basepath.getAbsolutePath().contains("target") : msgNotAllowedOnTest;

        return Objects.requireNonNullElseGet(
                basepath.listFiles(),
                () -> new File[0]
        );
    }
}
