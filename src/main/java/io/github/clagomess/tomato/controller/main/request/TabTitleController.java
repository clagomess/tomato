package io.github.clagomess.tomato.controller.main.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import io.github.clagomess.tomato.ui.main.request.TabTitleInterface;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class TabTitleController {
    private final List<Runnable> dispose = new ArrayList<>(2);
    private final TabTitleInterface ui;

    @Getter
    private final AtomicReference<RequestHeadDto> requestHeadAR = new AtomicReference<>();

    public TabTitleController(
            TabTitleInterface ui,
            @Nullable RequestHeadDto requestHead
    ) {
        this.ui = ui;
        this.requestHeadAR.set(requestHead);
    }

    public void addOnChangeListener(
            @NonNull RequestDto request
    ){
        var key = new RequestKey(requestHeadAR.get(), request);
        var uuid = RequestPublisher.getInstance()
                .getOnChange()
                .addListener(key, event -> {
                    requestHeadAR.compareAndSet(null, event.getEvent());
                    ui.refresh(event.getEvent());
                });

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnChange()
                .removeListener(uuid));
    }

    public void addOnStagingListener(
            @NonNull TabKey tabKey
    ){
        RequestPublisher.getInstance()
                .getOnStaging()
                .addListener(tabKey, ui::setIcon);

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnStaging()
                .removeListener(tabKey));
    }

    public void dispose(){
        dispose.forEach(Runnable::run);
    }
}
