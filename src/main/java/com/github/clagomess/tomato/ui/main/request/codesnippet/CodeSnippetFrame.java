package com.github.clagomess.tomato.ui.main.request.codesnippet;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class CodeSnippetFrame extends JFrame {
    private final CodeSnippetComboBox codeSnippetComboBox = new CodeSnippetComboBox();
    private final TRSyntaxTextArea textArea = new TRSyntaxTextArea();

    public CodeSnippetFrame(
            Component parent,
            RequestDto request
    ) {
        setTitle("Code Snippet");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "insets 10",
                "[grow,fill]"
        ));

        add(codeSnippetComboBox, "wrap");

        var sp = TRSyntaxTextArea.createScroll(textArea);
        sp.setBorder(new MatteBorder(1, 1, 1, 1, ColorConstant.GRAY));
        add(sp, "height 100%");

        codeSnippetComboBox.addActionListener(l -> refresh(request));

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        refresh(request);
    }

    private void refresh(RequestDto request){
        new WaitExecution(this, () -> {
            var codeSnippet = codeSnippetComboBox.getSelectedItem();
            if(codeSnippet == null){
                textArea.setText("");
                return;
            }

            textArea.setText(codeSnippet.build(request));
            textArea.setSyntaxEditingStyle(codeSnippet.getSyntaxStyle());
        }).execute();
    }
}
