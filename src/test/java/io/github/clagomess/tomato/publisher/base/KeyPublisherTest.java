package io.github.clagomess.tomato.publisher.base;

import io.github.clagomess.tomato.io.repository.DirectoryCreateException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

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

        assertThrowsExactly(
                RuntimeException.class,
                () -> doException.publish(2, "opa")
        );
    }

    @Test
    public void removeListener(){
        KeyPublisher<Integer, String> doException = new KeyPublisher<>();

        doException.addListener(1, event -> {
            throw new RuntimeException("OK");
        });

        assertThrowsExactly(
                RuntimeException.class,
                () -> doException.publish(1, "opa")
        );

        doException.removeListener(1);
        doException.publish(1, "opa");
    }

    @Test
    public void publish_concurrent(){
        KeyPublisher<String, String> publisher = new KeyPublisher<>();
        List<UUID> uuids = new LinkedList<>();
        var key = RandomStringUtils.secure().nextAlphanumeric(8);

        IntStream.range(0, 100).forEach(i -> {
            var uuid = publisher.addListener(key, event -> {});
            uuids.add(uuid);
        });

        uuids.parallelStream()
                .forEach(i -> {
                    var dummy = RandomStringUtils.secure().nextAlphanumeric(8);
                    publisher.publish(key, dummy);
                });
    }

    @Test
    public void changeKey(){
        KeyPublisher<Integer, String> doException = new KeyPublisher<>();
        doException.addListener(1, event -> {
            throw new RuntimeException();
        });
        doException.addListener(2, event -> {
            throw new IllegalArgumentException();
        });

        doException.changeKey(1, 3);
        doException.changeKey(2, 4);

        doException.publish(1, "opa");
        doException.publish(2, "opa");

        assertThrowsExactly(
                RuntimeException.class,
                () -> doException.publish(3, "opa")
        );

        assertThrowsExactly(
                IllegalArgumentException.class,
                () -> doException.publish(4, "opa")
        );
    }
}
