package com.github.clagomess.tomato.ui.main.request.left.bodytype;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.ui.component.TRSyntaxTextArea;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@Slf4j
public class RawBodyUI extends JPanel implements BodyTypeUI {
    private final RequestDto requestDto;
    private final RequestStagingMonitor requestStagingMonitor;

    private final JComboBox<RawBodyTypeEnum> cbContentType = new JComboBox<>(
            RawBodyTypeEnum.values()
    );
    private final TRSyntaxTextArea textArea = new TRSyntaxTextArea();

    public RawBodyUI(
            RequestDto requestDto,
            RequestStagingMonitor requestStagingMonitor
    ){
        this.requestDto = requestDto;
        this.requestStagingMonitor = requestStagingMonitor;
        var raw = requestDto.getBody().getRaw();

        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        cbContentType.setSelectedItem(raw.getType());
        cbContentType.addActionListener(l -> cbContentTypeAction());

        add(cbContentType, "wrap");
        add(TRSyntaxTextArea.createScroll(textArea), "height 100%");

        textArea.setSyntaxEditingStyle(raw.getType().getSyntaxStyle());
        textArea.setText(raw.getRaw());
        textArea.addOnChange(value -> {
            raw.setRaw(value);
            requestStagingMonitor.setActualHashCode(requestDto);
        });
    }

    private void cbContentTypeAction(){
        RawBodyTypeEnum type = (RawBodyTypeEnum) cbContentType.getSelectedItem();
        if(type == null) return;
        textArea.setSyntaxEditingStyle(type.getSyntaxStyle());
        requestDto.getBody().getRaw().setType(type);
        requestStagingMonitor.setActualHashCode(requestDto);
    }
}
