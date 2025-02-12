package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.github.clagomess.tomato.enums.HttpMethodEnum.GET;
import static com.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RequestUITest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private TabKey tabKey;
    private RequestHeadDto requestHead;
    private final RequestUI requestUI = Mockito.spy(new RequestUI());

    @BeforeEach
    public void setup() {
        var tree = new CollectionTreeDto();
        tree.setId("c_a");

        requestHead = new RequestHeadDto();
        requestHead.setId("r_a");
        requestHead.setName("aaa");
        requestHead.setMethod(GET);
        requestHead.setParent(tree);

        tabKey = new TabKey("r_a");
    }

    @AfterEach
    public void dispose(){
        requestUI.dispose();

        assertFalse(requestPublisher.getOnChange()
                .containsListener(new RequestKey(requestHead)));

        assertFalse(requestPublisher.getOnStaging()
                .containsListener(tabKey));
    }

    @Test
    public void trigger_requestPublisher_OnChange(){
        requestUI.addOnChangeListener(requestHead);

        var requestHeadUpdate = new RequestHeadDto();
        requestHeadUpdate.setId("r_a");
        requestHeadUpdate.setName("bbb");
        requestHeadUpdate.setMethod(POST);

        requestPublisher.getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(UPDATED, requestHeadUpdate)
        );

        assertEquals(requestHeadUpdate.getName(), requestUI.getTitle());
    }

    @Test
    public void trigger_requestPublisher_OnStaging(){
        requestPublisher.getOnStaging().publish(tabKey, true);

        Assertions.assertThat(requestUI.getTitle())
                .contains("[*]");
    }
}
