package com.github.clagomess.tomato.publisher.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PublisherEvent<T> {
    private EventTypeEnum type;
    private T event;
}
