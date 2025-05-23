package io.github.clagomess.tomato.controller.main.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.enums.HttpMethodEnum;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TabTitleController {
    private final List<Runnable> dispose = new ArrayList<>(2);

    public void addOnChangeListener(
            @NotNull RequestDto request,
            @Nullable RequestHeadDto requestHead,
            @NotNull UpdateTitleFI title
    ){
        var uuid = RequestPublisher.getInstance()
                .getOnChange()
                .addListener(
                        new RequestKey(requestHead, request),
                        event -> title.update(
                            event.getEvent().getMethod(),
                            event.getEvent().getName()
                        )
                );

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnChange()
                .removeListener(uuid));
    }

    public void addOnStagingListener(
            @NotNull TabKey tabKey,
            @NotNull UpdateIconChangeFI icon
    ){
        RequestPublisher.getInstance()
                .getOnStaging()
                .addListener(tabKey, icon::update);

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnStaging()
                .removeListener(tabKey));
    }

    public void dispose(){
        dispose.forEach(Runnable::run);
    }

    @FunctionalInterface
    public interface UpdateTitleFI {
        void update(
                HttpMethodEnum method,
                String name
        );
    }

    @FunctionalInterface
    public interface UpdateIconChangeFI {
        void update(boolean hasChanged);
    }
}
