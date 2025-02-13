package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static com.github.clagomess.tomato.enums.HttpMethodEnum.GET;
import static com.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RequestUITest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private TabKey tabKey;
    private RequestHeadDto requestHead;
    private final RequestUI requestUI = Mockito.mock(RequestUI.class);

    @BeforeEach
    public void setup() throws Exception {
        requestHead = new RequestHeadDto();
        requestHead.setId(RandomStringUtils.secure().nextAlphanumeric(8));
        requestHead.setName("aaa");
        requestHead.setMethod(GET);
        requestHead.setParent(new CollectionTreeDto());
        requestHead.getParent().setId(RandomStringUtils.secure().nextAlphanumeric(8));

        tabKey = new TabKey(requestHead.getId());

        // mocks
        Mockito.reset(requestUI);
        requestUI.dispose = new ArrayList<>();

        Mockito.doCallRealMethod()
                .when(requestUI)
                .setTitle(Mockito.any());
        Mockito.doCallRealMethod()
                .when(requestUI)
                .getTitle();
        Mockito.doCallRealMethod()
                .when(requestUI)
                .dispose();
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
        Mockito.doCallRealMethod().when(requestUI)
                .addOnChangeListener(Mockito.any());

        requestUI.addOnChangeListener(requestHead);

        var requestHeadUpdate = new RequestHeadDto();
        requestHeadUpdate.setId(requestHead.getId());
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
        Mockito.doCallRealMethod().when(requestUI)
                .addOnStagingListener(Mockito.any(), Mockito.any());

        requestUI.addOnStagingListener(tabKey, requestHead);
        requestPublisher.getOnStaging().publish(tabKey, true);

        Assertions.assertThat(requestUI.getTitle())
                .contains("[*]");
    }
}
