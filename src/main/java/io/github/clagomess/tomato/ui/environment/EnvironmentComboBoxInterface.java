package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;

public interface EnvironmentComboBoxInterface {
    void addItem(EnvironmentHeadDto head);
    void setSelectedItem(EnvironmentHeadDto head);
}
