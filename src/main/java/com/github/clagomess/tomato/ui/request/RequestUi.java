package com.github.clagomess.tomato.ui.request;

import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RequestUi extends JTabbedPane {
    public RequestUi(){
        addTab("Aoba", getTabContent());
        addTab("Aoba - 2", getTabContent());
    }

    public JSplitPane getTabContent(){
        JPanel pRequest = new JPanel();
        pRequest.setLayout(new MigLayout("insets 10 0 10 0", "[grow,fill]", ""));
        pRequest.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY)); //@TODO: fix color

        JComboBox<String> httpOptions = new JComboBox<>();
        httpOptions.addItem("GET");
        httpOptions.addItem("PUT");
        httpOptions.addItem("OPTIONS");

        pRequest.add(new JLabel("FOO - API / /api/get/test"), "span");
        pRequest.add(new JSeparator(JSeparator.HORIZONTAL), "span");
        pRequest.add(httpOptions);
        pRequest.add(new JTextField(), "width 100%");
        pRequest.add(new JButton("Send"));
        pRequest.add(new JButton("Save"), "wrap");

        JTabbedPane tpRequest = new JTabbedPane();
        tpRequest.addTab("Body", getEditor());
        tpRequest.addTab("Header", new JPanel(new BorderLayout()));

        pRequest.add(tpRequest, "span, pushx");

        // --- RESPONSE

        JPanel pResponse = new JPanel();
        pResponse.add(new JButton("Content"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pRequest, pResponse);
        splitPane.setDividerLocation(.5);


        return splitPane;
    }

    public JPanel getEditor(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setMinimumSize(new Dimension(0, 500));

        Theme theme = null;
        try {
            theme = Theme.load(getClass().getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        RSyntaxTextArea textArea = new RSyntaxTextArea();
        theme.apply(textArea);

        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);

        jPanel.add(sp);

        return jPanel;
    }
}
