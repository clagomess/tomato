package io.github.clagomess.tomato.controller.request;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RequestFrameController {
    private final List<Runnable> dispose = new ArrayList<>(3);

    public RequestDto load(
            @Nullable RequestHeadDto requestHead,
            @Nullable RequestDto request

    ) throws IOException {
        if(requestHead == null && request == null){
            throw new IllegalArgumentException("RequestHead and RequestDto are both null");
        }

        if(requestHead == null) return request;

        return new RequestRepository().load(requestHead).orElseThrow();
    }

    public void addOnStagingListener(
            TabKey tabKey,
            @NonNull RequestDto request,
            @NonNull UpdateFI title
    ){
        RequestPublisher.getInstance()
                .getOnStaging()
                .addListener(tabKey, changed -> {
                    if(changed){
                        title.setText("[*] " + request.getName());
                    }else{
                        title.setText(request.getName());
                    }
                });

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnStaging()
                .removeListener(tabKey));
    }

    public void addOnChangeListener(
            @Nullable RequestHeadDto requestHead,
            @NonNull UpdateFI title
    ){
        if(requestHead == null) return;

        var uuid = RequestPublisher.getInstance()
                .getOnChange()
                .addListener(
                    new RequestKey(requestHead),
                    event -> title.setText(event.getEvent().getName())
                );

        dispose.add(() -> RequestPublisher.getInstance()
                .getOnChange()
                .removeListener(uuid));
    }

    public void dispose(){
        dispose.forEach(Runnable::run);
    }

    @FunctionalInterface
    public interface UpdateFI {
        void setText(String text);
    }
}
