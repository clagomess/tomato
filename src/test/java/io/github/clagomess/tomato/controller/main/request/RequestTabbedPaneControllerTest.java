package io.github.clagomess.tomato.controller.main.request;

import io.github.clagomess.tomato.dto.RequestTabSnapshotDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.WorkspaceSessionDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.io.repository.TreeRepository;
import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.SystemPublisher;
import io.github.clagomess.tomato.publisher.WorkspacePublisher;
import io.github.clagomess.tomato.publisher.base.EventTypeEnum;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
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
    private final TreeRepository treeRepository = Mockito.mock(TreeRepository.class);
    private final RequestTabbedPaneController controller = Mockito.spy(new RequestTabbedPaneController(
            requestRepositoryMock,
            workspaceSessionRepository,
            treeRepository
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

    @Nested
    class LoadRequestFromSession {
        @Test
        public void whenNew() throws IOException {
            var wsRequest = new WorkspaceSessionDto.Request(null, new RequestDto());
            var ws = new WorkspaceSessionDto();
            ws.setRequests(List.of(wsRequest));

            Mockito.doReturn(ws)
                    .when(workspaceSessionRepository)
                    .load();

            AtomicBoolean triggered = new AtomicBoolean(false);
            controller.loadRequestFromSession((requestHead, request) -> {
                triggered.set(true);
                assertNull(requestHead);
                assertNotNull(request);
                assertEquals(request.getId(), wsRequest.getStaging().getId());
            });

            assertTrue(triggered.get());
        }

        @Test
        public void whenModified() throws IOException {
            var wsRequest = new WorkspaceSessionDto.Request(new File("foo"), new RequestDto());
            var ws = new WorkspaceSessionDto();
            ws.setRequests(List.of(wsRequest));

            Mockito.doReturn(ws)
                    .when(workspaceSessionRepository)
                    .load();


            var requestHeadMock = new RequestHeadDto();
            Mockito.doReturn(Optional.of(requestHeadMock))
                    .when(treeRepository)
                    .loadRequestHead(Mockito.any());

            AtomicBoolean triggered = new AtomicBoolean(false);
            controller.loadRequestFromSession((requestHead, request) -> {
                triggered.set(true);
                assertNotNull(requestHead);
                assertNotNull(request);
                assertEquals(requestHead.getId(), requestHeadMock.getId());
                assertEquals(request.getId(), wsRequest.getStaging().getId());
            });

            assertTrue(triggered.get());
        }

        @Test
        public void whenOpened() throws IOException {
            var wsRequest = new WorkspaceSessionDto.Request(new File("foo"), null);
            var ws = new WorkspaceSessionDto();
            ws.setRequests(List.of(wsRequest));

            Mockito.doReturn(ws)
                    .when(workspaceSessionRepository)
                    .load();

            var requestHeadMock = new RequestHeadDto();
            Mockito.doReturn(Optional.of(requestHeadMock))
                    .when(treeRepository)
                    .loadRequestHead(Mockito.any());

            var requestMock = new RequestDto();
            requestMock.setId(requestHeadMock.getId());
            Mockito.doReturn(Optional.of(requestMock))
                    .when(requestRepositoryMock)
                    .load(Mockito.any());

            AtomicBoolean triggered = new AtomicBoolean(false);
            controller.loadRequestFromSession((requestHead, request) -> {
                triggered.set(true);
                assertNotNull(requestHead);
                assertNotNull(request);
                assertEquals(requestHead.getId(), request.getId());
            });

            assertTrue(triggered.get());
        }
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
