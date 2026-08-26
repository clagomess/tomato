package io.github.clagomess.tomato.publisher.base;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class NoKeyPublisher<T> extends BasePublisher<Void, Consumer<T>> {
    public UUID addListener(Consumer<T> runnable) {
        var listener = new Listener<Void, Consumer<T>>(null, runnable);
        return addListener(listener);
    }

    public void publish(T event){
        if(log.isDebugEnabled()) log.debug("Publishing: {}", event);

        listeners.parallelStream().forEach(listener -> {
            if(log.isDebugEnabled()){
                log.debug(
                        "-> trigger: {}\n-> {}",
                        listener.getAbbrevUuid(),
                        listener.getListener()
                );
            }

            listener.getListener().accept(event);
        });
    }
}
