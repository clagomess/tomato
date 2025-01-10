package com.github.clagomess.tomato.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class CacheManagerUtilTest {
    @Test
    public void get(){
        var cache = new CacheManager<String, String>();

        cache.get("foo", () -> "bar");
        var result = cache.get("foo", () -> "xyz");

        assertEquals("bar", result);
    }

    @Test
    public void get_Concurrent(){
        var cache = new CacheManager<String, Integer>();

        IntStream.range(0, 10)
                .parallel()
                .forEach(i -> cache.get("foo", () -> i));
    }

    @Test
    public void get_KeyLess(){
        var cache = new CacheManager<String, String>("foo");

        cache.get(() -> "bar");
        var result = cache.get(() -> "xyz");

        assertEquals("bar", result);
    }

    @Test
    public void evict(){
        var cache = new CacheManager<String, String>();

        cache.get("foo", () -> "bar");
        cache.evict("foo");

        var result = cache.get("foo", () -> "xyz");

        assertEquals("xyz", result);
    }

    @Test
    public void evict_KeyLess(){
        var cache = new CacheManager<String, String>("foo");

        cache.get(() -> "bar");
        cache.evict();

        var result = cache.get(() -> "xyz");

        assertEquals("xyz", result);
    }
}
