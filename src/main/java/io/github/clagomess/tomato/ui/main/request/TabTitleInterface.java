package io.github.clagomess.tomato.ui.main.request;

import io.github.clagomess.tomato.dto.tree.RequestHeadDto;

public interface TabTitleInterface {
    void refresh(RequestHeadDto requestHead);
    void setIcon(boolean hasChanged);
}
