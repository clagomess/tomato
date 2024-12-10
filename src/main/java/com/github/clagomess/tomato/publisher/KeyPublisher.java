package com.github.clagomess.tomato.publisher;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class KeyPublisher<K, T> extends BasePublisher<K, T> {
    public UUID addListener(K key, OnChangeFI<T> runnable) {
        Listener<K, T> listener = new Listener<>(key, runnable);
        log.debug("AddListener: {} - {}", listener.getUuid(), key);

        listeners.add(listener);

        return listener.getUuid();
    }

    public void removeListener(K key) {
        log.debug("RemoveListener: {}", key);
        listeners.removeIf(listener -> listener.getKey().equals(key));
    }

    public void publish(K key, T event){
        log.debug("Publishing: {} - {}", key, event);

        var noConcurrentList = new ArrayList<>(listeners);

        noConcurrentList.stream()
                .filter(item -> Objects.equals(item.getKey(), key))
                .forEach(listener -> {
                    SwingUtilities.invokeLater(() -> {
                        log.debug(
                                "-> trigger: {} - {}",
                                listener.getUuid(),
                                listener.getKey()
                        );

                        listener.getRunnable().change(event);
                    });
                });

        noConcurrentList.clear();
    }
}
