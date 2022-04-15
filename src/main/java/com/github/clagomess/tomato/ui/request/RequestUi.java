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
        tpRequest.addTab("Header", getHeader());

        pRequest.add(tpRequest, "span, height 100%");

        // --- RESPONSE

        JPanel pResponse = new JPanel();
        pResponse.add(new JButton("Content"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pRequest, pResponse);


        return splitPane;
    }

    public Component getEditor(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new MigLayout("", "[grow, fill]", ""));


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
        sp.setBorder(BorderFactory.createEmptyBorder());

        JPanel jradios = new JPanel();
        jradios.add(new JRadioButton("No Body"));
        jradios.add(new JRadioButton("Multipart Form"));
        jradios.add(new JRadioButton("URL Encoded Form"));
        jradios.add(new JRadioButton("Raw"));
        jradios.add(new JRadioButton("Binary"));

        jPanel.add(jradios, "wrap");
        jPanel.add(sp, "height 100%");

        return jPanel;
    }

    public Component getHeader(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JTable table = new JTable(new String[][]{
                {"Content-Type", "application/json"},
                {"Origin", "localhost"},
        }, new String[]{"Header", "Value"});

        JScrollPane spane = new JScrollPane();
        spane.add(table);
        jPanel.add(spane);
        return table;
    }
}
