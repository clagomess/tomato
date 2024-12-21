package com.github.clagomess.tomato.service.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class MediaTypeTest {
    @Test
    public void constructor_type_subtype(){
        var media = new MediaType("text", "plain");
        assertNull(media.getCharset());
    }

    @Test
    public void contructor_type_subtype_charset(){
        var media = new MediaType("text", "plain", "utf-8");
        assertEquals(StandardCharsets.UTF_8, media.getCharset());
        Assertions.assertThat(media.getParameters())
                .containsEntry("charset", "utf-8");
    }

    @Test
    public void constructor_contentType(){
        var media = new MediaType("application/javascript");
        assertEquals("application", media.getType());
        assertEquals("javascript", media.getSubtype());
        assertNull(media.getCharset());
        Assertions.assertThat(media.getParameters())
                .isEmpty();
    }

    @Test
    public void constructor_contentType_whenWithCharset(){
        var media = new MediaType("application/javascript;charset=UTF-8");
        assertEquals("application", media.getType());
        assertEquals("javascript", media.getSubtype());
        assertEquals(StandardCharsets.UTF_8, media.getCharset());
        Assertions.assertThat(media.getParameters())
                .containsEntry("charset", "utf-8");
    }

    @Test
    public void isCompatible(){
        var media = new MediaType("text", "html", "utf-8");
        assertTrue(media.isCompatible(MediaType.TEXT_HTML));
    }

    @Test
    public void toString_mediaType(){
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
    public void isString(){
        assertTrue(MediaType.TEXT_HTML.isString());
        assertFalse(new MediaType(
                "application",
                "pdf"
        ).isString());
    }
}
