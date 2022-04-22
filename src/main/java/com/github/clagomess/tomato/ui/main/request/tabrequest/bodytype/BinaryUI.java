package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.ui.main.request.tabrequest.FileChooserUI;

public class BinaryUI extends FileChooserUI implements BodyTypeUI {
    @Override
    public RequestDto.Body getNewDtoFromUI() {
        return null; //@TODO: implements
    }
}
