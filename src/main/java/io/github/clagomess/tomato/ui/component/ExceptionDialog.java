package io.github.clagomess.tomato.ui.component;

import com.formdev.flatlaf.icons.FlatOptionPaneErrorIcon;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.ui.BaseDialog;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

@Slf4j
public class ExceptionDialog extends BaseDialog {
    public ExceptionDialog(
            Component parent,
            String message
    ) {
        this(parent, new TomatoException(message));
    }

    public ExceptionDialog(
            Component parent,
            Exception e
    ) {
        super(parent, "Error");

        log.error(e.getMessage(), e);

        setMinimumSize(new Dimension(350, 130));
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "",
                "[][grow, fill]"
        ));

        add(new JLabel(new FlatOptionPaneErrorIcon()), "top, width 45");

        JTextArea txtConsole = new JTextArea();
        txtConsole.setLineWrap(true);
        txtConsole.setWrapStyleWord(true);
        txtConsole.setEditable(false);
        txtConsole.setText(e.getMessage());

        JScrollPane sp = new JScrollPane(
                txtConsole,
                VERTICAL_SCROLLBAR_AS_NEEDED,
                HORIZONTAL_SCROLLBAR_NEVER
        );
        sp.setBorder(BorderFactory.createEmptyBorder());

        add(sp, "height 100%, wrap");

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(l -> dispose());
        add(btnOk, "span 2, align right");

        getRootPane().setDefaultButton(btnOk);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
