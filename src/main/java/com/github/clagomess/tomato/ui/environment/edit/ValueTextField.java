package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.io.keystore.EnvironmentKeystore;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLockIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxLockOpenIcon;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT;
import static com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.TEXT;

@Slf4j
public class ValueTextField extends ListenableTextField {
    private static final String UNKNOW_SECRET = "***";

    private final EnvironmentItemDto item;
    private final OnChangeFI onChangeFI;

    private final EnvironmentKeystore environmentKeystore;

    private final IconButton btnUnlockSecret = new IconButton(
            new BxLockIcon(),
            new BxLockOpenIcon(),
            "Unlock Secret"
    );

    public ValueTextField(
            @NotNull EnvironmentKeystore environmentKeystore,
            @NotNull EnvironmentItemDto item,
            OnChangeFI onChangeFI
    ) {
        this.item = item;
        this.onChangeFI = onChangeFI;
        this.environmentKeystore = environmentKeystore;

        if(item.getType() == TEXT) {
            setText(item.getValue());
            addOnChange(onChangeFI);
        }else{
            setText(UNKNOW_SECRET);
            setEnabled(false);
            putClientProperty(
                    TEXT_FIELD_TRAILING_COMPONENT,
                    btnUnlockSecret
            );
        }

        btnUnlockSecret.addActionListener(e -> btnEditSecretAction());
    }

    public void btnEditSecretAction(){
        try {
            String text = null;

            if(item.getSecretId() != null) {
                text = environmentKeystore.loadSecret(item.getSecretId())
                        .orElse(null);
            }

            btnUnlockSecret.setEnabled(false);
            setEnabled(true);
            setText(text);
            addOnChange(onChangeFI);
        } catch (Throwable e) {
            new ExceptionDialog(this, e);
        }
    }
}
