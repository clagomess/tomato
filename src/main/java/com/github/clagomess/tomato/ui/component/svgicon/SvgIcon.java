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
import java.util.Objects;

@RequiredArgsConstructor
public class SvgIcon implements Icon {
    private final String resourceName;
    private final int iconWidth;
    private final int iconHeight;
    private final Color color;

    public SvgIcon(String resourceName){
        this(resourceName, 18, 18, UIManager.getColor("Objects.Grey"));
    }

    private SVGDocument document = null;
    private SVGDocument getDocument() {
        if(document != null) return document;

        URL svgUrl = Objects.requireNonNull(
                getClass().getResource(resourceName),
                "Failed to load resource: " + resourceName
        );

        SVGLoader loader = new SVGLoader();

        if(color == null) {
            document = Objects.requireNonNull(
                    loader.load(svgUrl),
                    "Failed to load svg: " + svgUrl
            );
        }else{
            document = Objects.requireNonNull(
                    loader.load(svgUrl, new ColorParserProvider(color)),
                    "Failed to load svg: " + svgUrl
            );
        }

        return document;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        getDocument().render(c, g2, new ViewBox(x, y, iconWidth, iconHeight));
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
        private final Color color;

        @Override
        public DomProcessor createPreProcessor() {
            return new CustomDomProcessor(color);
        }
    }

    private static class CustomDomProcessor implements DomProcessor {
        private final String color;

        public CustomDomProcessor(Color color) {
            this.color = "#" + String.format("%06X", 0xFFFFFF & color.getRGB());
        }

        @Override
        public void process(ParsedElement root) {
            root.attributeNode().attributes().put("fill", this.color);
        }
    }
}
