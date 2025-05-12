package io.github.clagomess.tomato.publisher.base;

import lombok.Getter;
import lombok.Setter;
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

    public interface EventFI<T> {}

    @FunctionalInterface
    public interface EventPublishFI<T> extends EventFI<T> {
        void run(T event);
    }

    @FunctionalInterface
    public interface EventRequestFI<T> extends EventFI<T> {
        T get();
    }

    @Getter
    @Setter
    protected static class Listener<K, T> {
        private final UUID uuid = UUID.randomUUID();
        private K key;
        private final EventFI<T> runnable;

        public Listener(K key, EventFI<T> runnable) {
            this.key = key;
            this.runnable = runnable;
        }

        public String getAbbrevUuid(){
            return uuid.toString().substring(0, 8);
        }
    }

    public static String debug(){
        StringBuilder sb = new StringBuilder();
        String basePackage = "io.github.clagomess.tomato.";

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
