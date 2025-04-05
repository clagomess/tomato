package io.github.clagomess.tomato.publisher.base;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class NoKeyPublisher<T> extends BasePublisher<Void, T> {
    public UUID addListener(OnChangeFI<T> runnable) {
        Listener<Void, T> listener = new Listener<>(null, runnable);

        if(log.isDebugEnabled()){
            log.debug(
                    "AddListener: {}\n-> {}",
                    listener.getAbbrevUuid(),
                    runnable
            );
        }

        listeners.add(listener);

        return listener.getUuid();
    }

    public void publish(T event){
        if(log.isDebugEnabled()) log.debug("Publishing: {}", event);

        listeners.parallelStream().forEach(listener -> {
            if(log.isDebugEnabled()) {
                log.debug(
                        "-> trigger: {}\n-> {}",
                        listener.getAbbrevUuid(),
                        listener.getRunnable()
                );
            }

            listener.getRunnable().change(event);
        });
    }
}
