package com.github.clagomess.tomato.publisher;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Slf4j
abstract class BasePublisher<K, T> {
    private static final List<BasePublisher<?, ?>> toDebug = new LinkedList<>();
    protected final List<Listener<K, T>> listeners = new LinkedList<>();

    public BasePublisher() {
        toDebug.add(this);
    }

    public void removeListener(UUID uuid) {
        listeners.removeIf(listener -> {
            if(listener.uuid.equals(uuid)){
                log.debug("RemoveListener: {}", uuid);
                return true;
            }else{
                return false;
            }
        });
    }

    @FunctionalInterface
    public interface OnChangeFI<T> {
        void change(T event);
    }

    @Getter
    protected static class Listener<K, T> {
        private final UUID uuid = UUID.randomUUID();
        private final K key;
        private final OnChangeFI<T> runnable;

        public Listener(K key, OnChangeFI<T> runnable) {
            this.key = key;
            this.runnable = runnable;
        }
    }

    public static String debug(){
        StringBuilder sb = new StringBuilder();
        String basePackage = "com.github.clagomess.tomato.";

        toDebug.stream()
                .filter(item -> !item.listeners.isEmpty())
                .forEach(publisher -> {
                    sb.append("# ");
                    sb.append(publisher.toString().replace(basePackage, ""));
                    sb.append("\n");

                    publisher.listeners.forEach(listener -> {
                       sb.append("  - ");
                       sb.append(listener.getUuid());
                       sb.append(" - ");
                       sb.append(listener.getKey());
                       sb.append(" - ");
                       sb.append(listener.getRunnable().toString()
                                       .replace(basePackage, ""));
                       sb.append("\n");
                    });

                    sb.append("\n");
                });

        return sb.toString();
    }
}
