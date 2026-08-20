package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.controller.environment.EnvironmentExportFrameController;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.component.ConverterComboBox;
import io.github.clagomess.tomato.ui.component.FileExport;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

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
        new WaitExecution(this, btnExport, () -> {
            controller.export(
                    cbEnvironment.getSelectedItem(),
                    cbConverter.getSelectedItem()
            );

            setVisible(false);
            dispose();
        }).execute();
    }

    public void exportFile(
            String name,
            FileExport.Consumer consumer
    ) throws IOException {
        var file = new FileExport(this);
        file.setSelectedFile(new File(name));
        file.save(consumer);
    }
}
