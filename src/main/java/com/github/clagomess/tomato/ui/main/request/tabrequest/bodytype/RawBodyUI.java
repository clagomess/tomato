package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.ContentTypeEnum;
import com.github.clagomess.tomato.factory.EditorFactory;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;

public class RawBodyUI extends JPanel implements BodyTypeUI {
    private final JComboBox<ContentTypeEnum> cbContentType = new JComboBox<>();
    private final RSyntaxTextArea textArea = EditorFactory.getInstance().createEditor();

    public RawBodyUI(){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        cbContentType.addItem(ContentTypeEnum.TEXT);
        cbContentType.addItem(ContentTypeEnum.JSON);
        cbContentType.addItem(ContentTypeEnum.XML);
        cbContentType.addItem(ContentTypeEnum.HTML);
        cbContentType.addItem(ContentTypeEnum.JAVASCRIPT);
        cbContentType.setSelectedItem(ContentTypeEnum.JSON.getSyntaxStyle());
        cbContentType.addActionListener(l -> cbContentTypeAction());

        add(cbContentType, "wrap");
        add(EditorFactory.createScroll(textArea), "height 100%");
    }

    private void cbContentTypeAction(){
        ContentTypeEnum contentType = (ContentTypeEnum) cbContentType.getSelectedItem();
        if(contentType == null) return;
        textArea.setSyntaxEditingStyle(contentType.getSyntaxStyle());
    }

//    @Override
    public RequestDto.Body getNewDtoFromUI() {
        RequestDto.Body body = new RequestDto.Body();
        body.setBodyType(BodyTypeEnum.RAW);
        body.setRaw(textArea.getText());
        return body;
    }
}
