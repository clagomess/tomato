package io.github.clagomess.tomato.dto.tree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.clagomess.tomato.dto.data.TomatoID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentHeadDto {
    private TomatoID id;
    private String name;
    private boolean production;
}
