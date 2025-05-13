package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.io.converter.InterfaceConverter;
import io.github.clagomess.tomato.ui.collection.CollectionExportFrameInterface;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class CollectionExportFrameController {
    private final CollectionExportFrameInterface ui;

    public CollectionExportFrameController(
            CollectionExportFrameInterface ui
    ) {
        this.ui = ui;
    }

    public void export(
            CollectionTreeDto collection,
            InterfaceConverter converter
    ) throws IOException {
        if (collection == null) throw new RuntimeException("Collection is empty");
        if (converter == null) throw new RuntimeException("Type is empty");

        String targetFileName = collection.getName() +
                converter.getCollectionDumpFileSuffix();

        Optional<File> targetFile = ui.getExportFile(targetFileName);
        if (targetFile.isEmpty()) return;

        converter.dumpCollection(targetFile.get(), collection);
    }
}
