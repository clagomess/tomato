package com.github.clagomess.tomato.controller.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.WorkspaceDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.WorkspacePublisher;
import com.github.clagomess.tomato.publisher.base.EventTypeEnum;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTabbedPaneControllerTest {
    private final WorkspacePublisher workspacePublisher = WorkspacePublisher.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final RequestRepository requestRepositoryMock = Mockito.mock(RequestRepository.class);
    private final RequestTabbedPaneController controller = Mockito.spy(new RequestTabbedPaneController(
            requestRepositoryMock
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
