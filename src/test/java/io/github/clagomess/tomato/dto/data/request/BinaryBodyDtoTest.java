package io.github.clagomess.tomato.dto.data.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class BinaryBodyDtoTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new BinaryBodyDto())
                .isEqualTo(new BinaryBodyDto());
    }
}
