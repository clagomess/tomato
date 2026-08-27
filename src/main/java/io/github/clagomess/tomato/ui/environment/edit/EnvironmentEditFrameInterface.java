package io.github.clagomess.tomato.ui.environment.edit;

import io.github.clagomess.tomato.dto.data.EnvironmentDto;
import io.github.clagomess.tomato.ui.component.stagingmonitor.FrameTitleStagingMonitor;

public interface EnvironmentEditFrameInterface extends FrameTitleStagingMonitor<EnvironmentDto> {
    String getPassword();
    String getNewPassword();
}
