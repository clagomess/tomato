package com.github.clagomess.tomato.controller.workspace;

import com.github.clagomess.tomato.dto.data.DataSessionDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.io.repository.DataSessionRepository;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class WorkspaceSwitchFrameControllerTest {
    private WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final DataSessionRepository dataSessionRepository = Mockito.mock(DataSessionRepository.class);
    private final WorkspaceSwitchFrameController controller = Mockito.spy(new WorkspaceSwitchFrameController(
            dataSessionRepository
    ));

    @BeforeEach
    public void setup(){
        Mockito.reset(dataSessionRepository);
        Mockito.reset(controller);
    }

    @Test
    public void switchWorkspace_trigger() throws IOException {
        var selectedWorkspace = new WorkspaceDto();
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

        controller.switchWorkspace(selectedWorkspace);

        assertEquals(selectedWorkspace.getId(), session.getWorkspaceId());
        assertTrue(triggeredSwitch.get());
    }
}
