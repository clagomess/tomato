package com.github.clagomess.tomato.publisher.base;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
public class NoKeyPublisher<T> extends BasePublisher<Void, T> {
    public UUID addListener(OnChangeFI<T> runnable) {
        Listener<Void, T> listener = new Listener<>(null, runnable);
        log.debug("AddListener: {}", listener.getUuid());

        listeners.add(listener);

        return listener.getUuid();
    }

    public void publish(T event){
        log.debug("Publishing: {}",event);

        var noConcurrentList = new ArrayList<>(listeners);

        noConcurrentList.parallelStream().forEach(listener -> {
            log.debug("-> trigger: {}", listener.getUuid());
            listener.getRunnable().change(event);
        });

        noConcurrentList.clear();
    }
}
