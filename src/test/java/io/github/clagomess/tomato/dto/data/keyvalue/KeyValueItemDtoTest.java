package io.github.clagomess.tomato.dto.data.keyvalue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyValueItemDtoTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new KeyValueItemDto())
                .isEqualTo(new KeyValueItemDto());
    }

    @Test
    public void sort(){
        List<KeyValueItemDto> list = new ArrayList<>(2);
        list.add(new KeyValueItemDto("bbb", "value"));
        list.add(new KeyValueItemDto("aaa", "value"));

        Collections.sort(list);

        assertEquals("aaa", list.get(0).getKey());
    }
}
