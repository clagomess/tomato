package com.github.clagomess.tomato.controller.main.request;

import com.github.clagomess.tomato.dto.RequestTabSnapshotDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.SystemPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.base.EventTypeEnum;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTabbedPaneControllerTest {
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final RequestRepository requestRepositoryMock = Mockito.mock(RequestRepository.class);
    private final WorkspaceSessionRepository workspaceSessionRepository = Mockito.mock(WorkspaceSessionRepository.class);
    private final RequestTabbedPaneController controller = Mockito.spy(new RequestTabbedPaneController(
            requestRepositoryMock,
            workspaceSessionRepository
    ));

    @BeforeEach
    public void setup(){
        Mockito.reset(requestRepositoryMock);
        Mockito.reset(controller);
    }

    @Test
    public void addWorkspaceOnSwitchListener_trigger(){
        var triggered = new AtomicBoolean(false);

        controller.addWorkspaceOnSwitchListener(() -> triggered.set(true));

        workspacePublisher.getOnSwitch().publish(new WorkspaceDto());

        assertTrue(triggered.get());
    }

    @Test
    public void addRequestOnLoadListener_trigger(){
        var triggered = new AtomicBoolean(false);

        Mockito.doAnswer(answer -> {
                    triggered.set(true);
                    return null;
                })
                .when(controller)
                .loadRequest(Mockito.any(), Mockito.any());

        controller.addRequestOnLoadListener((eHead, eRequest) -> {});

        requestPublisher.getOnLoad().publish(new PublisherEvent<>(
                EventTypeEnum.NEW,
                new RequestHeadDto()
        ));

        assertTrue(triggered.get());
    }

    @Test
    public void addSaveRequestsSnapshotListener_trigger(){
        var triggerCount = new AtomicInteger();

        Mockito.doAnswer(answer -> {
            triggerCount.incrementAndGet();
            return null;
        })
                .when(controller)
                .saveRequestsSnapshot(Mockito.any());

        controller.addSaveRequestsSnapshotListener(() -> null);

        workspacePublisher.getOnBeforeSwitch()
                .publish("foo");

        SystemPublisher.getInstance()
                .getOnClosing()
                .publish("foo");

        assertEquals(2, triggerCount.get());
    }

    @Test
    public void saveRequestsSnapshot() throws IOException {
        var session = new WorkspaceSessionDto();

        Mockito.doReturn(session)
                .when(workspaceSessionRepository)
                .load();

        controller.saveRequestsSnapshot(List.of(
                new RequestTabSnapshotDto(true, null, new RequestDto())
        ));

        Assertions.assertThat(session.getRequests())
                .isNotEmpty();
    }

    @Test
    public void saveRequestsSnapshot_whenEmpty() throws IOException {
        var session = new WorkspaceSessionDto();
        session.setRequests(List.of(
                new WorkspaceSessionDto.Request()
        ));

        Mockito.doReturn(session)
                .when(workspaceSessionRepository)
                .load();

        controller.saveRequestsSnapshot(List.of());

        Assertions.assertThat(session.getRequests())
                .isEmpty();
    }

    @Test
    public void loadRequest_whenNew(){
        controller.loadRequest(
                new PublisherEvent<>(EventTypeEnum.NEW, null),
                (head, request) -> {
                    assertNull(head);
                    assertNotNull(request);
                }
        );
    }

    @Test
    public void loadRequest_whenExisting() throws IOException {
        var request = new RequestDto();
        Mockito.doReturn(Optional.of(request))
                .when(requestRepositoryMock)
                .load(Mockito.any());

        controller.loadRequest(
                new PublisherEvent<>(EventTypeEnum.LOAD, new RequestHeadDto()),
                (eHead, eRequest) -> {
                    assertNotNull(eHead);
                    assertEquals(request, eRequest);
                }
        );
    }
}
