package io.github.clagomess.tomato.controller.environment;

import io.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import io.github.clagomess.tomato.exception.ConverterTypeEmptyException;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.converter.InterfaceConverter;
import io.github.clagomess.tomato.ui.environment.EnvironmentExportFrameInterface;

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
        if(environment == null) throw new TomatoException("Environment is empty");
        if(converter == null) throw new ConverterTypeEmptyException();

        String targetFileName = environment.getName() +
                converter.getEnvironmentDumpFileSuffix();

        ui.exportFile(targetFileName, file -> converter.dumpEnvironment(
                file,
                environment.getId()
        ));
    }
}
