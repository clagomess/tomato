package com.github.clagomess.tomato.publisher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class NoKeyPublisherTest {
    @Test
    public void addListenerAndPublish(){
        NoKeyPublisher<String> doException = new NoKeyPublisher<>();
        doException.addListener(event -> {
            throw new RuntimeException("OK");
        });

        assertThrowsExactly(RuntimeException.class, () -> doException.publish("opa"));
    }

    @Test
    public void removeListener(){
        NoKeyPublisher<String> doException = new NoKeyPublisher<>();

        var uuid = doException.addListener(event -> {
            throw new RuntimeException("OK");
        });
        assertThrowsExactly(RuntimeException.class, () -> doException.publish("opa"));

        doException.removeListener(uuid);
        doException.publish("opa");
    }
}
