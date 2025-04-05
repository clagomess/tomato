package io.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import io.github.clagomess.tomato.util.CacheManager;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class WorkspaceSessionRepository extends AbstractRepository {
    private final WorkspaceRepository workspaceRepository;
    protected static final CacheManager<File, WorkspaceSessionDto> cache = new CacheManager<>();

    public WorkspaceSessionRepository() {
        this(
                new WorkspaceRepository()
        );
    }

    protected File getWorkspaceSessionFile() throws IOException {
        WorkspaceDto workspace = workspaceRepository.getDataSessionWorkspace();

        return new File(workspace.getPath(), "workspace-session.json");
    }

    public WorkspaceSessionDto load() throws IOException {
        File filePath = getWorkspaceSessionFile();

        return cache.get(filePath, () -> {
            Optional<WorkspaceSessionDto> opt = readFile(
                    getWorkspaceSessionFile(),
                    new TypeReference<>() {}
            );

            return opt.orElseGet(WorkspaceSessionDto::new);
        });
    }

    public File save(WorkspaceSessionDto dto) throws IOException {
        File filePath = getWorkspaceSessionFile();

        writeFile(filePath, dto);
        cache.evict(filePath);

        return filePath;
    }
}
