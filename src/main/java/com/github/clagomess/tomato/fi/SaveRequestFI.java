package com.github.clagomess.tomato.fi;

import com.github.clagomess.tomato.dto.RequestDto;

@FunctionalInterface
public interface SaveRequestFI {
    void saveRequest(RequestDto dto);
}
