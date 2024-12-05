package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.ui.component.FileChooserComponent;

public class BinaryUI extends FileChooserComponent implements BodyTypeUI {
//    @Override
    public RequestDto.Body getNewDtoFromUI() {
        RequestDto.Body body = new RequestDto.Body();
        body.setBodyType(BodyTypeEnum.BINARY);
        body.setBinaryFilePath(selectedFile.getAbsolutePath());
        return body;
    }
}
