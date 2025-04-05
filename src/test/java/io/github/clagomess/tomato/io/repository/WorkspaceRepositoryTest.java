package io.github.clagomess.tomato.io.repository;

import io.github.clagomess.tomato.dto.data.DataSessionDto;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkspaceRepositoryTest extends RepositoryStubs {
    private final ConfigurationRepository configurationRepositoryMock = Mockito.mock(ConfigurationRepository.class);
    private final DataSessionRepository dataSessionRepositoryMock = Mockito.spy(
            new DataSessionRepository(configurationRepositoryMock)
    );
    private final WorkspaceRepository workspaceRepository = Mockito.spy(new WorkspaceRepository(
            configurationRepositoryMock,
            dataSessionRepositoryMock
    ));

    @BeforeEach
    public void setup() throws IOException {
        // mock ConfigurationRepository
        Mockito.reset(configurationRepositoryMock);
        Mockito.when(configurationRepositoryMock.getDataDir())
                .thenReturn(mockDataDir);
    }

    @Test
    public void getWorkspaceDirectory_whenNotExists_create() throws IOException {
        var result = workspaceRepository.getWorkspaceDirectory(
                RandomStringUtils.secure().nextAlphanumeric(8)
        );

        Assertions.assertThat(result).isDirectory();
    }

    @Test
    public void save_whenNotExists_CreateAndWriteDto() throws IOException {
        var dto = new WorkspaceDto();
        dto.setName("OPA");

        workspaceRepository.save(dto);

        var result = new File(
                mockDataDir,
                String.format(
                        "workspace-%s/workspace-%s.json",
                        dto.getId(),
                        dto.getId()
                )
        );

        assertTrue(result.isFile());
    }

    @Test
    public void list_whenDataDirIsEmpty_ReturnDefault() throws IOException {
        Assertions.assertThat(workspaceRepository.list())
                .isNotEmpty()
                .allMatch(item -> item.getPath().isDirectory())
        ;
    }

    @Test
    public void list_assertSorted() throws IOException {
        Mockito.doReturn(List.of(
                new File("BBB"),
                new File("AAA")
        )).when(workspaceRepository)
                .listDirectories();

        Mockito.doAnswer(answer -> {
            var dto = new WorkspaceDto();
            dto.setName(answer.getArgument(0).toString());
            return Optional.of(dto);
        }).when(workspaceRepository)
                .load(Mockito.any());

        var result = workspaceRepository.list();
        Assertions.assertThat(result).isSorted();
    }

    @Test
    public void getDataSessionWorkspace_whenNotDefined_thenGetFirst() throws IOException {
        var workspace = new WorkspaceDto();

        Mockito.when(workspaceRepository.list())
                .thenReturn(List.of(workspace));

        var result = workspaceRepository.getDataSessionWorkspace();
        assertEquals(result.getId(), workspace.getId());
    }

    @Test
    public void getDataSessionWorkspace_whenDefined_thenResult() throws IOException {
        var workspace = new WorkspaceDto();
        var dataSession = new DataSessionDto();
        dataSession.setWorkspaceId(workspace.getId());

        Mockito.when(dataSessionRepositoryMock.load())
                .thenReturn(dataSession);
        Mockito.when(workspaceRepository.list())
                .thenReturn(List.of(new WorkspaceDto(), workspace));

        var result = workspaceRepository.getDataSessionWorkspace();
        assertEquals(result.getId(), workspace.getId());
    }

    @Test
    public void delete() throws IOException {
        var workspace = new WorkspaceDto();
        workspaceRepository.save(workspace);

        workspaceRepository.delete(workspace);
        var workspaceDir = new File(mockDataDir, "workspace-" + workspace.getId());
        Assertions.assertThat(workspaceDir)
                .doesNotExist();
    }
}
