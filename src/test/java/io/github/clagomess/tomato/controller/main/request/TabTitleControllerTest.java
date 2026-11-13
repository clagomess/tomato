package io.github.clagomess.tomato.controller.main.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import io.github.clagomess.tomato.ui.main.request.TabTitleInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.github.clagomess.tomato.enums.HttpMethodEnum.GET;
import static io.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class TabTitleControllerTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final TabTitleInterface ui = Mockito.mock(TabTitleInterface.class);

    private TabKey tabKey;
    private RequestDto request;
    private RequestHeadDto requestHead;
    private TabTitleController controller;

    @BeforeEach
    void setup() {
        request = new RequestDto();

        requestHead = new RequestHeadDto();
        requestHead.setId(request.getId());
        requestHead.setName(request.getName());
        requestHead.setMethod(GET);
        requestHead.setParent(new CollectionTreeDto());

        tabKey = new TabKey(request.getId());

        Mockito.reset(ui);
    }

    @AfterEach
    void dispose(){
        controller.dispose();

        assertThat(requestPublisher.getOnChange()
                .containsListener(new RequestKey(requestHead)))
                .isFalse();

        assertThat(requestPublisher.getOnStaging()
                .containsListener(tabKey))
                .isFalse();
    }

    private RequestHeadDto publishUpdate(){
        var requestHeadUpdate = new RequestHeadDto();
        requestHeadUpdate.setId(requestHead.getId());
        requestHeadUpdate.setName("bbb");
        requestHeadUpdate.setMethod(POST);

        requestPublisher.getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(UPDATED, requestHeadUpdate)
        );

        return requestHeadUpdate;
    }

    @Nested
    class addOnChangeListener {
        @Test
        void whenTrigger_refreshUi(){
            controller = new TabTitleController(ui, requestHead);
            controller.addOnChangeListener(request);

            var requestHeadUpdate = publishUpdate();

            verify(ui).refresh(requestHeadUpdate);
            assertThat(controller.getRequestHeadAR().get())
                    .isSameAs(requestHead);
        }

        @Test
        void whenRequestHeadIsNull_setRequestHeadFromEvent(){
            controller = new TabTitleController(ui, null);
            controller.addOnChangeListener(request);

            var requestHeadUpdate = publishUpdate();

            verify(ui).refresh(requestHeadUpdate);
            assertThat(controller.getRequestHeadAR().get())
                    .isSameAs(requestHeadUpdate);
        }
    }

    @Test
    void addOnStagingListener_trigger(){
        controller = new TabTitleController(ui, requestHead);
        controller.addOnStagingListener(tabKey);

        requestPublisher.getOnStaging().publish(tabKey, true);

        verify(ui).setIcon(true);
    }
}
