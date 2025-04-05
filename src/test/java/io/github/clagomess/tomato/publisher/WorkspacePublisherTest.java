package io.github.clagomess.tomato.publisher;

import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkspacePublisherTest {
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();

    @Test
    public void onChange_hitParent(){
        var workspace = new WorkspaceDto();
        var count = new AtomicInteger(0);

        workspacePublisher.getOnChange().addListener(
                workspace.getId(),
                event -> count.getAndIncrement()
        );

        workspacePublisher.getOnChangeAny().addListener(
                event -> count.getAndIncrement()
        );

        workspacePublisher.getOnChange().publish(
                workspace.getId(),
                new PublisherEvent<>(UPDATED, workspace)
        );

        assertEquals(2, count.get());
    }
}
