package com.github.clagomess.tomato.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EditorFactoryTest {
    @Test
    public void createSyntaxStyleFromContentType(){
        Assertions.assertEquals("text/html", EditorFactory.createSyntaxStyleFromContentType("text/html; charset=ISO-8859-1"));
        Assertions.assertEquals("text/html", EditorFactory.createSyntaxStyleFromContentType("text/html"));
        Assertions.assertEquals("text/html", EditorFactory.createSyntaxStyleFromContentType(" text/html "));
        Assertions.assertEquals("text/plain", EditorFactory.createSyntaxStyleFromContentType(" "));
        Assertions.assertEquals("text/plain", EditorFactory.createSyntaxStyleFromContentType(null));
    }
}
