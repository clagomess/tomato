package io.github.clagomess.tomato.publisher.base;

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

    protected UUID addListener(Listener<K, T> listener) {
        if(log.isDebugEnabled()){
            log.debug(
                    "AddListener: {} - {}\n-> {}",
                    listener.getAbbrevUuid(),
                    listener.getKey(),
                    listener.getListener()
            );
        }

        listeners.add(listener);

        return listener.getUuid();
    }

    public void removeListener(UUID uuid) {
        Optional<Listener<K, T>> opt = listeners.stream()
                .filter(Objects::nonNull)
                .filter(consumer -> Objects.equals(consumer.getUuid(), uuid))
                .findFirst();

        if (opt.isPresent()) {
            if (log.isDebugEnabled()) log.debug("RemoveListener: {}", uuid);
            listeners.remove(opt.get());
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
                       sb.append(listener.getListener().toString()
                                       .replace(basePackage, ""));
                       sb.append("\n");
                    });

                    sb.append("\n");
                });

        return sb.toString();
    }
}
