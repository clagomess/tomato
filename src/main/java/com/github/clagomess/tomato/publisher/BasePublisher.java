package com.github.clagomess.tomato.publisher;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class BasePublisher<T> {
    private final Map<UUID, OnChangeFI<T>> listeners = new HashMap<>();

    public UUID addListener(OnChangeFI<T> listener) {
        var uuid = UUID.randomUUID();
        log.debug("addListener: {}", uuid);
        listeners.put(uuid, listener);
        return uuid;
    }

    public void removeListener(UUID uuid) {
        log.debug("removeListener: {}", uuid);
        listeners.remove(uuid);
    }

    public void publish(T event){
        log.debug("Publishing: {}", event);
        listeners.forEach((key, value) -> {
            log.debug("-> trigger: {}", key);
            value.change(event);
        });
    }

    @FunctionalInterface
    public interface OnChangeFI<T> {
        void change(T event);
    }
}
