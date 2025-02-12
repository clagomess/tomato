package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.DataSessionDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.util.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class WorkspaceRepository extends AbstractRepository {
    private final ConfigurationRepository configurationRepository;
    private final DataSessionRepository dataSessionRepository;

    public WorkspaceRepository() {
        this(
                new ConfigurationRepository(),
                new DataSessionRepository()
        );
    }

    protected File getWorkspaceDirectory(String id) throws IOException {
        return createDirectoryIfNotExists(new File(
                configurationRepository.getDataDir(),
                String.format("workspace-%s", id)
        ));
    }

    public void save(WorkspaceDto dto) throws IOException {
        File workspaceDir = getWorkspaceDirectory(dto.getId());

        writeFile(new File(
                workspaceDir,
                String.format(
                        "workspace-%s.json",
                        dto.getId()
                )
        ), dto);

        cacheList.evict();
    }

    protected List<File> listDirectories() throws IOException {
        File dataDir = configurationRepository.getDataDir();

        List<File> result = Arrays.stream(listFiles(dataDir)).parallel()
                .filter(File::isDirectory)
                .filter(item -> item.getName().startsWith("workspace"))
                .toList();

        if (result.isEmpty()) {
            var defaultWorkspace = new WorkspaceDto();
            defaultWorkspace.setName("Default Workspace");
            save(defaultWorkspace);

            return List.of(getWorkspaceDirectory(defaultWorkspace.getId()));
        }

        return result;
    }

    protected Optional<WorkspaceDto> load(File workspaceDir) throws IOException {
        String id = workspaceDir.getName().replace("workspace-", "");

        return readFile(
                new File(
                        workspaceDir,
                        String.format("workspace-%s.json", id)
                ),
                new TypeReference<>() {}
        );
    }

    protected static final CacheManager<String, List<WorkspaceDto>> cacheList = new CacheManager<>("workspaces");
    public List<WorkspaceDto> list() throws IOException {
        return cacheList.getSynchronized(() -> listDirectories().stream()
                .map(item -> {
                    try{
                        Optional<WorkspaceDto> optResult = load(item);

                        if(optResult.isPresent()){
                            optResult.get().setPath(item);
                            return optResult.get();
                        }

                        return null;
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .toList()
        );
    }

    public WorkspaceDto getDataSessionWorkspace() throws IOException {
        DataSessionDto dataSession = dataSessionRepository.load();

        if(dataSession.getWorkspaceId() == null){
            WorkspaceDto workspace = list().stream().findFirst().orElseThrow();
            dataSession.setWorkspaceId(workspace.getId());

            dataSessionRepository.save(dataSession);

            return workspace;
        }

        return list().stream()
                .filter(item -> item.getId().equals(dataSession.getWorkspaceId()))
                .findFirst()
                .orElseThrow();
    }
}
