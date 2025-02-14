package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.io.http.UrlBuilder;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.main.request.RequestSplitPaneUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ViewRenderedUrlFrame extends JFrame {
    public ViewRenderedUrlFrame(
            RequestSplitPaneUI parent,
            RequestDto requestDto
    ){
        setTitle("View Rendered Url");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(400, 200));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new MigLayout(
                "insets 10",
                "[grow, fill]"
        ));

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(new JScrollPane(textArea), "height 100%");

        pack();
        setLocationRelativeTo(parent);

        try {
            var result = new UrlBuilder(requestDto).buildUri();
            textArea.setText(result.toString());
            setVisible(true);
        } catch (Throwable e){
            new ExceptionDialog(parent, e);
            dispose();
        }
    }
}
