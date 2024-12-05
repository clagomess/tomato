package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.ui.main.request.tabrequest.RequestKeyValueTableUI;

public class UrlEncodedFormUI extends RequestKeyValueTableUI implements BodyTypeUI {
    public UrlEncodedFormUI(String keyColumnLabel, String valueColumnLabel) {
        super(keyColumnLabel, valueColumnLabel);
    }

    public RequestDto.Body getNewDtoFromUI() {
        RequestDto.Body body = new RequestDto.Body();
        body.setBodyType(BodyTypeEnum.URL_ENCODED_FORM);
        body.setUrlEncodedForm(getNewListDtoFromUI());
        return body;
    }
}
