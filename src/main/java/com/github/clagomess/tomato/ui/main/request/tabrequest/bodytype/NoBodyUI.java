package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;

import javax.swing.*;

public class NoBodyUI extends JPanel implements BodyTypeUI {
    @Override
    public RequestDto.Body getNewDtoFromUI() {
        RequestDto.Body body = new RequestDto.Body();
        body.setBodyType(BodyTypeEnum.NO_BODY);
        return body;
    }
}
