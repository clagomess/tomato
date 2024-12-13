package com.github.clagomess.tomato.ui.main.request.left.bodytype;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.ContentTypeEnum;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@Slf4j
public class RawBodyUI extends JPanel implements BodyTypeUI {
    private final JComboBox<ContentTypeEnum> cbContentType = new JComboBox<>(
            ContentTypeEnum.values()
    );
    private final TRSyntaxTextArea textArea = new TRSyntaxTextArea();

    public RawBodyUI(){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        cbContentType.setSelectedItem(ContentTypeEnum.JSON.getSyntaxStyle());
        cbContentType.addActionListener(l -> cbContentTypeAction());

        add(cbContentType, "wrap");
        add(TRSyntaxTextArea.createScroll(textArea), "height 100%");
    }

    private void cbContentTypeAction(){
        ContentTypeEnum contentType = (ContentTypeEnum) cbContentType.getSelectedItem();
        if(contentType == null) return;
        textArea.setSyntaxEditingStyle(contentType.getSyntaxStyle());
    }

//    @Override
    public RequestDto.Body getNewDtoFromUI() {
        RequestDto.Body body = new RequestDto.Body();
        body.setType(BodyTypeEnum.RAW);
//        body.setRaw(textArea.getText()); // @TODO: check
        return body;
    }
}
