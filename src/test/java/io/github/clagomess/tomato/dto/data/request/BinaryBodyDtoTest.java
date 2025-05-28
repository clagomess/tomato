package io.github.clagomess.tomato.dto.data.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BinaryBodyDtoTest {
    @Test
    void equalsHashCode(){
        Assertions.assertThat(new BinaryBodyDto())
                .isEqualTo(new BinaryBodyDto());
    }
}
