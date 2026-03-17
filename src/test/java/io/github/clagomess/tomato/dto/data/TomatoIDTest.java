package io.github.clagomess.tomato.dto.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.clagomess.tomato.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@Slf4j
class TomatoIDTest {
    private final ObjectMapper mapper = ObjectMapperUtil.getInstance();
    private final TomatoID ID = new TomatoID("JNtdCPvw");
    private record Example(TomatoID id){}

    @Nested
    class constructor {
        @Test
        void whenNull_throws(){
            assertThrowsExactly(
                    NullPointerException.class,
                    () -> new TomatoID(null)
            );
        }

        @Test
        void whenIncorrect_throws(){
            assertThrowsExactly(
                    IllegalArgumentException.class,
                    () -> new TomatoID("xxx")
            );
        }

        @Test
        void whenCorret_ok(){
            var result = new TomatoID("JNtdCPvw");
            assertEquals(ID, result);
        }
    }

    @Test
    void jsonWrite() throws JsonProcessingException {
        var json = mapper.writeValueAsString(new Example(ID));
        assertEquals("{\"id\":\"JNtdCPvw\"}", json);
    }

    @Test
    void jsonRead() throws JsonProcessingException {
        var result = mapper.readValue(
                "{\"id\":\"JNtdCPvw\"}",
                Example.class
        );

        assertEquals(ID, result.id());
    }

    @Test
    void toStringTest(){
        assertEquals("JNtdCPvw", ID.toString());
    }

    @Test
    void equalsTest(){
        Assertions.assertThat(ID)
                .isEqualTo(new TomatoID("JNtdCPvw"));
    }

    @Test
    void sort(){
        var a = new TomatoID("aaaaaaaa");
        var b = new TomatoID("bbbbbbbb");

        List<TomatoID> list = new ArrayList<>(List.of(b, a));

        Collections.sort(list);

        assertEquals(a, list.get(0));
    }

    @Test
    void serialize() throws IOException {
        var result = new TomatoID();

        try(
                var baos = new ByteArrayOutputStream();
                var oos = new ObjectOutputStream(baos)
        ){
            oos.writeObject(result);
            log.info("{}", baos);
        }
    }
}
