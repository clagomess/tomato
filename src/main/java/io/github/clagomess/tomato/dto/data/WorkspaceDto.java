package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.clagomess.tomato.dto.data.workspace.ProxyDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"path"})
public class WorkspaceDto extends MetadataDto implements Comparable<WorkspaceDto> {
    private String name;

    @JsonIgnore
    private File path;

    private List<ProxyDto> proxies;

    public List<ProxyDto> getProxies() {
        if(proxies == null) proxies = new ArrayList<>();
        return proxies;
    }

    @Override
    public int compareTo(@NonNull WorkspaceDto o) {
        return StringUtils.compareIgnoreCase(this.getName(), o.getName(), true);
    }
}
