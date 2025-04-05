package io.github.clagomess.tomato.publisher.key;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequestKeyTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new RequestKey("a", "a"))
                .isEqualTo(new RequestKey("a", "a"));
    }
}
