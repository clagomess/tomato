package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxFolderOpenIcon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.io.File;

import static com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT;
import static javax.swing.JFileChooser.FILES_ONLY;

@Slf4j
public class FileChooser extends ListenableTextField {
    private static final Icon FOLDER_OPEN_ICON = new BxFolderOpenIcon();

    private final IconButton btnSelect = new IconButton(FOLDER_OPEN_ICON, "Select");
    private final int fileSelectionMode;

    private final WorkspaceSessionRepository workspaceSessionRepository;

    public FileChooser() {
        this(FILES_ONLY);
    }

    public FileChooser(int fileSelectionMode) {
        this.fileSelectionMode = fileSelectionMode;
        this.workspaceSessionRepository = new WorkspaceSessionRepository();

        putClientProperty(TEXT_FIELD_TRAILING_COMPONENT, btnSelect);

        btnSelect.addActionListener(l -> btnSelectAction());
    }

    private File getCurrentDirectory(){
        var file = getValue();
        if(file != null && file.exists()){
            return file.getParentFile();
        }

        try {
            return workspaceSessionRepository.load().getLastOpenedDirectory();
        }catch (Exception e){
            log.warn(log.getName(), e.getMessage());
        }

        return null;
    }

    private void btnSelectAction(){
        JFileChooser file = new JFileChooser(getCurrentDirectory());
        file.setFileSelectionMode(fileSelectionMode);

        if(file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            setValue(file.getSelectedFile());

            try {
                var session = workspaceSessionRepository.load();
                session.setLastOpenedDirectory(file.getCurrentDirectory());
                workspaceSessionRepository.save(session);
            }catch (Exception e){
                log.warn(log.getName(), e.getMessage());
            }

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
