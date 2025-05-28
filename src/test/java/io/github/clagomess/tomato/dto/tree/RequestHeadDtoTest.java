package io.github.clagomess.tomato.dto.tree;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestHeadDtoTest {
    @Test
    void sort(){
        var a = new RequestHeadDto();
        a.setName("aaa");

        var b = new RequestHeadDto();
        b.setName("bbb");

        List<RequestHeadDto> list = new ArrayList<>(2);
        list.add(b);
        list.add(a);


        Collections.sort(list);

        assertEquals("aaa", list.get(0).getName());
    }
}
