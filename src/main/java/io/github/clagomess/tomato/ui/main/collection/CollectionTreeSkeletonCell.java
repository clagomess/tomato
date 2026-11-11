package io.github.clagomess.tomato.ui.main.collection;

import io.github.clagomess.tomato.ui.component.ColorConstant;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CollectionTreeSkeletonCell extends JComponent {
    private static final int ICON_SIZE = 16;
    private static final int GAP = 4;
    private static final int TEXT_WIDTH = 100;
    private static final int TEXT_HEIGHT = 10;

    private static final int SHIMMER_DURATION_MS = 1200;
    private static final int SHIMMER_WIDTH = 40;
    private static final int FRAME_DELAY_MS = 40;
    private static final Color SHIMMER_COLOR = new Color(130, 132, 134);

    private final Timer repaintTimer = new Timer(FRAME_DELAY_MS, e -> repaintPending());
    private @Nullable JTree pendingTree;
    private @Nullable Rectangle pendingBounds;

    public CollectionTreeSkeletonCell() {
        setPreferredSize(new Dimension(ICON_SIZE + GAP + TEXT_WIDTH, ICON_SIZE));
        repaintTimer.setRepeats(false);
    }

    public void scheduleRepaint(JTree tree, int row) {
        Rectangle bounds = tree.getRowBounds(row);
        if(bounds == null) return;

        pendingBounds = pendingTree == tree && pendingBounds != null ?
                pendingBounds.union(bounds) :
                bounds;
        pendingTree = tree;

        if(!repaintTimer.isRunning()) repaintTimer.start();
    }

    private void repaintPending() {
        if(pendingTree != null && pendingBounds != null) {
            pendingTree.repaint(pendingBounds);
        }

        pendingTree = null;
        pendingBounds = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(createShimmerPaint());

            int iconY = (getHeight() - ICON_SIZE) / 2;
            g2.fillRoundRect(0, iconY, ICON_SIZE, ICON_SIZE, 4, 4);

            int textY = (getHeight() - TEXT_HEIGHT) / 2;
            g2.fillRoundRect(ICON_SIZE + GAP, textY, TEXT_WIDTH, TEXT_HEIGHT, TEXT_HEIGHT, TEXT_HEIGHT);
        } finally {
            g2.dispose();
        }
    }

    private Paint createShimmerPaint() {
        float phase = (System.currentTimeMillis() % SHIMMER_DURATION_MS) / (float) SHIMMER_DURATION_MS;
        float center = -SHIMMER_WIDTH + phase * (getWidth() + SHIMMER_WIDTH * 2);

        return new LinearGradientPaint(
                center - SHIMMER_WIDTH, 0,
                center + SHIMMER_WIDTH, 0,
                new float[]{0f, 0.5f, 1f},
                new Color[]{ColorConstant.GRAY, SHIMMER_COLOR, ColorConstant.GRAY}
        );
    }
}
