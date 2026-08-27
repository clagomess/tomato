package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

import static io.github.clagomess.tomato.io.repository.EnvironmentRepository.SYSENV_COLLECTION_FILE_DIR_KEY;
import static io.github.clagomess.tomato.util.FileUtils.COLLECTION_FILES_DIR;
import static javax.swing.JFileChooser.FILES_ONLY;

@Slf4j
public class RequestFileChooser extends FileChooser {
    private static final String DIALOG_TITLE = "Import File";
    private static final String DIALOG_MESSAGE = "Do you want to import this file to collection directory?";
    private final RequestHeadDto requestHead;

    public RequestFileChooser(@Nullable RequestHeadDto requestHead) {
        super(FILES_ONLY);
        this.requestHead = requestHead;
    }


    @Override
    protected void btnSelectAction(){
        super.btnSelectAction();
        new WaitExecution(this, btnSelect, this::importFile).execute();
    }

    public void importFile() throws IOException {
        if(requestHead == null) return;
        if(StringUtils.isBlank(getText())) return;

        int ret = JOptionPane.showConfirmDialog(
                this,
                DIALOG_MESSAGE,
                DIALOG_TITLE,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret != JOptionPane.OK_OPTION) return;

        File source = getValue();
        if(source == null) return;

        File collectionFilesDir = new File(requestHead.getParent().getPath(), COLLECTION_FILES_DIR);
        if(!collectionFilesDir.exists() && !collectionFilesDir.mkdirs()){
            throw new AccessDeniedException("Cannot create collection files directory");
        }

        File target = new File(collectionFilesDir, source.getName());
        new FileExport(this).save(source, target);

        setText("{{%s}}/%s".formatted(SYSENV_COLLECTION_FILE_DIR_KEY, source.getName()));
    }

}
