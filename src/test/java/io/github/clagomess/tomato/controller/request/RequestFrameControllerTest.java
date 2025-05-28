package io.github.clagomess.tomato.controller.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.clagomess.tomato.enums.HttpMethodEnum.GET;
import static io.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.*;

class RequestFrameControllerTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private TabKey tabKey;
    private RequestHeadDto requestHead;
    private final RequestFrameController controller = Mockito.spy(RequestFrameController.class);

    @BeforeEach
    void setup() throws Exception {
        requestHead = new RequestHeadDto();
        requestHead.setId(RandomStringUtils.secure().nextAlphanumeric(8));
        requestHead.setName("aaa");
        requestHead.setMethod(GET);
        requestHead.setParent(new CollectionTreeDto());
        requestHead.getParent().setId(RandomStringUtils.secure().nextAlphanumeric(8));

        tabKey = new TabKey(requestHead.getId());

        // mocks
        Mockito.reset(controller);
    }

    @AfterEach
    void dispose(){
        controller.dispose();

        assertFalse(requestPublisher.getOnChange()
                .containsListener(new RequestKey(requestHead)));

        assertFalse(requestPublisher.getOnStaging()
                .containsListener(tabKey));
    }

    @Test
    void load_whenAllNull(){
        assertThrowsExactly(
                IllegalArgumentException.class,
                () -> controller.load(null, null)
        );
    }

    @Test
    void load_whenNewRequest() throws IOException {
        var request = new RequestDto();
        var result = controller.load(null, request);

        assertSame(request, result, "Same Class");
    }

    @Test
    void load_load() throws IOException {
        var requestHead = new RequestHeadDto();

        try(var ignored = Mockito.mockConstruction(
                RequestRepository.class,
                (mock, context) -> {
                    Mockito.doReturn(Optional.of(new RequestDto()))
                            .when(mock)
                            .load(requestHead);
                })
        ) {
            var result = controller.load(requestHead, null);
            assertNotNull(result);
        }
    }

    @Test
    void addOnStagingListener_trigger(){
        AtomicReference<String> result = new AtomicReference<>();

        controller.addOnStagingListener(tabKey, new RequestDto(), result::set);

        requestPublisher.getOnStaging().publish(tabKey, true);

        Assertions.assertThat(result.get())
                .contains("[*]");
    }

    @Test
    void addOnChangeListener_trigger(){
        AtomicReference<String> result = new AtomicReference<>();

        controller.addOnChangeListener(requestHead, result::set);

        var requestHeadUpdate = new RequestHeadDto();
        requestHeadUpdate.setId(requestHead.getId());
        requestHeadUpdate.setName("bbb");
        requestHeadUpdate.setMethod(POST);

        requestPublisher.getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(UPDATED, requestHeadUpdate)
        );

        assertEquals(
                requestHeadUpdate.getName(),
                result.get()
        );
    }
}
