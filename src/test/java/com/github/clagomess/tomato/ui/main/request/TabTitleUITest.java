package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.clagomess.tomato.enums.HttpMethodEnum.GET;
import static com.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TabTitleUITest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private TabKey tabKey;
    private RequestHeadDto requestHead;
    private TabTitleUI tabTitleUI;

    @BeforeEach
    public void setup() {
        var request = new RequestDto();

        requestHead = new RequestHeadDto();
        requestHead.setId(request.getId());
        requestHead.setName(request.getName());
        requestHead.setMethod(GET);
        requestHead.setParent(new CollectionTreeDto());

        tabKey = new TabKey(request.getId());

        tabTitleUI = new TabTitleUI(null, tabKey, requestHead, request);
    }

    @AfterEach
    public void dispose(){
        tabTitleUI.dispose();

        assertFalse(requestPublisher.getOnChange()
                .containsListener(new RequestKey(requestHead)));

        assertFalse(requestPublisher.getOnStaging()
                .containsListener(tabKey));
    }

    @Test
    public void trigger_requestPublisher_OnChange(){
        var requestHeadUpdate = new RequestHeadDto();
        requestHeadUpdate.setId("r_a");
        requestHeadUpdate.setName("bbb");
        requestHeadUpdate.setMethod(POST);

        requestPublisher.getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(UPDATED, requestHeadUpdate)
        );

        assertEquals(
                requestHeadUpdate.getMethod().getIcon().getClass(),
                tabTitleUI.httpMethod.getIcon().getClass()
        );
        assertEquals(requestHeadUpdate.getName(), tabTitleUI.title.getText());
    }

    @Test
    public void trigger_requestPublisher_OnStaging(){
        requestPublisher.getOnStaging().publish(tabKey, true);

        assertEquals(
                tabTitleUI.iconHasChanged.getClass(),
                tabTitleUI.changeIcon.getIcon().getClass()
        );
    }
}
