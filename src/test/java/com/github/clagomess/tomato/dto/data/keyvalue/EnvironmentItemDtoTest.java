package com.github.clagomess.tomato.dto.data.keyvalue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnvironmentItemDtoTest {
    @Test
    public void equalsHashCode(){
        Assertions.assertThat(new EnvironmentItemDto())
                .isEqualTo(new EnvironmentItemDto());
    }

    @Test
    public void sort(){
        List<EnvironmentItemDto> list = new ArrayList<>(2);
        list.add(new EnvironmentItemDto("bbb", "value"));
        list.add(new EnvironmentItemDto("aaa", "value"));

        Collections.sort(list);

        assertEquals("aaa", list.get(0).getKey());
    }
}
