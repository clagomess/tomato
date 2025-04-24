package io.github.clagomess.tomato.controller.workspace.list;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkspaceListFrameControllerTest {
    private final WorkspaceRepository workspaceRepositoryMock = Mockito.mock(WorkspaceRepository.class);
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    private final WorkspaceListFrameController controller = Mockito.spy(new WorkspaceListFrameController(
            workspaceRepositoryMock,
            workspacePublisher
    ));

    @BeforeEach
    public void setup(){
        Mockito.reset(workspaceRepositoryMock);
        Mockito.reset(controller);
        workspacePublisher.getOnChangeAny().getListeners().clear();
    }

    @AfterEach
    public void dispose(){
        controller.dispose();

        Assertions.assertThat(workspacePublisher.getOnChangeAny().getListeners())
                .isEmpty();
    }

    @Test
    public void addOnChangeListener_trigger() {
        AtomicInteger result = new AtomicInteger();

        controller.addOnChangeListener(e -> result.incrementAndGet());

        workspacePublisher.getOnChangeAny()
                .publish(new PublisherEvent<>(UPDATED, new WorkspaceDto()));

        assertEquals(1, result.get());
    }

    @Test
    public void refresh() throws IOException {
        AtomicInteger result = new AtomicInteger();
        controller.refresh(e -> result.incrementAndGet());
        assertEquals(1, result.get());
    }
}
