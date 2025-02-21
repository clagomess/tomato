package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.controller.environment.EnvironmentComboBoxController;
import com.github.clagomess.tomato.dto.tree.EnvironmentHeadDto;
import com.github.clagomess.tomato.io.converter.InterfaceConverter;
import com.github.clagomess.tomato.ui.component.ConverterComboBox;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ForkJoinPool;

import static javax.swing.SwingUtilities.invokeLater;

public class EnvironmentExportFrame extends JFrame {
    private final EnvironmentComboBox cbEnvironment = new EnvironmentComboBox();
    private final JButton btnExport = new JButton("Export");
    private final ConverterComboBox cbConverter = new ConverterComboBox();

    private final EnvironmentComboBoxController controller = new EnvironmentComboBoxController();

    public EnvironmentExportFrame(
            Component parent
    ){
        setTitle("Export Environment");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Environment"), "wrap");
        add(cbEnvironment, "width 300!, wrap");
        add(new JLabel("Type"), "wrap");
        add(cbConverter, "width 300!, wrap");
        add(btnExport, "align right");

        getRootPane().setDefaultButton(btnExport);

        ForkJoinPool.commonPool().submit(() -> {
            try {
                controller.loadItems((selected, item) -> invokeLater(() -> {
                    cbEnvironment.addItem(item);
                    if(selected) cbEnvironment.setSelectedItem(item);
                }), () -> {});
            } catch (Throwable e){
                invokeLater(() -> new ExceptionDialog(this, e));
            }
        });

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnExport.addActionListener(l -> btnExportAction());
    }

    private void btnExportAction(){
        new WaitExecution(this, btnExport, () -> {
            EnvironmentHeadDto selected = cbEnvironment.getSelectedItem();
            if(selected == null) throw new Exception("Environment is empty");

            InterfaceConverter converter = cbConverter.getSelectedItem();
            if(converter == null) throw new Exception("Type is empty");

            JFileChooser file = new JFileChooser();
            file.setFileSelectionMode(JFileChooser.FILES_ONLY);
            file.setSelectedFile(new File(
                    selected.getName() +
                    converter.getEnvironmentDumpFileSuffix()
            ));

            if(file.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
                return;
            }

            converter.dumpEnvironment(
                    file.getSelectedFile(),
                    selected.getId()
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
