package com.github.clagomess.tomato.ui.request.tabrequest;

import com.github.clagomess.tomato.factory.EditorFactory;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;

@Getter
public class TabRequestUi extends JPanel {
    private final JLabel lblRequestName = new JLabel("FOO - API / /api/get/test");
    private final JTextField txtRequestUrl = new JTextField();
    private final JButton btnSendRequest = new JButton("Send");
    private final JButton btnSaveRequest = new JButton("Save");

    public TabRequestUi(){
        setLayout(new MigLayout("insets 10 0 10 0", "[grow,fill]", ""));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY)); //@TODO: fix color

        add(lblRequestName, "span");
        add(new JSeparator(JSeparator.HORIZONTAL), "span");
        add(getHttpOptionsComponent());
        add(txtRequestUrl, "width 100%");
        add(btnSendRequest);
        add(btnSaveRequest, "wrap");

        JTabbedPane tpRequest = new JTabbedPane();
        tpRequest.addTab("Body", getBody());
        tpRequest.addTab("Header", getHeader());

        add(tpRequest, "span, height 100%");
    }

    public JComboBox<String> getHttpOptionsComponent(){
        JComboBox<String> httpOptions = new JComboBox<>();
        httpOptions.addItem("GET");
        httpOptions.addItem("PUT");
        httpOptions.addItem("OPTIONS");
        return httpOptions;
    }

    public Component getBody(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new MigLayout("", "[grow, fill]", ""));

        RSyntaxTextArea textArea = EditorFactory.getInstance().createEditor();

        JPanel jradios = new JPanel();
        jradios.add(new JRadioButton("No Body"));
        jradios.add(new JRadioButton("Multipart Form"));
        jradios.add(new JRadioButton("URL Encoded Form"));
        jradios.add(new JRadioButton("Raw"));
        jradios.add(new JRadioButton("Binary"));

        jPanel.add(jradios, "wrap");
        jPanel.add(EditorFactory.createScroll(textArea), "height 100%");

        return jPanel;
    }

    public Component getHeader(){
        JTable table = new JTable(new String[][]{
                {"Content-Type", "application/json"},
                {"Origin", "localhost"},
        }, new String[]{"Header", "Value"});

        JScrollPane spane = new JScrollPane();
        spane.setViewportView(table);
        return spane;
    }
}
