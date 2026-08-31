package io.github.clagomess.tomato.dto.data.workspace;

import io.github.clagomess.tomato.dto.data.TomatoID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxyDtoTest {
    @Test
    void equalsHashCode(){
        var id = new TomatoID();
        var a = new ProxyDto();
        a.setId(id);
        var b = new ProxyDto();
        b.setId(id);

        Assertions.assertThat(a).isEqualTo(b);
    }

    @Test
    void sort(){
        var a = new ProxyDto();
        a.setHost("aaa");
        var b = new ProxyDto();
        b.setHost("bbb");

        List<ProxyDto> list = new ArrayList<>(2);
        list.add(b);
        list.add(a);

        Collections.sort(list);

        assertEquals("aaa", list.get(0).getHost());
    }
}
