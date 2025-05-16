package io.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.clagomess.tomato.dto.data.MetadataDto;
import io.github.clagomess.tomato.util.CacheManager;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
abstract class AbstractRepository {
    private final ObjectMapperUtil objectMapper;

    public AbstractRepository() {
        this.objectMapper = ObjectMapperUtil.getInstance();
    }

    private void verifyOperationAllowedInTestMode(@Nullable File path) {
        if(path == null) return;

        assert path.getAbsolutePath().contains("target")
                : "No allowed outside dir in test mode";
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
        if(log.isDebugEnabled()) log.debug("READING: {}", filepath);
        verifyOperationAllowedInTestMode(filepath);

        if(!filepath.isFile()){
            if(log.isDebugEnabled()) log.debug("- file not exist");
            return Optional.empty();
        }

        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            if(log.isDebugEnabled()) log.debug("- readed");
            return Optional.of(
                    objectMapper.readValue(br, type)
            );
        }
    }

    protected <T extends MetadataDto> boolean writeFile(
            File filepath,
            TypeReference<T> type,
            T content
    ) throws IOException {
        log.info("WRITING: {}", filepath);
        verifyOperationAllowedInTestMode(filepath);

        if(readFile(filepath, type).filter(content::equals).isPresent()){
            log.info("- not writed - Same Content");
            return false;
        }

        content.setUpdateTime(LocalDateTime.now());

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(bw, content);
            log.info("- writed");
            return true;
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
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    });
        }
    }

    protected static final CacheManager<String, File> cacheHomeDir = new CacheManager<>("homeDir");
    protected File getHomeDir(){
        return cacheHomeDir.get(() -> {
            if("true".equals(System.getProperty("TOMATO_AWAYS_USE_TEST_DATA"))){
                return new File("src/test/resources/io/github/clagomess/tomato/io/repository/home");
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
