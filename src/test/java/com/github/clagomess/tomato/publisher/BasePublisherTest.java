package com.github.clagomess.tomato.publisher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasePublisherTest {
    @Test
    public void addListener(){
        BasePublisher<String> doException = new BasePublisher<>();
        doException.addListener(event -> {
            throw new RuntimeException("OK");
        });

        assertThrows(RuntimeException.class, () -> doException.publish("opa"));
    }

    @Test
    public void removeListener(){
        BasePublisher<String> doException = new BasePublisher<>();

        var uuid = doException.addListener(event -> {
            throw new RuntimeException("OK");
        });
        assertThrows(RuntimeException.class, () -> doException.publish("opa"));

        doException.removeListener(uuid);
        doException.publish("opa");
    }
}
