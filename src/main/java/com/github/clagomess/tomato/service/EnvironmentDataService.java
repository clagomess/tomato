package com.github.clagomess.tomato.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class EnvironmentDataService {
    private final DataService dataService;
    private final WorkspaceDataService workspaceDataService;

    public EnvironmentDataService() {
        this(
                new DataService(),
                new WorkspaceDataService()
        );
    }

    protected File getEnvironmentFile(String id) throws IOException {
        WorkspaceDto workspace = workspaceDataService.getDataSessionWorkspace();

        return new File(workspace.getPath(), String.format(
                "environment-%s.json",
                id
        ));
    }

    public Optional<EnvironmentDto> load(String id) throws IOException {
        return dataService.readFile(getEnvironmentFile(id), new TypeReference<>(){});
    }

    public File save(EnvironmentDto environmentDto) throws IOException {
        File filePath = getEnvironmentFile(environmentDto.getId());

        dataService.writeFile(filePath, environmentDto);

        return filePath;
    }

    public Stream<EnvironmentDto> list() throws IOException {
        WorkspaceDto workspace = workspaceDataService.getDataSessionWorkspace();

        return Arrays.stream(dataService.listFiles(workspace.getPath()))
                .filter(File::isFile)
                .filter(item -> item.getName().startsWith("environment"))
                .map(item -> {
                    String id = item.getName()
                            .replace("environment-", "")
                            .replace(".json", "");
                    try {
                        Optional<EnvironmentDto> optResult = load(id);
                        return optResult.orElse(null);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }).filter(Objects::nonNull);
    }
}
