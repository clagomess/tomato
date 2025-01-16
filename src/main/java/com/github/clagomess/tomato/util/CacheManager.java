package com.github.clagomess.tomato.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor
public class CacheManager<K, V> {
    private K defaultKey = null;
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    public CacheManager(K defaultKey) {
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

    @FunctionalInterface
    public interface TaskFI <V, E extends Throwable> {
        V run() throws E;
    }
}
