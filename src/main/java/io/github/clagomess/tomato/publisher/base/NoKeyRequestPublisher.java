package io.github.clagomess.tomato.publisher.base;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
public class NoKeyRequestPublisher<T, Q> extends BasePublisher<Void, Function<Q, T>> {
    public UUID addListener(Function<Q, T> runnable) {
        var listener = new Listener<Void, Function<Q, T>>(null, runnable);

        listeners.forEach(item -> removeListener(item.getUuid()));

        return addListener(listener);
    }

    public T request(){
        return request(null);
    }

    public T request(Q query){
        if(log.isDebugEnabled()) log.debug("Requesting: {}", query);

        Optional<Listener<Void, Function<Q, T>>> listenerOpt = listeners.stream().findFirst();

        if(listenerOpt.isEmpty()){
            throw new IllegalStateException("No listener found");
        }

        Listener<Void, Function<Q, T>> listener = listenerOpt.get();

        if(log.isDebugEnabled()){
            log.debug(
                    "-> request: {}\n-> {}",
                    listener.getAbbrevUuid(),
                    listener.getListener()
            );
        }

        return listener.getListener().apply(query);
    }
}
