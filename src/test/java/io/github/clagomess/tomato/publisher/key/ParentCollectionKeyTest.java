package io.github.clagomess.tomato.publisher.key;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParentCollectionKeyTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new ParentCollectionKey("a"))
                .isEqualTo(new ParentCollectionKey("a"));
    }
}
