package io.github.clagomess.tomato.controller.main.request;

import io.github.clagomess.tomato.dto.ResponseDto;
import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.http.HttpService;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.*;

class RequestSplitPaneControllerTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final RequestRepository requestRepositoryMock = Mockito.mock(RequestRepository.class);
    private final RequestSplitPaneController controller = Mockito.spy(new RequestSplitPaneController(
            requestRepositoryMock
    ));

    @BeforeEach
    void setup(){
        Mockito.reset(requestRepositoryMock);
        Mockito.reset(controller);
    }

    @Test
    void sendRequest_OnComplete() throws InterruptedException {
        var complete = new AtomicBoolean(false);
        var error = new AtomicBoolean(false);

        try(var ignored = Mockito.mockConstruction(
                HttpService.class,
                (mock, context) -> {
                    Mockito.doReturn(new ResponseDto("foo"))
                            .when(mock)
                            .perform();
                })
        ) {
            var thread = controller.sendRequest(
                    new RequestDto(),
                    response -> {
                        complete.set(true);
                        assertNotNull(response);
                    },
                    e -> error.set(true)
            );

            thread.join();

            assertTrue(complete.get());
            assertFalse(error.get());
        }
    }

    @Test
    void sendRequest_OnError() throws InterruptedException {
        var complete = new AtomicBoolean(false);
        var error = new AtomicBoolean(false);

        try(var ignored = Mockito.mockConstruction(
                HttpService.class,
                (mock, context) -> {
                    Mockito.doThrow(new TomatoException(""))
                            .when(mock)
                            .perform();
                })
        ) {
            var thread = controller.sendRequest(
                    new RequestDto(),
                    response -> {
                        complete.set(true);
                        assertNull(response);
                    },
                    e -> {
                        error.set(true);
                        assertNotNull(e);
                    }
            );

            thread.join();

            assertTrue(complete.get());
            assertTrue(error.get());
        }
    }

    @Test
    void save_trigger() throws IOException {
        var triggered = new AtomicBoolean(false);
        var request = new RequestDto();
        var requestHead = new RequestHeadDto();
        requestHead.setId(request.getId());
        requestHead.setParent(new CollectionTreeDto());

        requestPublisher.getOnChange().addListener(new RequestKey(requestHead), event -> {
            triggered.set(true);
            assertEquals(UPDATED, event.getType());
        });

        controller.save(requestHead, request);

        assertTrue(triggered.get());
    }
}
