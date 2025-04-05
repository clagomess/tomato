package io.github.clagomess.tomato.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanEnvironmentDto {
    private String id;
    private String name;
    private List<Value> values;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Value {
        private String key;
        private String value;
        private Boolean enabled;
    }
}
