package com.github.clagomess.tomato.publisher.base;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class NoKeyPublisherTest {
    @Test
    public void addListenerAndPublish(){
        NoKeyPublisher<String> doException = new NoKeyPublisher<>();
        doException.addListener(event -> {
            throw new RuntimeException("OK");
        });

        try(var msSwing = Mockito.mockStatic(SwingUtilities.class)) {
            msSwing.when(() -> SwingUtilities.invokeLater(Mockito.any()))
                    .thenAnswer(answer -> {
                        ((Runnable) answer.getArgument(0)).run();
                        return null;
                    });

            assertThrowsExactly(
                    RuntimeException.class,
                    () -> doException.publish("opa")
            );
        }
    }

    @Test
    public void removeListener(){
        NoKeyPublisher<String> doException = new NoKeyPublisher<>();

        var uuid = doException.addListener(event -> {
            throw new RuntimeException("OK");
        });

        try(var msSwing = Mockito.mockStatic(SwingUtilities.class)) {
            msSwing.when(() -> SwingUtilities.invokeLater(Mockito.any()))
                    .thenAnswer(answer -> {
                        ((Runnable) answer.getArgument(0)).run();
                        return null;
                    });

            assertThrowsExactly(
                    RuntimeException.class,
                    () -> doException.publish("opa")
            );

            doException.removeListener(uuid);
            doException.publish("opa");
        }
    }
}
