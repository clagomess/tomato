package io.github.clagomess.tomato.publisher.base;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;

@Slf4j
public class KeyPublisher<K, T> extends BasePublisher<K, T> {
    public UUID addListener(K key, EventPublishFI<T> runnable) {
        return addListenerGeneric(key, runnable);
    }

    private UUID addListenerGeneric(K key, EventFI<T> runnable) {
        Listener<K, T> listener = new Listener<>(key, runnable);

        if(log.isDebugEnabled()){
            log.debug(
                    "AddListener: {} - {}\n-> {}",
                    listener.getAbbrevUuid(),
                    key,
                    runnable
            );
        }

        listeners.add(listener);

        return listener.getUuid();
    }

    public void removeListener(K key) {
        if(log.isDebugEnabled()) log.debug("RemoveListener: {}", key);
        listeners.removeIf(listener -> listener.getKey().equals(key));
    }

    public boolean containsListener(K key) {
        return listeners.stream()
                .anyMatch(listener -> listener.getKey().equals(key));
    }

    public void changeKey(K oldKey, K newKey) {
        listeners.stream()
                .filter(item -> item.getKey().equals(oldKey))
                .forEach(item -> {
                    if(log.isDebugEnabled()){
                        log.debug("Changed Key: {} -> {}", oldKey, newKey);
                    }

                    item.setKey(newKey);
                });
    }

    public void publish(K key, T event){
        if(log.isDebugEnabled()) log.debug("Publishing: {} - {}", key, event);

        listeners.parallelStream()
                .filter(item -> Objects.equals(item.getKey(), key))
                .forEach(listener -> {
                    if (log.isDebugEnabled()) {
                        log.debug(
                                "-> trigger: {} - {}\n-> {}",
                                listener.getAbbrevUuid(),
                                listener.getKey(),
                                listener.getRunnable()
                        );
                    }

                    if(listener.getRunnable() instanceof EventPublishFI<T> runnable){
                        runnable.run(event);
                    }
                });
    }
}
