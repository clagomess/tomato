package com.github.clagomess.tomato.dto.tree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = {"id"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionTreeDto {
    private String id;
    private String name;
    private File path;
    private CollectionTreeDto parent;
    private BuildStreamFI<CollectionTreeDto> children = parent -> Stream.empty();
    private BuildStreamFI<RequestHeadDto> requests = parent -> Stream.empty();

    public Stream<CollectionTreeDto> getChildren() {
        return children.build(this);
    }

    public Stream<RequestHeadDto> getRequests() {
        return requests.build(this);
    }

    public Stream<CollectionTreeDto> flattened() {
        return Stream.concat(
                Stream.of(this),
                getChildren().flatMap(CollectionTreeDto::flattened)
        );
    }

    public String getFlattenedParentString() {
        if(parent == null) return name;
        return parent.getFlattenedParentString() + " / " + name;
    }

    @FunctionalInterface
    public interface BuildStreamFI<T> {
        Stream<T> build(CollectionTreeDto parent);
    }
}
