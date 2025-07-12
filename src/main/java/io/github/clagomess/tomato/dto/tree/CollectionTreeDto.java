package io.github.clagomess.tomato.dto.tree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.stream.Stream;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionTreeDto implements Comparable<CollectionTreeDto> {
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
        return getFlattenedParentStringBuilder().toString();
    }

    protected StringBuilder getFlattenedParentStringBuilder() {
        StringBuilder sb = new StringBuilder();

        if(parent == null){
            sb.append("ROOT - ");
            sb.append(name);
            sb.append(" /");
            return sb;
        }

        if(parent.parent != null){
            sb.append(parent.getFlattenedParentStringBuilder());
            sb.append(" / ");
        }

        sb.append(name);

        return sb;
    }

    @Override
    public int compareTo(CollectionTreeDto o) {
        return StringUtils.compareIgnoreCase(this.getName(), o.getName(), true);
    }

    @FunctionalInterface
    public interface BuildStreamFI<T> {
        Stream<T> build(CollectionTreeDto parent);
    }
}
