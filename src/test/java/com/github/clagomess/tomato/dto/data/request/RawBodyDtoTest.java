package com.github.clagomess.tomato.dto.data.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class RawBodyDtoTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new RawBodyDto())
                .isEqualTo(new RawBodyDto());
    }
}
