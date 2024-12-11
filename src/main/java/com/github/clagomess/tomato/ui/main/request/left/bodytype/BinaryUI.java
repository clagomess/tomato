package com.github.clagomess.tomato.ui.main.request.left.bodytype;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.ui.component.FileChooser;

public class BinaryUI extends FileChooser implements BodyTypeUI {
//    @Override
    public RequestDto.Body getNewDtoFromUI() {
        RequestDto.Body body = new RequestDto.Body();
        body.setBodyType(BodyTypeEnum.BINARY);
        body.setBinaryFilePath(selectedFile.getAbsolutePath());
        return body;
    }
}
