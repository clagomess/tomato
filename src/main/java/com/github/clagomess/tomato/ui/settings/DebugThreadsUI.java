package com.github.clagomess.tomato.ui.settings;

import com.github.clagomess.tomato.ui.MainUI;
import com.github.clagomess.tomato.ui.component.RawTextArea;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.Comparator;

public class DebugThreadsUI extends JFrame {
    private final RawTextArea console = new RawTextArea();
    private final Timer checkTimer;

    public DebugThreadsUI(MainUI mainUI) {
        setTitle("Debug -> Threads");
        setIconImages(FaviconImage.getFrameIconImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));

        setLayout(new MigLayout(
                "insets 10",
                "[grow, fill]"
        ));

        add(new JScrollPane(console), "height 100%, wrap");

        pack();
        setLocationRelativeTo(mainUI);
        setVisible(true);

        checkTimer = new Timer(100, l -> {
            console.reset();

            Thread.getAllStackTraces().keySet().stream()
                    .sorted(Comparator.comparing(item -> item.getThreadGroup().getName() + item.getName()))
                    .forEach(thread -> {
                        try {
                            console.getDocument().insertString(
                                    console.getDocument().getLength(),
                                    String.format(
                                            "[%s] prior: %s => %s - %s\n",
                                            thread.getThreadGroup().getName(),
                                            thread.getPriority(),
                                            thread.getState(),
                                            thread.getName()
                                    ),
                                    null
                            );
                        } catch (BadLocationException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
        checkTimer.start();
    }

    @Override
    public void dispose(){
        checkTimer.stop();
        super.dispose();
    }
}
