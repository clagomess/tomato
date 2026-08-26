package io.github.clagomess.tomato.controller.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import io.github.clagomess.tomato.ui.component.NameInterface;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestRenameFrameControllerTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private final RequestRepository requestRepositoryMock = Mockito.mock(RequestRepository.class);
    private final NameInterface requestRenameFrame = Mockito.mock(NameInterface.class);
    private final RequestRenameFrameController controller = new RequestRenameFrameController(
            requestRepositoryMock,
            requestRenameFrame
    );

    @Test
    void save_expected_OnChange() throws IOException {
        Mockito.doReturn(Optional.of(new RequestDto()))
                .when(requestRepositoryMock)
                .load(Mockito.any());

        var requestHead = new RequestHeadDto();
        requestHead.setParent(new CollectionTreeDto());

        var count = new AtomicInteger(0);
        requestPublisher.getOnChange().addListener(new RequestKey(requestHead), event -> {
            count.incrementAndGet();
            assertEquals(UPDATED, event.getType());
        });

        controller.save(requestHead);

        assertEquals(1, count.get());
    }
}
