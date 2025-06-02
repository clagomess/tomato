package io.github.clagomess.tomato.ui.environment.edit;

import io.github.clagomess.tomato.controller.environment.edit.ValueTextFieldController;
import io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import io.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.ListenableTextField;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLockIcon;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLockOpenIcon;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

import static com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT;
import static io.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.TEXT;

@Slf4j
public class ValueTextField
        extends ListenableTextField
        implements ValueTextFieldInterface {
    private static final String UNKNOW_SECRET = "***";
    private static final Icon LOCK_ICON = new BxLockIcon();
    private static final Icon LOCK_OPEN_ICON = new BxLockOpenIcon();

    private final ValueTextFieldController controller;
    private final OnChangeFI onChangeFI;

    private final IconButton btnUnlockSecret = new IconButton(
            LOCK_ICON,
            LOCK_OPEN_ICON,
            "Unlock Secret"
    );

    public ValueTextField(
            @NotNull EnvironmentKeystore environmentKeystore,
            @NotNull EnvironmentItemDto item,
            OnChangeFI onChangeFI
    ) {
        this.onChangeFI = onChangeFI;

        controller = new ValueTextFieldController(
                this,
                environmentKeystore,
                item
        );

        if(item.getType() == TEXT) {
            setText(item.getValue());
            setOnChangeEnabled(true);
        }else{
            setText(UNKNOW_SECRET);
            setEnabled(false);
            putClientProperty(
                    TEXT_FIELD_TRAILING_COMPONENT,
                    btnUnlockSecret
            );
        }

        btnUnlockSecret.addActionListener(e -> btnUnlockSecretAction());
    }

    public void setOnChangeEnabled(boolean enabled){
        if(enabled){
            addOnChange(onChangeFI);
        }else{
            removeOnChange(onChangeFI);
        }
    }

    public void revealSecret(){
        btnUnlockSecret.setEnabled(false);
        setEnabled(true);
        setOnChangeEnabled(true);
    }

    private void btnUnlockSecretAction(){
        try {
            controller.unlockSecret();
        } catch (Exception e) {
            new ExceptionDialog(this, e);
        }
    }

    public String getSecret() throws IOException {
        return controller.unlockSecret();
    }
}
