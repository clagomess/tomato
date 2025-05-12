package io.github.clagomess.tomato.publisher.base;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class NoKeyPublisher<T> extends BasePublisher<Void, T> {
    public UUID addListener(EventPublishFI<T> runnable) {
        return addListenerGeneric(runnable);
    }

    public UUID addListener(EventRequestFI<T> runnable) {
        listeners.forEach(item -> removeListener(item.getUuid()));
        return addListenerGeneric(runnable);
    }

    private UUID addListenerGeneric(EventFI<T> runnable) {
        Listener<Void, T> listener = new Listener<>(null, runnable);

        if(log.isDebugEnabled()){
            log.debug(
                    "AddListener: {}\n-> {}",
                    listener.getAbbrevUuid(),
                    runnable
            );
        }

        listeners.add(listener);

        return listener.getUuid();
    }

    public void publish(T event){
        if(log.isDebugEnabled()) log.debug("Publishing: {}", event);

        listeners.parallelStream().forEach(listener -> {
            if(listener.getRunnable() instanceof EventPublishFI<T> runnable){
                if(log.isDebugEnabled()){
                    log.debug(
                            "-> trigger: {}\n-> {}",
                            listener.getAbbrevUuid(),
                            listener.getRunnable()
                    );
                }

                runnable.run(event);
            }
        });
    }

    public T request(){
        if(log.isDebugEnabled()) log.debug("Requesting");

        Optional<Listener<Void, T>> listenerOpt = listeners.stream()
                .filter(listener -> listener.getRunnable() instanceof EventRequestFI<T>)
                .findFirst();

        if(listenerOpt.isEmpty()){
            throw new IllegalStateException("No listener found");
        }

        Listener<Void, T> listener = listenerOpt.get();

        if(listener.getRunnable() instanceof EventRequestFI<T> runnable){
            if(log.isDebugEnabled()){
                log.debug(
                        "-> request: {}\n-> {}",
                        listener.getAbbrevUuid(),
                        listener.getRunnable()
                );
            }

            return runnable.get();
        }

        throw new IllegalStateException("listener not executed");
    }
}
