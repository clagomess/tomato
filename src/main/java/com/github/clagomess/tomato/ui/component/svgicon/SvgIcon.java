package com.github.clagomess.tomato.ui.component.svgicon;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.parser.DefaultParserProvider;
import com.github.weisj.jsvg.parser.DomProcessor;
import com.github.weisj.jsvg.parser.ParsedElement;
import com.github.weisj.jsvg.parser.SVGLoader;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SvgIcon implements Icon {
    private final String resourceName;
    private final int iconWidth;
    private final int iconHeight;
    private final String color;

    public SvgIcon(String resourceName, int iconWidth, int iconHeight, Color color) {
        this.resourceName = resourceName;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.color = "#" + String.format("%06X", 0xFFFFFF & color.getRGB());
    }

    public SvgIcon(String resourceName){
        this.resourceName = resourceName;
        this.iconWidth = 18;
        this.iconHeight = 18;
        this.color = "#BBBBBB";
    }

    private final Map<Boolean, SVGDocument> documentMap = new HashMap<>(1);
    private SVGDocument getDocument(Component component) {
        if(documentMap.containsKey(component.isEnabled())){
            return documentMap.get(component.isEnabled());
        }

        URL svgUrl = Objects.requireNonNull(getClass().getResource(resourceName));

        if(color == null && component.isEnabled()) {
            SVGDocument document = Objects.requireNonNull(
                    new SVGLoader().load(svgUrl)
            );
            documentMap.put(component.isEnabled(), document);
            return document;
        }

        SVGDocument document = Objects.requireNonNull(
                new SVGLoader().load(svgUrl, new ColorParserProvider(
                        component.isEnabled() ? color : "#616365"
                ))
        );

        documentMap.put(component.isEnabled(), document);

        return document;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        getDocument(c).render(c, g2, new ViewBox(x, y, iconWidth, iconHeight));
    }

    @Override
    public int getIconWidth() {
        return iconWidth;
    }

    @Override
    public int getIconHeight() {
        return iconHeight;
    }

    @RequiredArgsConstructor
    private static class ColorParserProvider extends DefaultParserProvider {
        private final String color;

        @Override
        public DomProcessor createPreProcessor() {
            return new CustomDomProcessor(color);
        }
    }

    @RequiredArgsConstructor
    private static class CustomDomProcessor implements DomProcessor {
        private final String color;

        @Override
        public void process(ParsedElement root) {
            root.attributeNode().attributes().put("fill", this.color);
        }
    }
}
