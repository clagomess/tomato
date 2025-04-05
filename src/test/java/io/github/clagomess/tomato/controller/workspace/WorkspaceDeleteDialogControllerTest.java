package io.github.clagomess.tomato.controller.workspace;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;
import static org.junit.jupiter.api.Assertions.*;

public class WorkspaceDeleteDialogControllerTest {
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
    private final WorkspaceDeleteDialogController controller = new WorkspaceDeleteDialogController(
            workspaceRepositoryMock
    );

    @BeforeEach
    public void setup(){
        Mockito.reset(workspaceRepositoryMock);
    }

    @Test
    public void delete_doExceptionWhenCurrentWs() throws IOException {
        var workspace = new WorkspaceDto();

        Mockito.doReturn(workspace)
                .when(workspaceRepositoryMock)
                .getDataSessionWorkspace();

        assertThrows(
                RuntimeException.class,
                () -> controller.delete(workspace)
        );
    }

    @Test
    public void delete_assert_OnChange() throws IOException {
        var workspace = new WorkspaceDto();
        var triggered = new AtomicBoolean(false);

        Mockito.doReturn(new WorkspaceDto())
                .when(workspaceRepositoryMock)
                .getDataSessionWorkspace();

        workspacePublisher.getOnChange().addListener(
                workspace.getId(),
                e -> {
                    triggered.set(true);
                    assertEquals(DELETED, e.getType());
                }
        );

        controller.delete(workspace);

        assertTrue(triggered.get());
    }
}
