package io.github.clagomess.tomato.ui.settings;

import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.component.RawTextArea;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.Comparator;

public class DebugThreadsFrame extends JFrame {
    private final RawTextArea console = new RawTextArea();
    private final Timer checkTimer;

    public DebugThreadsFrame(MainFrame mainFrame) {
        setTitle("Debug -> Threads");
        setIconImages(FaviconImage.getFrameIconImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));

        setLayout(new MigLayout(
                "insets 10",
                "[grow, fill]"
        ));

        add(new JScrollPane(console), "height 100%, wrap");

        pack();
        setLocationRelativeTo(mainFrame);
        setVisible(true);

        checkTimer = new Timer(100, l -> {
            console.reset();

            Thread.getAllStackTraces().keySet().stream()
                    .sorted(Comparator.comparing(item -> {
                        if (item.getThreadGroup() != null) {
                            return item.getThreadGroup().getName() + item.getName();
                        } else {
                            return item.getName();
                        }
                    }))
                    .forEach(thread -> {
                        try {
                            String groupName = thread.getThreadGroup() != null ?
                                    thread.getThreadGroup().getName() :
                                    "";

                            console.getDocument().insertString(
                                    console.getDocument().getLength(),
                                    String.format(
                                            "[%s] prior: %s => %s - %s\n",
                                            groupName,
                                            thread.getPriority(),
                                            thread.getState(),
                                            thread.getName()
                                    ),
                                    null
                            );
                        } catch (BadLocationException e) {
                            throw new RuntimeException(e.getMessage(), e);
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
