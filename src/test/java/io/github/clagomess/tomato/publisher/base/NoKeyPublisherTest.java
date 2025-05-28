package io.github.clagomess.tomato.publisher.base;

import io.github.clagomess.tomato.exception.TomatoException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class NoKeyPublisherTest {
    @Test
    void addListenerAndPublish(){
        NoKeyPublisher<String> doException = new NoKeyPublisher<>();
        doException.addListener(event -> {
            throw new TomatoException("OK");
        });

        try(var msSwing = Mockito.mockStatic(SwingUtilities.class)) {
            msSwing.when(() -> SwingUtilities.invokeLater(Mockito.any()))
                    .thenAnswer(answer -> {
                        ((Runnable) answer.getArgument(0)).run();
                        return null;
                    });

            assertThrowsExactly(
                    TomatoException.class,
                    () -> doException.publish("opa")
            );
        }
    }

    @Test
    void removeListener(){
        NoKeyPublisher<String> doException = new NoKeyPublisher<>();

        var uuid = doException.addListener(event -> {
            throw new TomatoException("OK");
        });

        try(var msSwing = Mockito.mockStatic(SwingUtilities.class)) {
            msSwing.when(() -> SwingUtilities.invokeLater(Mockito.any()))
                    .thenAnswer(answer -> {
                        ((Runnable) answer.getArgument(0)).run();
                        return null;
                    });

            assertThrowsExactly(
                    TomatoException.class,
                    () -> doException.publish("opa")
            );

            doException.removeListener(uuid);
            doException.publish("opa");
        }
    }

    @Test
    void publish_concurrent(){
        NoKeyPublisher<String> publisher = new NoKeyPublisher<>();
        List<UUID> uuids = new LinkedList<>();

        IntStream.range(0, 100).forEach(i -> {
            var uuid = publisher.addListener(event -> {});
            uuids.add(uuid);
        });

        uuids.parallelStream()
                .forEach(i -> {
                    var dummy = RandomStringUtils.secure().nextAlphanumeric(8);
                    publisher.publish(dummy);
                });
    }

    @Test
    void request(){
        NoKeyPublisher<String> publisher = new NoKeyPublisher<>();
        publisher.addListener(() -> "opa1");
        publisher.addListener(() -> "opa2");

        assertEquals("opa2", publisher.request());
    }
}
