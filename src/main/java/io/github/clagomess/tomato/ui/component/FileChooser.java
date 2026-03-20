package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxFolderOpenIcon;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

import static com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT;
import static javax.swing.JFileChooser.FILES_ONLY;

public class FileChooser extends ListenableTextField {
    private static final Icon FOLDER_OPEN_ICON = new BxFolderOpenIcon();

    private final IconButton btnSelect = new IconButton(FOLDER_OPEN_ICON, "Select");
    private final int fileSelectionMode;

    public FileChooser() {
        this(FILES_ONLY);
    }

    public FileChooser(int fileSelectionMode) {
        this.fileSelectionMode = fileSelectionMode;

        putClientProperty(TEXT_FIELD_TRAILING_COMPONENT, btnSelect);

        btnSelect.addActionListener(l -> btnSelectAction());
    }

    private File getCurrentDirectory(){
        var file = getValue();
        if(file == null || !file.exists()) return null;
        return file.getParentFile();
    }

    private void btnSelectAction(){
        JFileChooser file = new JFileChooser(getCurrentDirectory());
        file.setFileSelectionMode(fileSelectionMode);

        if(file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            setValue(file.getSelectedFile());
            onChangeList.forEach(ch -> ch.change(getText()));
        }
    }

    public void setValue(File file){
        setText(file != null ? file.getAbsolutePath() : null);
    }

    public void setValue(String path){
        setText(path);
    }

    public @Nullable File getValue(){
        var value = getText();
        if(StringUtils.isBlank(value)) return null;

        return new File(value);
    }

    @Override
    public void setEnabled(boolean enabled){
        setEditable(enabled);
        btnSelect.setEnabled(enabled);
    }
}
