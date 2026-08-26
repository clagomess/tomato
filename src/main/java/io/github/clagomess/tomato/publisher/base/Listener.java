package io.github.clagomess.tomato.publisher.base;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
class Listener<K, T> {
    private final UUID uuid = UUID.randomUUID();
    private K key;
    private final T listener;

    public Listener(K key, T listener) {
        this.key = key;
        this.listener = listener;
    }

    public String getAbbrevUuid(){
        return uuid.toString().substring(0, 8);
    }
}
