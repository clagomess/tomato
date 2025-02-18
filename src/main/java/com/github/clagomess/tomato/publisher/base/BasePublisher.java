package com.github.clagomess.tomato.publisher.base;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Getter
abstract class BasePublisher<K, T> {
    private static final List<BasePublisher<?, ?>> toDebug = new LinkedList<>();
    protected final Queue<Listener<K, T>> listeners = new ConcurrentLinkedQueue<>();

    public BasePublisher() {
        toDebug.add(this);
    }

    public void removeListener(UUID uuid) {
        Optional<Listener<K, T>> opt = listeners.stream()
                .filter(Objects::nonNull)
                .filter(listener -> Objects.equals(listener.uuid, uuid))
                .findFirst();

        if (opt.isPresent()) {
            if (log.isDebugEnabled()) log.debug("RemoveListener: {}", uuid);
            listeners.remove(opt.get());
        }
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

        public String getAbbrevUuid(){
            return uuid.toString().substring(0, 8);
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
                       sb.append(listener.getAbbrevUuid());
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
