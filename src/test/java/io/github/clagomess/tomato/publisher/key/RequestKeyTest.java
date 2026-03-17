package io.github.clagomess.tomato.publisher.key;

import io.github.clagomess.tomato.dto.data.TomatoID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestKeyTest {
    @Test
    void equalsHashCode(){
        Assertions.assertThat(new RequestKey(new TomatoID("aaaaaaaa"), new TomatoID("aaaaaaaa")))
                .isEqualTo(new RequestKey(new TomatoID("aaaaaaaa"), new TomatoID("aaaaaaaa")));
    }
}
