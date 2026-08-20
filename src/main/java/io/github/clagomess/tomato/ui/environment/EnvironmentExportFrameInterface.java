package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.ui.component.FileExport;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface EnvironmentExportFrameInterface {
    void exportFile(
            String name,
            FileExport.Consumer consumer
    ) throws IOException;
}
