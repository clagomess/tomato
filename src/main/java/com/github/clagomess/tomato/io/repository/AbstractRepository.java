package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.MetadataDto;
import com.github.clagomess.tomato.util.CacheManager;
import com.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
abstract class AbstractRepository {
    private void verifyOperationAllowedInTestMode(@Nullable File path) {
        if(path == null) return;
        assert path.getAbsolutePath().contains("target") :
                "No allowed outside dir in test mode";
    }

    protected File createDirectoryIfNotExists(File path){
        verifyOperationAllowedInTestMode(path);

        if(path.isDirectory()){
            if(log.isDebugEnabled()) log.debug("DIR: {}", path);
            return path;
        }

        if(!path.mkdirs()){
            throw new DirectoryCreateException(path);
        }

        if(log.isDebugEnabled()) log.debug("MKDIR: {}", path);
        return path;
    }

    protected <T> Optional<T> readFile(File filepath, TypeReference<T> type) throws IOException {
        if(log.isDebugEnabled()) log.debug("READ: {}", filepath);
        verifyOperationAllowedInTestMode(filepath);

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
        verifyOperationAllowedInTestMode(filepath);

        content.setUpdateTime(LocalDateTime.now());

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            ObjectMapperUtil.getInstance()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(bw, content);
        }
    }

    protected void move(File sourcePath, File targetPath) throws IOException {
        log.info("MOVE: {} -> {}", sourcePath, targetPath);
        verifyOperationAllowedInTestMode(sourcePath);
        verifyOperationAllowedInTestMode(targetPath);

        if(!targetPath.isDirectory()){
            throw new IOException("Target path is not a directory");
        }

        if(!sourcePath.renameTo(new File(targetPath, sourcePath.getName()))){
            throw new IOException(String.format(
                    "Fail to move %s to %s",
                    sourcePath,
                    targetPath
            ));
        }
    }

    protected void deleteFile(File filepath) throws IOException {
        log.info("DELETE: {}", filepath);
        verifyOperationAllowedInTestMode(filepath);

        if(!filepath.delete()){
            throw new IOException(filepath + " cannot be deleted");
        }
    }

    protected void deleteDirectory(File filepath) throws IOException {
        log.info("TO-DELETE: {}", filepath);
        verifyOperationAllowedInTestMode(filepath);

        try(Stream<Path> paths = Files.walk(filepath.toPath())){
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(item -> {
                        try {
                            deleteFile(item);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    protected static final CacheManager<String, File> cacheHomeDir = new CacheManager<>("homeDir");
    protected File getHomeDir(){
        return cacheHomeDir.get(() -> {
            if("true".equals(System.getProperty("TOMATO_AWAYS_USE_TEST_DATA"))){
                return new File("src/test/resources/com/github/clagomess/tomato/io/repository/home");
            }

            return createDirectoryIfNotExists(new File(
                    System.getProperty("user.home"),
                    ".tomato"
            ));
        });
    }

    protected File[] listFiles(File basepath){
        if(log.isDebugEnabled()) log.debug("LIST-DIR: {}", basepath);
        verifyOperationAllowedInTestMode(basepath);

        if(basepath == null || !basepath.isDirectory()) return new File[0];

        return Objects.requireNonNullElseGet(
                basepath.listFiles(),
                () -> new File[0]
        );
    }
}
