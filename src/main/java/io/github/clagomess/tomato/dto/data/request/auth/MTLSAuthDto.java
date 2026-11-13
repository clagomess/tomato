package io.github.clagomess.tomato.dto.data.request.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class MTLSAuthDto {
    private Type type = Type.PKCS12;
    private Pkcs12Dto pkcs12;

    public Type getType() {
        if(type == null) type = Type.PKCS12;
        return type;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Pkcs12Dto getPkcs12() {
        if(type != Type.PKCS12) return null;
        if(pkcs12 == null) pkcs12 = new Pkcs12Dto();
        return pkcs12;
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        PKCS12("PKCS#12 (*.pfx, *.p12)");

        private final String description;

        @Override
        public String toString() {
            return description;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pkcs12Dto {
        private String file;
        private String password;
        private String alias;
    }
}
