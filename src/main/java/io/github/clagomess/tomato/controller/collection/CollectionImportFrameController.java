package io.github.clagomess.tomato.controller.collection;

import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.exception.ConverterTypeEmptyException;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.converter.InterfaceConverter;
import io.github.clagomess.tomato.publisher.CollectionPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.ParentCollectionKey;

import java.io.File;
import java.io.IOException;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class CollectionImportFrameController {
    private final CollectionPublisher collectionPublisher;

    public CollectionImportFrameController() {
        this.collectionPublisher = CollectionPublisher.getInstance();
    }

    public void importCollection(
            CollectionTreeDto parent,
            InterfaceConverter converter,
            File sourceFile
    ) throws IOException {
        if(parent == null) throw new TomatoException("Destination is empty");
        if(converter == null) throw new ConverterTypeEmptyException();

        CollectionDto dto = converter.pumpCollection(
                parent.getPath(),
                sourceFile
        );

        var key = new ParentCollectionKey(parent.getId());
        collectionPublisher.getOnChange()
                .publish(key, new PublisherEvent<>(INSERTED, dto.getId()));
    }
}
