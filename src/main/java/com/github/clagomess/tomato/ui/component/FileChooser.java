package com.github.clagomess.tomato.ui.component;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileChooser extends JPanel {
    private final List<OnChangeFI> onChangeList = new LinkedList<>();
    private final JTextField txtFilepath = new JTextField();
    private final JButton btnSelect = new JButton("Select");

    @Getter
    private File value;

    public FileChooser() {
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        add(txtFilepath, "width 100%");
        add(btnSelect);

        btnSelect.addActionListener(l -> btnSelectAction());
        txtFilepath.setEditable(false);
    }

    private void btnSelectAction(){
        JFileChooser file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if(file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            setValue(file.getSelectedFile());
            onChangeList.forEach(ch -> ch.change(value));
        }
    }

    public void setValue(File file){
        txtFilepath.setText(file != null ? file.getAbsolutePath() : null);
        value = file;
    }

    public void setValue(String path){
        if(StringUtils.isBlank(path)){
            value = null;
            txtFilepath.setText(null);
        }else{
            value = new File(path);
            txtFilepath.setText(value.getAbsolutePath());
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
