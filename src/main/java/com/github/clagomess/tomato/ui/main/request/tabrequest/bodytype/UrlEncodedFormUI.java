package com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype;

import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.ui.main.request.tabrequest.RequestKeyValueTableUI;

public class UrlEncodedFormUI extends RequestKeyValueTableUI implements BodyTypeUI {
    public UrlEncodedFormUI(String keyColumnLabel, String valueColumnLabel) {
        super(keyColumnLabel, valueColumnLabel);
    }

    @Override
    public RequestDto.Body getNewDtoFromUI() {
        return null; //@TODO: implements
    }
}
