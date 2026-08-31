package io.github.clagomess.tomato.io.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static io.github.clagomess.tomato.io.http.MediaType.CHARSET_PARAM;
import static org.junit.jupiter.api.Assertions.*;

class MediaTypeTest {
    @Nested
    class Constructor {
        @Test
        void type_subtype(){
            var media = new MediaType("text", "plain");
            assertNull(media.getCharset());
        }

        @Test
        void type_subtype_charset(){
            var media = new MediaType("text", "plain", "utf-8");
            assertEquals(StandardCharsets.UTF_8, media.getCharset());
            Assertions.assertThat(media.getParameters())
                    .containsEntry(CHARSET_PARAM, "utf-8");
        }

        @Test
        void contentType(){
            var media = new MediaType("application/javascript");
            assertEquals("application", media.getType());
            assertEquals("javascript", media.getSubtype());
            assertNull(media.getCharset());
            Assertions.assertThat(media.getParameters())
                    .isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "application/javascript;charset=UTF-8",
                "application/javascript;charset=utf-8",
                "application/javascript; charset=\"UTF-8\"",
        })
        void contentType_whenWithCharset(String contentType){
            var media = new MediaType(contentType);
            assertEquals("application", media.getType());
            assertEquals("javascript", media.getSubtype());
            assertEquals(StandardCharsets.UTF_8, media.getCharset());
            Assertions.assertThat(media.getParameters())
                    .containsEntry(CHARSET_PARAM, "utf-8");
        }

        @Test
        void contentType_whenWrongCharset(){
            var media = new MediaType("application/javascript;charset=UTF-42");
            assertNull(media.getCharset());
            Assertions.assertThat(media.getParameters())
                    .containsEntry(CHARSET_PARAM, "utf-42");
        }
    }

    @Test
    void isCompatible(){
        var media = new MediaType("text", "html", "utf-8");
        assertTrue(media.isCompatible(MediaType.TEXT_HTML));
    }

    @ParameterizedTest
    @CsvSource({
            "text/plain;charset=UTF-8,UTF-8",
            "text/plain;charset=ISO-8859-1,ISO-8859-1",
            "text/plain,UTF-8",
    })
    void getCharsetOrDefault(
            String input,
            Charset expectedCharset
    ){
        var media = new MediaType(input);
        Charset result = media.getCharsetOrDefault();

        assertEquals(expectedCharset, result);
    }

    @Test
    void toString_mediaType(){
        assertEquals(
                "application/javascript;charset=utf-8",
                new MediaType("application", "javascript", "utf-8").toString()
        );
        assertEquals(
                "text/html",
                MediaType.TEXT_HTML.toString()
        );
    }

    @Test
    void isString(){
        assertTrue(MediaType.TEXT_HTML.isString());
        assertFalse(new MediaType(
                "application",
                "pdf"
        ).isString());
    }
}
