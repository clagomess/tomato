package io.github.clagomess.tomato.publisher.key;

import io.github.clagomess.tomato.dto.data.TomatoID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ParentCollectionKeyTest {
    @Test
    void equalsHashCode(){
        Assertions.assertThat(new ParentCollectionKey(new TomatoID("aaaaaaaa")))
                .isEqualTo(new ParentCollectionKey(new TomatoID("aaaaaaaa")));
    }
}
