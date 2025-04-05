package io.github.clagomess.tomato.controller.workspace;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkspaceRenameFrameControllerTest {
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
    private final WorkspaceRenameFrameController controller = new WorkspaceRenameFrameController(
            workspaceRepositoryMock,
            workspacePublisher
    );

    @Test
    public void save_assert_OnChange() throws IOException {
        var workspace = new WorkspaceDto();
        var triggered = new AtomicBoolean(false);

        workspacePublisher.getOnChange().addListener(
                workspace.getId(),
                e -> {
                    triggered.set(true);
                    assertEquals(UPDATED, e.getType());
                }
        );

        controller.save(workspace);

        assertTrue(triggered.get());
    }
}
