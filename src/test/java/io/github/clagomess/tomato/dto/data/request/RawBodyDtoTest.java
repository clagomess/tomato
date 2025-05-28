package io.github.clagomess.tomato.dto.data.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RawBodyDtoTest {
    @Test
    void equalsHashCode(){
        Assertions.assertThat(new RawBodyDto())
                .isEqualTo(new RawBodyDto());
    }
}
