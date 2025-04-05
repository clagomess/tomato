package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"path"})
public class WorkspaceDto extends MetadataDto implements Comparable<WorkspaceDto> {
    private String name;

    @JsonIgnore
    private File path;

    @Override
    public int compareTo(@NotNull WorkspaceDto o) {
        return StringUtils.compareIgnoreCase(this.getName(), o.getName());
    }
}
