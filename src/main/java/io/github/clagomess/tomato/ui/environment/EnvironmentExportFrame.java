package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.controller.environment.EnvironmentExportFrameController;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.component.ConverterComboBox;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import io.github.clagomess.tomato.ui.component.FileExport;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static javax.swing.SwingUtilities.invokeLater;

public class EnvironmentExportFrame
        extends BaseFrame
        implements EnvironmentExportFrameInterface {
    private final EnvironmentComboBox cbEnvironment = new EnvironmentComboBox();
    private final JButton btnExport = new JButton("Export");
    private final ConverterComboBox cbConverter = new ConverterComboBox();

    private final EnvironmentExportFrameController controller;

    public EnvironmentExportFrame(
            Component parent
    ){
        controller = new EnvironmentExportFrameController(this);

        setTitle("Export Environment");
        setMinimumSize(new Dimension(300, 100));
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

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnExport.addActionListener(l -> btnExportAction());
    }

    private void btnExportAction(){
        var selected = cbEnvironment.getSelectedItem();
        var converter = cbConverter.getSelectedItem();

        new WaitExecution(this, btnExport)
                .thenBackground(() -> controller.export(selected, converter))
                .execute();
    }

    @Override
    public void exportFile(
            String name,
            FileExport.Consumer consumer
    ) {
        invokeLater(() -> {
            try {
                var fileExport = new FileExport(this);
                fileExport.setSelectedFile(new File(name));

                var target = fileExport.getTargetFile();
                if(target.isEmpty()) return;

                new WaitExecution(this, btnExport)
                        .thenBackground(() -> consumer.accept(target.get()))
                        .thenEdt(() -> {
                            fileExport.showSuccessMessage(target.get());
                            setVisible(false);
                            dispose();
                        })
                        .execute();
            } catch (IOException e) {
                new ExceptionDialog(this, e);
            }
        });
    }
}
