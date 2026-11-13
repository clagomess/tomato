package io.github.clagomess.tomato.dto.data.request;

import io.github.clagomess.tomato.util.ObjectMapperUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.clagomess.tomato.dto.data.request.AuthDto.Type.*;

class AuthDtoTest {
    private final ObjectMapperUtil mapper = ObjectMapperUtil.getInstance();

    @Test
    void equalsHashCode(){
        Assertions.assertThat(new AuthDto())
                .isEqualTo(new AuthDto());
    }

    @Nested
    class getMtls {
        @Test
        void whenTypeIsNotMtls_returnNull(){
            var dto = new AuthDto();
            dto.setType(BASIC);

            Assertions.assertThat(dto.getMtls()).isNull();
        }

        @Test
        void whenTypeIsMtls_returnInitializedPkcs12(){
            var dto = new AuthDto();
            dto.setType(MTLS);

            Assertions.assertThat(dto.getMtls()).isNotNull();
            Assertions.assertThat(dto.getMtls().getPkcs12()).isNotNull();
        }
    }

    @Test
    void serialization_mtlsRoundTrip() throws Exception {
        var dto = new AuthDto();
        dto.setType(MTLS);
        dto.getMtls().getPkcs12().setFile("client.pfx");
        dto.getMtls().getPkcs12().setPassword("changeit");

        var json = mapper.writeValueAsString(dto);

        Assertions.assertThat(json)
                .contains("\"mtls\"")
                .contains("\"pkcs12\"")
                .doesNotContain("\"basic\"");
        Assertions.assertThat(mapper.readValue(json, AuthDto.class))
                .isEqualTo(dto);
    }
}
