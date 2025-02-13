package com.github.clagomess.tomato.controller.main.request;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.clagomess.tomato.enums.HttpMethodEnum.GET;
import static com.github.clagomess.tomato.enums.HttpMethodEnum.POST;
import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static org.junit.jupiter.api.Assertions.*;

public class TabTitleControllerTest {
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private TabKey tabKey;
    private RequestHeadDto requestHead;
    private final TabTitleController controller = Mockito.spy(TabTitleController.class);

    @BeforeEach
    public void setup() {
        var request = new RequestDto();

        requestHead = new RequestHeadDto();
        requestHead.setId(request.getId());
        requestHead.setName(request.getName());
        requestHead.setMethod(GET);
        requestHead.setParent(new CollectionTreeDto());

        tabKey = new TabKey(request.getId());

        // mocks
        Mockito.reset(controller);
    }

    @AfterEach
    public void dispose(){
        controller.dispose();

        assertFalse(requestPublisher.getOnChange()
                .containsListener(new RequestKey(requestHead)));

        assertFalse(requestPublisher.getOnStaging()
                .containsListener(tabKey));
    }

    @Test
    public void addOnChangeListener_trigger(){
        AtomicReference<HttpMethodEnum> methodResult = new AtomicReference<>();
        AtomicReference<String> nameResult = new AtomicReference<>();

        controller.addOnChangeListener(requestHead, (method, name) -> {
            methodResult.set(method);
            nameResult.set(name);
        });

        var requestHeadUpdate = new RequestHeadDto();
        requestHeadUpdate.setId(requestHead.getId());
        requestHeadUpdate.setName("bbb");
        requestHeadUpdate.setMethod(POST);

        requestPublisher.getOnChange().publish(
                new RequestKey(requestHead),
                new PublisherEvent<>(UPDATED, requestHeadUpdate)
        );

        assertEquals(
                requestHeadUpdate.getMethod(),
                methodResult.get()
        );
        assertEquals(
                requestHeadUpdate.getName(),
                nameResult.get()
        );
    }

    @Test
    public void addOnStagingListener_trigger(){
        AtomicBoolean result = new AtomicBoolean();
        controller.addOnStagingListener(tabKey, result::set);

        requestPublisher.getOnStaging().publish(tabKey, true);

        assertTrue(result.get());
    }
}
