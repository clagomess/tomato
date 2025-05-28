package io.github.clagomess.tomato.publisher.key;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestKeyTest {
    @Test
    void equalsHashCode(){
        Assertions.assertThat(new RequestKey("a", "a"))
                .isEqualTo(new RequestKey("a", "a"));
    }
}
