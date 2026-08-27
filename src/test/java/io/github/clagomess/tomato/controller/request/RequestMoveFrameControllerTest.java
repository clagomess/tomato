package io.github.clagomess.tomato.controller.request;

import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;
import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;
import static org.junit.jupiter.api.Assertions.*;

class RequestMoveFrameControllerTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private final RequestRepository requestRepositoryMock = Mockito.mock(RequestRepository.class);
    private final RequestMoveFrameController controller = new RequestMoveFrameController(
            requestRepositoryMock
    );

    @Nested
    class moveRequest {
        @Test
        void whenDestinationNull_throws() {
            assertThrowsExactly(
                    TomatoException.class,
                    () -> controller.moveRequest(new RequestHeadDto(), null)
            );
        }

        @Test
        void whenExpected_moveAndPublishDeletedAndInserted() throws IOException {
            var source = new RequestHeadDto();
            source.setId(new TomatoID());
            source.setName("my-request");
            source.setPath(new File("/source/request-my-request.json"));
            source.setParent(new CollectionTreeDto());
            source.getParent().setId(new TomatoID());

            var destination = new CollectionTreeDto();
            destination.setId(new TomatoID());
            destination.setPath(new File("/destination"));

            var deleted = new AtomicInteger(0);
            requestPublisher.getOnChange().addListener(new RequestKey(source), event -> {
                deleted.incrementAndGet();
                assertEquals(DELETED, event.getType());
                assertSame(source, event.getEvent());
            });

            var inserted = new AtomicInteger(0);
            var targetKey = new RequestKey(destination.getId(), source.getId());
            requestPublisher.getOnChange().addListener(targetKey, event -> {
                inserted.incrementAndGet();
                assertEquals(INSERTED, event.getType());
                assertEquals(source.getId(), event.getEvent().getId());
                assertSame(destination, event.getEvent().getParent());
                assertEquals(
                        new File("/destination", "request-my-request.json"),
                        event.getEvent().getPath()
                );
            });

            controller.moveRequest(source, destination);

            Mockito.verify(requestRepositoryMock).move(source, destination);
            assertEquals(1, deleted.get());
            assertEquals(1, inserted.get());
        }
    }
}
