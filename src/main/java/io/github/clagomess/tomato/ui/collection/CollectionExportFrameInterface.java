package io.github.clagomess.tomato.ui.collection;

import io.github.clagomess.tomato.ui.component.FileExport;

import java.io.IOException;

public interface CollectionExportFrameInterface {
    void exportFile(
            String name,
            FileExport.Consumer consumer
    ) throws IOException;
}
