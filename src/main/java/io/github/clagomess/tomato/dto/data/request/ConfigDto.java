package io.github.clagomess.tomato.dto.data.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.clagomess.tomato.dto.data.TomatoID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigDto {
    private TomatoID proxyId;

    @JsonIgnore
    public boolean isNotEmpty(){
        return proxyId != null;
    }
}
