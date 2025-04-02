package com.github.clagomess.tomato.ui.environment.edit;

import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.io.keepass.EnvironmentSecret;
import com.github.clagomess.tomato.io.repository.WorkspaceRepository;
import com.github.clagomess.tomato.ui.component.*;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSaveIcon;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT;
import static com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.TEXT;

@Slf4j
public class ValueTextField extends ListenableTextField {
    private static final String UNKNOW_SECRET = "***";

    private final EnvironmentItemDto item;
    private final OnChangeFI onChangeFI;

    private final EnvironmentSecret environmentSecret;

    private final IconButton btnEditSecret = new IconButton(new BxSaveIcon(), "Edit Secret");

    public ValueTextField(
            @NotNull String environmentId,
            @NotNull EnvironmentItemDto item,
            OnChangeFI onChangeFI
    ) {
        this.item = item;
        this.onChangeFI = onChangeFI;
        this.environmentSecret = getEnvironmentSecret(environmentId);

        if(item.getType() == TEXT) {
            setText(item.getValue());
            addOnChange(onChangeFI);
        }else{
            setText(UNKNOW_SECRET);
            setEnabled(false);
            putClientProperty(
                    TEXT_FIELD_TRAILING_COMPONENT,
                    btnEditSecret
            );
        }

        btnEditSecret.addActionListener(e -> btnEditSecretAction());
    }

    private EnvironmentSecret getEnvironmentSecret(String environmentId) {
        try {
            File workspacePath = new WorkspaceRepository()
                    .getDataSessionWorkspace()
                    .getPath();

            var environmentSecret = new EnvironmentSecret(workspacePath, environmentId);
            environmentSecret.setGetPassword(() -> new PasswordDialog(this).showDialog());
            environmentSecret.setGetNewPassword(() -> new NewPasswordDialog(this).showDialog());

            return environmentSecret;
        }catch (IOException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void btnEditSecretAction(){
        try {
            String text = null;

            if(item.getSecretId() != null) {
                text = environmentSecret.loadSecret(item.getSecretId())
                        .orElse(null);
            }

            btnEditSecret.setEnabled(false);
            setEnabled(true);
            setText(text);
            addOnChange(onChangeFI);
        } catch (Throwable e) {
            new ExceptionDialog(this, e);
        }
    }
}
