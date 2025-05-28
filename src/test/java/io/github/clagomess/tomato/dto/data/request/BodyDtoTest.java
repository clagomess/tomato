package io.github.clagomess.tomato.dto.data.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BodyDtoTest {
    @Test
    void equalsHashCode(){
        Assertions.assertThat(new BodyDto())
                .isEqualTo(new BodyDto());
    }
}
