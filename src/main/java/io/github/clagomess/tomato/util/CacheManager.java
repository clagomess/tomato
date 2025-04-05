package io.github.clagomess.tomato.util;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CacheManager<K, V> {
    private static final List<CacheManager<?, ?>> toDebug = new LinkedList<>();
    private final Map<K, AtomicInteger> hitCount = new ConcurrentHashMap<>();
    private final Map<K, AtomicInteger> missCount = new ConcurrentHashMap<>();

    private K defaultKey = null;
    private final Map<K, V> cache = new ConcurrentHashMap<>();

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
            if(log.isDebugEnabled()) log.debug("Cache HIT: {}", key);
            hitCount.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();
            return cache.get(key);
        }

        if(log.isDebugEnabled()) log.debug("Cache Miss: {}", key);

        V result = doTaskIfAbsent.run();
        cache.put(key, result);

        missCount.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();

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

        if(log.isDebugEnabled()) log.debug("Cache Evict: {}", key);
        hitCount.remove(key);
        missCount.remove(key);
        cache.remove(key);
    }

    public void evict() {
        evict(defaultKey);
    }

    public void evictAll() {
        cache.clear();
        hitCount.clear();
        missCount.clear();
    }

    public static String debug(){
        StringBuilder sb = new StringBuilder();
        String basePackage = "io.github.clagomess.tomato.";

        toDebug.forEach(manager -> {
            sb.append("# ");
            sb.append(manager.toString().replace(basePackage, ""));
            sb.append("\n");

            manager.cache.forEach((key, value) -> {
                sb.append("  - HIT/MISS: ");
                sb.append(manager.hitCount.getOrDefault(key, new AtomicInteger()).get());
                sb.append("/");
                sb.append(manager.missCount.getOrDefault(key, new AtomicInteger()).get());
                sb.append(" - ");
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
        toDebug.forEach(CacheManager::evictAll);
    }

    @FunctionalInterface
    public interface TaskFI <V, E extends Throwable> {
        V run() throws E;
    }
}
