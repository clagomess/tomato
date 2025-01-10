package com.github.clagomess.tomato.ui.main.request.right.statusbadge;

import com.github.clagomess.tomato.io.http.MediaType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharsetBadgeTest {

    @Test
    public void getCharset_whenNull_ReturnsMessage() {
        var ui = new CharsetBadge(null);
        assertEquals("No Content Type", ui.getCharset(null));
    }

    @Test
    public void getCharset_whenNotUTF8_ReturnsMessage() {
        var ui = new CharsetBadge(null);
        var contentType = new MediaType("text", "xml", "iso-8859-1");

        assertEquals("iso-8859-1", ui.getCharset(contentType));
    }

    @Test
    public void getCharset_whenDefault_ReturnsMessage() {
        var ui = new CharsetBadge(null);
        var contentType = new MediaType("text", "xml");

        assertEquals("UTF-8", ui.getCharset(contentType));
    }

    @Test
    public void getCharset_whenNotText_ReturnsFullContentType() {
        var ui = new CharsetBadge(null);
        assertEquals(
                "application/pdf",
                ui.getCharset(new MediaType("application", "pdf"))
        );
    }
}
