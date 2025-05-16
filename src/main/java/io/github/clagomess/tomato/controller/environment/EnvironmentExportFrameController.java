package io.github.clagomess.tomato.controller.environment;

import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.io.converter.InterfaceConverter;
import io.github.clagomess.tomato.ui.environment.EnvironmentExportFrameInterface;

import java.io.File;
import java.util.Optional;

public class EnvironmentExportFrameController {
    private final EnvironmentExportFrameInterface ui;

    public EnvironmentExportFrameController(
            EnvironmentExportFrameInterface ui
    ) {
        this.ui = ui;
    }

    public void export(
            EnvironmentHeadDto environment,
            InterfaceConverter converter
    ) throws Exception {
        if(environment == null) throw new Exception("Environment is empty");
        if(converter == null) throw new Exception("Type is empty");

        String targetFileName = environment.getName() +
                converter.getEnvironmentDumpFileSuffix();

        Optional<File> targetFile = ui.getExportFile(targetFileName);
        if (targetFile.isEmpty()) return;

        converter.dumpEnvironment(
                targetFile.get(),
                environment.getId()
        );
    }
}
