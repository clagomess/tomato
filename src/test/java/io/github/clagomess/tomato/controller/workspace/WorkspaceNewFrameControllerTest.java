package io.github.clagomess.tomato.controller.workspace;

import io.github.clagomess.tomato.dto.data.DataSessionDto;
import io.github.clagomess.tomato.io.repository.DataSessionRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceNewFrameControllerTest {
    private WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final DataSessionRepository dataSessionRepository = Mockito.mock(DataSessionRepository.class);
    private final WorkspaceRepository workspaceRepository = Mockito.mock(WorkspaceRepository.class);
    private final WorkspaceNewFrameController controller = Mockito.spy(new WorkspaceNewFrameController(
            dataSessionRepository,
            workspaceRepository
    ));

    @BeforeEach
    void setup(){
        Mockito.reset(dataSessionRepository);
        Mockito.reset(workspaceRepository);
        Mockito.reset(controller);
    }

    @Test
    void switchWorkspace_trigger() throws IOException {
        var session = new DataSessionDto();

        var triggeredBeforeSwitch = new AtomicBoolean(false);
        var triggeredSwitch = new AtomicBoolean(false);
        workspacePublisher.getOnBeforeSwitch()
                .addListener(event -> triggeredBeforeSwitch.set(true));
        workspacePublisher.getOnSwitch()
                .addListener(event -> triggeredSwitch.set(true));

        Mockito.doReturn(session)
                .when(dataSessionRepository)
                .load();

        Mockito.doAnswer(answer -> {
            assertTrue(triggeredBeforeSwitch.get());
            assertFalse(triggeredSwitch.get());
            return null;
        }).when(dataSessionRepository).save(Mockito.any());

        controller.save("fooo");

        assertNotNull(session.getWorkspaceId());
        assertTrue(triggeredSwitch.get());
    }
}
