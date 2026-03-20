package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxFolderOpenIcon;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT;
import static javax.swing.JFileChooser.FILES_ONLY;

public class FileChooser extends JTextField {
    private static final Icon FOLDER_OPEN_ICON = new BxFolderOpenIcon();

    private final List<OnChangeFI> onChangeList = new LinkedList<>();
    private final IconButton btnSelect = new IconButton(FOLDER_OPEN_ICON, "Select");

    @Getter
    private File value;

    private final int fileSelectionMode;

    public FileChooser() {
        this(FILES_ONLY);
    }

    public FileChooser(int fileSelectionMode) {
        this.fileSelectionMode = fileSelectionMode;

        putClientProperty(TEXT_FIELD_TRAILING_COMPONENT, btnSelect);
        setEditable(false);

        btnSelect.addActionListener(l -> btnSelectAction());
    }

    private static File currentDir = null;
    private File getCurrentDirectory(){
        if(value == null) return currentDir;
        if(value.isFile()) return value.getParentFile();
        return value;
    }

    private void btnSelectAction(){
        JFileChooser file = new JFileChooser(getCurrentDirectory());
        file.setFileSelectionMode(fileSelectionMode);

        if(file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            setValue(file.getSelectedFile());
            onChangeList.forEach(ch -> ch.change(value));
            currentDir = file.getSelectedFile().getParentFile();
        }
    }

    public void setValue(File file){
        setText(file != null ? file.getAbsolutePath() : null);
        value = file;
    }

    public void setValue(String path){
        if(StringUtils.isBlank(path)){
            value = null;
            setText(null);
        }else{
            value = new File(path);
            setText(value.getAbsolutePath());
        }
    }

    @Override
    public void setEnabled(boolean enabled){
        setEditable(enabled);
        btnSelect.setEnabled(enabled);
    }

    public void addOnChange(OnChangeFI value){
        onChangeList.add(value);
    }

    @FunctionalInterface
    public interface OnChangeFI {
        void change(File file);
    }
}
