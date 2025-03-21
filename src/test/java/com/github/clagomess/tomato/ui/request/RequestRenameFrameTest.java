package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.component.undoabletextcomponent.UndoableTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestRenameFrameTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private final RequestRepository requestRepositoryMock = Mockito.mock(RequestRepository.class);
    private final RequestRenameFrame requestRenameFrame = Mockito.mock(RequestRenameFrame.class);

    @BeforeEach
    public void setup() throws Exception {
        requestRenameFrame.requestRepository = requestRepositoryMock;
    }

    @Test
    public void save_expected_OnChange() throws IOException {
        Mockito.doCallRealMethod()
                .when(requestRenameFrame)
                .save(Mockito.any());

        Mockito.doReturn(new UndoableTextField())
                .when(requestRenameFrame)
                .getTxtName();

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

        requestRenameFrame.save(requestHead);

        assertEquals(1, count.get());
    }
}
