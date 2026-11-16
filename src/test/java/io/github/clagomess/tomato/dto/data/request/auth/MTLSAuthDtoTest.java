package io.github.clagomess.tomato.dto.data.request.auth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MTLSAuthDtoTest {
    @Nested
    class Pkcs12DtoTest {
        @Test
        void equalsHashCode(){
            var dtoA = new MTLSAuthDto.Pkcs12Dto("client.p12", "changeit", "client");
            var dtoB = new MTLSAuthDto.Pkcs12Dto("client.p12", "changeit", "client");

            Assertions.assertThat(dtoA)
                    .isEqualTo(dtoB)
                    .hasSameHashCodeAs(dtoB);
        }
    }
}
