package io.github.clagomess.tomato.dto.data.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlParamDtoTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new UrlParamDto())
                .isEqualTo(new UrlParamDto());
    }
}
