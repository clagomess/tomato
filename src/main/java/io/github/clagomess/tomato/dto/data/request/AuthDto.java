package io.github.clagomess.tomato.dto.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.clagomess.tomato.dto.data.request.auth.BasicAuthDto;
import io.github.clagomess.tomato.dto.data.request.auth.MTLSAuthDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthDto {
    private Type type = Type.NO_AUTH;
    private BasicAuthDto basic;
    private MTLSAuthDto mtls;

    public Type getType() {
        if(type == null) type = Type.NO_AUTH;
        return type;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BasicAuthDto getBasic() {
        if(type != Type.BASIC) return null;
        if(basic == null) basic = new BasicAuthDto();
        return basic;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public MTLSAuthDto getMtls() {
        if(type != Type.MTLS) return null;
        if(mtls == null) mtls = new MTLSAuthDto();
        return mtls;
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        NO_AUTH("No Auth"),
        BASIC("Basic"),
        MTLS("mTLS");

        private final String description;

        @Override
        public String toString() {
            return description;
        }
    }
}
