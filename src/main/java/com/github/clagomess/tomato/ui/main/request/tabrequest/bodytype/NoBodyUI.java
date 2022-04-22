package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;

import javax.swing.*;

public class NoBodyUI extends JPanel implements BodyTypeUI {
    @Override
    public RequestDto.Body getNewDtoFromUI() {
        return null;
    }
}
