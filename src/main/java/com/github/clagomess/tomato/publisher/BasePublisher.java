package com.github.clagomess.tomato.publisher;

import java.util.ArrayList;
import java.util.List;

public class BasePublisher<A> {
    private final List<OnChangeFI<A>> listners = new ArrayList<>();

    public void addListener(OnChangeFI<A> listener) {
        listners.add(listener);
    }

    public void publish(A event){
        listners.forEach(item -> item.change(event));
    }

    @FunctionalInterface
    public interface OnChangeFI<A> {
        void change(A event);
    }
}
