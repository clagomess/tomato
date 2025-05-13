package io.github.clagomess.tomato.ui.collection;

import java.io.File;
import java.util.Optional;

public interface CollectionExportFrameInterface {
    Optional<File> getExportFile(String name);
}
