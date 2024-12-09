package com.github.clagomess.tomato.publisher;

import com.github.clagomess.tomato.exception.DirectoryCreateException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class KeyPublisherTest {
    @Test
    public void addListenerAndPublish(){
        KeyPublisher<Integer, String> doException = new KeyPublisher<>();
        doException.addListener(1, event -> {
            throw new DirectoryCreateException(new File(""));
        });
        doException.addListener(2, event -> {
            throw new RuntimeException("OK");
        });

        assertThrowsExactly(RuntimeException.class, () -> doException.publish(2, "opa"));
    }

    @Test
    public void removeListener(){
        KeyPublisher<Integer, String> doException = new KeyPublisher<>();

        doException.addListener(1, event -> {
            throw new RuntimeException("OK");
        });
        assertThrowsExactly(RuntimeException.class, () -> doException.publish(1, "opa"));

        doException.removeListener(1);
        doException.publish(1, "opa");
    }
}
