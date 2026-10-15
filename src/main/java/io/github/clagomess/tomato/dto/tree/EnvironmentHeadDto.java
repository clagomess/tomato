package io.github.clagomess.tomato.dto.tree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.clagomess.tomato.dto.data.TomatoID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentHeadDto implements Comparable<EnvironmentHeadDto>{
    private TomatoID id;
    private String name;
    private boolean production;

     @Override
     public int compareTo(EnvironmentHeadDto o) {
         return StringUtils.compareIgnoreCase(this.getName(), o.getName(), true);
     }
}
