package io.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.clagomess.tomato.dto.RequestPropertiesDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.util.DateUtils;
import io.github.clagomess.tomato.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;
import static io.github.clagomess.tomato.enums.BodyTypeEnum.BINARY;
import static io.github.clagomess.tomato.enums.BodyTypeEnum.MULTIPART_FORM;
import static io.github.clagomess.tomato.io.repository.EnvironmentRepository.SYSENV_COLLECTION_FILE_DIR_KEY;
import static io.github.clagomess.tomato.util.FileUtils.COLLECTION_FILES_DIR;

@Slf4j
public class RequestRepository extends AbstractRepository {


    public Optional<RequestDto> load(
            RequestHeadDto request
    ) throws IOException {
        return readFile(
                request.getPath(),
                new TypeReference<>() {}
        );
    }

    protected Optional<RequestHeadDto> loadHead(File file) throws IOException {
        return readFile(file, new TypeReference<>() {});
    }

    public File save(File basepath, RequestDto request) throws IOException {
        File requestFile;

        if(basepath.isDirectory()){
            requestFile = new File(basepath, String.format(
                    "request-%s.json",
                    request.getId()
            ));
        }else{
            requestFile = basepath;
        }

        writeFile(requestFile, new TypeReference<>(){}, request);

        return requestFile;
    }

    protected Stream<File> listRequestFiles(File rootPath) {
        return Arrays.stream(listFiles(rootPath)).parallel()
                .filter(File::isFile)
                .filter(item -> item.getName().startsWith("request-"));
    }

    public void delete(RequestHeadDto head) throws IOException {
        deleteFile(head.getPath());
    }

    public void move(
            RequestHeadDto source,
            CollectionTreeDto target
    ) throws IOException {
        RequestDto request = load(source).orElseThrow();

        Function<String, Optional<File>> getFileSource = value -> {
            if(StringUtils.isBlank(value)) return Optional.empty();
            if(!value.contains(SYSENV_COLLECTION_FILE_DIR_KEY)) return Optional.empty();

            var fileSource = new File(value.replace(
                    "{{" + SYSENV_COLLECTION_FILE_DIR_KEY + "}}",
                    source.getPath().getParent() + "/" + COLLECTION_FILES_DIR
            ));

            if(fileSource.exists()) return Optional.of(fileSource);

            return Optional.empty();
        };

        Supplier<File> getFileTarget = () -> {
            var targetDir = new File(target.getPath(), COLLECTION_FILES_DIR);
            targetDir.mkdirs();
            return targetDir;
        };

        // move multiparti-body
        if(request.getBody().getType() == MULTIPART_FORM) {
            for (var item : request.getBody().getMultiPartForm()) {
                if (item.getType() != FILE) continue;
                Optional<File> fileSource = getFileSource.apply(item.getValue());
                if (fileSource.isPresent()) move(fileSource.get(), getFileTarget.get());
            }
        }

        // move binary-body
        if(request.getBody().getType() == BINARY){
            Optional<File> fileSource = getFileSource.apply(
                    request.getBody().getBinary().getFile()
            );
            if(fileSource.isPresent()) move(fileSource.get(), getFileTarget.get());
        }

        move(source.getPath(), target.getPath());
    }

    public RequestPropertiesDto properties(
            RequestHeadDto request
    ){
        return new RequestPropertiesDto(
                request.getId().toString(),
                request.getName(),
                request.getPath().getAbsolutePath(),
                FileUtils.humanReadableByteCountBinary(request.getPath().length()),
                DateUtils.epochMilliToISO(request.getPath().lastModified())
        );
    }
}
