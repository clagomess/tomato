package com.github.clagomess.tomato.ui.component;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Getter
public class FileChooserComponent extends JPanel {
    private final List<OnChangeFI> onChangeList = new LinkedList<>();
    private final JTextField txtFilepath = new JTextField();
    private final JButton btnSelect = new JButton("Select");
    protected File selectedFile;

    public FileChooserComponent() {
        setLayout(new MigLayout("insets 0 0 0 0", "[grow, fill]", ""));

        add(txtFilepath, "width 100%");
        add(btnSelect);

        btnSelect.addActionListener(l -> btnSelectAction());
    }

    private void btnSelectAction(){
        JFileChooser file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if(file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            selectedFile = file.getSelectedFile();
            txtFilepath.setText(selectedFile.getAbsolutePath());
            onChangeList.forEach(ch -> ch.change(selectedFile));
        }
    }

    @Override
    public void setEnabled(boolean enabled){
        txtFilepath.setEnabled(enabled);
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
