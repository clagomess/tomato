package com.github.clagomess.tomato.util;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CacheManager<K, V> {
    private static final List<CacheManager<?, ?>> toDebug = new LinkedList<>();
    private K defaultKey = null;
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    public CacheManager() {
        toDebug.add(this);
    }

    public CacheManager(K defaultKey) {
        this();
        this.defaultKey = defaultKey;
    }

    public <E extends Throwable> V get(
            K key,
            TaskFI<V, E> doTaskIfAbsent
    ) throws E {
        if(cache.containsKey(key)) {
            log.debug("Cache HIT: {}", key);
            return cache.get(key);
        }

        log.debug("Cache Miss: {}", key);

        V result = doTaskIfAbsent.run();
        cache.put(key, result);

        return result;
    }

    public <E extends Throwable> V get(
            TaskFI<V, E> doTaskIfAbsent
    ) throws E {
        return get(defaultKey, doTaskIfAbsent);
    }

    public synchronized <E extends Throwable> V getSynchronized(
            TaskFI<V, E> doTaskIfAbsent
    ) throws E {
        return get(defaultKey, doTaskIfAbsent);
    }

    public void evict(K key) {
        if(!cache.containsKey(key)) return;

        log.debug("Cache Evict: {}", key);
        cache.remove(key);
    }

    public void evict() {
        evict(defaultKey);
    }

    public void evictAll() {
        cache.clear();
    }

    public static String debug(){
        StringBuilder sb = new StringBuilder();
        String basePackage = "com.github.clagomess.tomato.";

        toDebug.stream()
                .filter(item -> !item.cache.isEmpty())
                .forEach(manager -> {
                    sb.append("# ");
                    sb.append(manager.toString().replace(basePackage, ""));
                    sb.append("\n");

                    manager.cache.forEach((key, value) -> {
                        sb.append("  - ");
                        sb.append(key);
                        sb.append(" - ");
                        sb.append(value);
                        sb.append("\n");
                    });

                    sb.append("\n");
                });

        return sb.toString();
    }

    public static void reset(){
        toDebug.forEach(item -> item.cache.clear());
    }

    @FunctionalInterface
    public interface TaskFI <V, E extends Throwable> {
        V run() throws E;
    }
}
