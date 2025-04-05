package io.github.clagomess.tomato.ui.component.favicon;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.renderer.awt.NullPlatformSupport;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class FaviconImage {
    private final SVGDocument document;

    public FaviconImage() {
        URL svgUrl = Objects.requireNonNull(
                getClass().getResource("favicon.svg"),
                "Failed to load resource: favicon.svg"
        );
        SVGLoader loader = new SVGLoader();

        document = Objects.requireNonNull(
                loader.load(svgUrl),
                "Failed to load svg: favicon.svg"
        );
    }

    private AffineTransform getAffineTransform(){
        return GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getDefaultTransform();
    }

    private int clipScale(int value, double scale) {
        if (scale == 1.0) return value;
        return (int) Math.round(value * scale);
    }

    public BufferedImage build(int width, int height) {
        AffineTransform affineTransform = getAffineTransform();

        int nWidth = clipScale(width, affineTransform.getScaleX());
        int nHeight = clipScale(height, affineTransform.getScaleY());

        var image = new BufferedImage(
                nWidth,
                nHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        document.renderWithPlatform(
                NullPlatformSupport.INSTANCE,
                g2,
                new ViewBox(0, 0, nWidth, nHeight)
        );

        g2.dispose();

        return image;
    }

    /**
     * @see Window#getIconImages()
     */
    public static List<BufferedImage> getFrameIconImage(){
        var faviconImage = new FaviconImage();

        return List.of(
                new Dimension(16, 16),
                new Dimension(32, 32),
                new Dimension(64, 64),
                new Dimension(128, 128)
        ).parallelStream()
                .map(item -> faviconImage.build(item.width, item.height))
                .toList();
    }
}
