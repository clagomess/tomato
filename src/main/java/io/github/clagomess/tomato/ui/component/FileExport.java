package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.io.repository.WorkspaceSessionRepository;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class FileExport extends JFileChooser {
    private final Component parentComponent;
    private final WorkspaceSessionRepository workspaceSessionRepository;

    private static final String OVERRIDE_TITLE = "Replace File";
    private static final String OVERRIDE_MESSAGE = "A file named \"%s\" already exists in \"%s\".\nDo you want to replace it?";
    private static final String SUCCESS_TITLE = "File Saved";
    private static final String SUCCESS_MESSAGE = "File saved successfully to:\n%s";

    public FileExport(Component parentComponent) throws IOException {
        super();
        this.parentComponent = parentComponent;
        this.workspaceSessionRepository = new WorkspaceSessionRepository();

        var lastOpenedDirectory = workspaceSessionRepository.load().getLastOpenedDirectory();
        if(StringUtils.isNotBlank(lastOpenedDirectory)) {
            setCurrentDirectory(new File(lastOpenedDirectory));
        }

        setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    public Optional<File> save(File source) throws IOException {
        Optional<File> target = getTargetFile();

        if(target.isPresent()){
            Files.copy(
                    source.toPath(),
                    target.get().toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
            showSuccessMessage(target.get());
        }

        return target;
    }

    public void save(Consumer consumer) throws IOException {
        Optional<File> target = getTargetFile();

        if(target.isPresent()){
            consumer.accept(target.get());
            showSuccessMessage(target.get());
        }
    }

    public boolean save(File source, File target) throws IOException{
        if(!source.exists()) throw new FileNotFoundException(source.getAbsolutePath());
        if(target.exists() && cantOverrideFile(target)) return false;

        Files.copy(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );

        return true;
    }

    protected Optional<File> getTargetFile() throws IOException {
        if(showSaveDialog(parentComponent) != JFileChooser.APPROVE_OPTION){
            return Optional.empty();
        }

        if(getSelectedFile().exists() && cantOverrideFile(getSelectedFile())){
            return Optional.empty();
        }

        var session = workspaceSessionRepository.load();
        session.setLastOpenedDirectory(getCurrentDirectory() != null
                ? getCurrentDirectory().getAbsolutePath()
                : null);
        workspaceSessionRepository.save(session);

        return Optional.of(getSelectedFile());
    }

    protected boolean cantOverrideFile(File target){
        int ret = JOptionPane.showConfirmDialog(
                parentComponent,
                String.format(
                        OVERRIDE_MESSAGE,
                        target.getName(),
                        target.getAbsoluteFile().getParent()
                ),
                OVERRIDE_TITLE,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return ret != JOptionPane.OK_OPTION;
    }

    protected void showSuccessMessage(File target){
        JOptionPane.showMessageDialog(
                parentComponent,
                String.format(
                        SUCCESS_MESSAGE,
                        target.getAbsolutePath()
                ),
                SUCCESS_TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @FunctionalInterface
    public interface Consumer {
        void accept(File file) throws IOException;
    }
}
