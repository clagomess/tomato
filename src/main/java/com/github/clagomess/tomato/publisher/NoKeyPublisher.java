package com.github.clagomess.tomato.publisher;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
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

        noConcurrentList.forEach(listener -> {
            SwingUtilities.invokeLater(() -> {
                log.debug("-> trigger: {}", listener.getUuid());
                listener.getRunnable().change(event);
            });
        });

        noConcurrentList.clear();
    }
}
