package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestRenameUITest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private final RequestRepository requestRepositoryMock = Mockito.mock(RequestRepository.class);
    private final RequestRenameUI requestRenameUI = Mockito.spy(new RequestRenameUI(
            requestRepositoryMock
    ));

    @Test
    public void save_expected_OnChange() throws IOException {
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

        requestRenameUI.save(requestHead);

        assertEquals(2, count.get());
    }
}
