package io.github.clagomess.tomato.dto.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.clagomess.tomato.enums.AuthTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static io.github.clagomess.tomato.enums.AuthTypeEnum.BASIC;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthDto {
    private AuthTypeEnum type = BASIC;
    private BasicAuthDto basic;

    public AuthTypeEnum getType() {
        if(type == null) type = AuthTypeEnum.BASIC;
        return type;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BasicAuthDto getBasic() {
        if(type != BASIC) return null;
        if(basic == null) basic = new BasicAuthDto();
        return basic;
    }
}
