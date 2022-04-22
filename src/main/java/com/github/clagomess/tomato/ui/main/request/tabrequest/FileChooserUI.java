package com.github.clagomess.tomato.ui.main.request.tabrequest;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.File;

@Getter
public class FileChooserUI extends JPanel {
    private final JTextField txtFilepath = new JTextField();
    private final JButton btnSelect = new JButton("Select");
    protected File selectedFile;

    public FileChooserUI() {
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
            txtFilepath.setText(file.getSelectedFile().getAbsolutePath());
        }
    }

    @Override
    public void setEnabled(boolean enabled){
        txtFilepath.setEnabled(enabled);
        btnSelect.setEnabled(enabled);
    }
}
