package com.github.clagomess.tomato.controller.main.request;

import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TabTitleController {
    private final List<Runnable> dispose = new ArrayList<>(2);

    public void addOnChangeListener(
            @Nullable RequestHeadDto requestHead,
            @NotNull UpdateTitleFI title
    ){
        if(requestHead == null) return;

        var key = new RequestKey(requestHead);

        RequestPublisher.getInstance()
                .getOnChange()
                .addListener(key, event -> title.update(
                        event.getEvent().getMethod(),
                        event.getEvent().getName()
                ));

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnChange()
                .removeListener(key));
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
