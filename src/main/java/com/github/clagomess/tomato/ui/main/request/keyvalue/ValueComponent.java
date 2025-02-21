package com.github.clagomess.tomato.ui.main.request.keyvalue;

import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.FileKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.enums.RawBodyTypeEnum;
import com.github.clagomess.tomato.ui.component.FileChooser;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextField;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextfieldOptions;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static com.github.clagomess.tomato.dto.data.keyvalue.KeyValueTypeEnum.FILE;

@Slf4j
class ValueComponent<T extends KeyValueItemDto> {
    private final RequestStagingMonitor requestStagingMonitor;
    private final KeyValueOptions options;
    private final T item;

    @Getter
    private final JComponent component;

    public ValueComponent(
            RequestStagingMonitor requestStagingMonitor,
            KeyValueOptions options,
            T item
    ) {
        this.requestStagingMonitor = requestStagingMonitor;
        this.options = options;
        this.item = item;

        if(item instanceof FileKeyValueItemDto fvItem && fvItem.getType() == FILE){
            component = buildFileChooser(fvItem);
            return;
        }

        if(item instanceof ContentTypeKeyValueItemDto ctItem){
            component = buildEnvTextFieldContentType(ctItem);
            return;
        }

        component = buildEnvTextField(item);
    }

    private FileChooser buildFileChooser(FileKeyValueItemDto fvItem){
        var fileChooser = new FileChooser();
        fileChooser.setValue(fvItem.getValue());
        fileChooser.addOnChange(file -> {
            try {
                fvItem.setValueContentType(file != null ?
                        Files.probeContentType(file.toPath()) :
                        null
                );
            }catch(IOException e){
                log.warn(e.getMessage());
            }

            valueOnChange(file != null ? file.getAbsolutePath() : null);
        });
        return fileChooser;
    }

    private EnvTextField buildEnvTextField(
            T item
    ){
        var textField = new EnvTextField(EnvTextfieldOptions.builder().build());
        textField.setText(item.getValue());
        textField.addOnChange(this::valueOnChange);
        return textField;
    }

    private EnvTextField buildEnvTextFieldContentType(
            ContentTypeKeyValueItemDto ctItem
    ){
        var envTextfieldOptions = EnvTextfieldOptions.builder()
                .valueEditorShowContentTypeEdit(true)
                .valueEditorSelectedRawBodyType(
                        RawBodyTypeEnum.valueOfContentType(ctItem.getValueContentType())
                )
                .build();

        envTextfieldOptions.setValueEditorOnDispose((rawBodyType, text) -> {
            if(Objects.equals(
                    ctItem.getValueContentType(),
                    rawBodyType.getContentType().toString()
            )) return;

            envTextfieldOptions.setValueEditorSelectedRawBodyType(rawBodyType);
            ctItem.setValueContentType(rawBodyType.getContentType().toString());
            requestStagingMonitor.update();
            options.getOnChange().run(item);
        });

        var textField = new EnvTextField(envTextfieldOptions);
        textField.setText(item.getValue());
        textField.addOnChange(this::valueOnChange);
        return textField;
    }

    private void valueOnChange(String value){
        if(Objects.equals(value, item.getValue())) return;

        item.setValue(value);
        requestStagingMonitor.update();
        options.getOnChange().run(item);
    }

    public void setValue(String value){
        if(component instanceof EnvTextField envTextField){
            envTextField.setText(value);
        }

        if(component instanceof FileChooser fileChooser){
            fileChooser.setValue(value);
        }
    }

    public void dispose(){
        if(component instanceof EnvTextField envTextField){
            envTextField.dispose();
        }
    }
}
