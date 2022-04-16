package com.github.clagomess.tomato.ui.request.tabrequest;

import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.factory.EditorFactory;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

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
        add(getHttpMethodsComponent());
        add(txtRequestUrl, "width 100%");
        add(btnSendRequest);
        add(btnSaveRequest, "wrap");

        JTabbedPane tpRequest = new JTabbedPane();
        tpRequest.addTab("Body", getBody());
        tpRequest.addTab("Header", getHeader());

        add(tpRequest, "span, height 100%");
    }

    public JComboBox<String> getHttpMethodsComponent(){
        JComboBox<String> httpMethod = new JComboBox<>();
        Arrays.stream(HttpMethodEnum.values()).forEach(item -> httpMethod.addItem(item.getMethod()));
        return httpMethod;
    }

    public Component getBody(){
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new MigLayout("", "[grow, fill]", ""));

        ButtonGroup bgBodyType = new ButtonGroup();
        JPanel pRadioBodyType = new JPanel();

        Arrays.stream(BodyTypeEnum.values()).forEach(item -> {
            JRadioButton rbBodyType = new JRadioButton(item.getDescription());
            rbBodyType.setSelected(item == BodyTypeEnum.NO_BODY);
            rbBodyType.addActionListener(l -> {
                bodyPanel.remove(1);
                bodyPanel.add(getBodyType(item));
                bodyPanel.revalidate();
                bodyPanel.repaint();
            });

            bgBodyType.add(rbBodyType);
            pRadioBodyType.add(rbBodyType);
        });

        bodyPanel.add(pRadioBodyType, "wrap");
        bodyPanel.add(getBodyType(BodyTypeEnum.NO_BODY), "height 100%");

        return bodyPanel;
    }

    private Component getBodyType(BodyTypeEnum bodyType){
        switch (bodyType){
            case MULTIPART_FORM:
                return getMultiPartFormBodyType();
            case URL_ENCODED_FORM:
                return getUrlEncodedFormBodyType();
            case RAW:
                return getRawBodyType();
            case BINARY:
                return getBinaryBodyType();
            default:
                return getNoBodyBodyType();
        }
    }

    private Component getNoBodyBodyType(){
        return new JPanel();
    }

    private Component getMultiPartFormBodyType(){
        return new JPanel();
    }

    private Component getUrlEncodedFormBodyType(){
        return new JPanel();
    }

    private Component getRawBodyType(){
        RSyntaxTextArea textArea = EditorFactory.getInstance().createEditor();
        return EditorFactory.createScroll(textArea);
    }

    private Component getBinaryBodyType(){
        return new JPanel();
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
