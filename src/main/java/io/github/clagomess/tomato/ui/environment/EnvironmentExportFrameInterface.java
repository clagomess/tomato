package io.github.clagomess.tomato.ui.environment;

import java.io.File;
import java.util.Optional;

public interface EnvironmentExportFrameInterface {
    Optional<File> getExportFile(String name);
}
