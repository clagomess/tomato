package io.github.clagomess.tomato.ui.settings;

import io.github.clagomess.tomato.dto.data.ConfigurationDto;
import io.github.clagomess.tomato.io.repository.ConfigurationRepository;
import io.github.clagomess.tomato.ui.MainFrame;
import io.github.clagomess.tomato.ui.component.ExceptionDialog;
import io.github.clagomess.tomato.ui.component.FileChooser;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.SwingUtilities.invokeLater;

public class ConfigurationFrame extends JFrame {
    private final ConfigurationRepository configurationRepository = new ConfigurationRepository();

    protected final JButton btnSave = new JButton("Save");
    protected final FileChooser fcDataDir = new FileChooser(DIRECTORIES_ONLY);

    public ConfigurationFrame(MainFrame mainFrame) {
        setTitle("Configuration");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Data Directory"), "wrap");
        add(fcDataDir, "width 300!, wrap");
        add(btnSave, "align right");

        getRootPane().setDefaultButton(btnSave);

        btnSave.addActionListener(e -> btnSaveAction());

        ForkJoinPool.commonPool().submit(() -> {
            try {
                ConfigurationDto configuration = configurationRepository.load();
                invokeLater(() -> {
                    fcDataDir.setValue(configuration.getDataDirectory());
                });
            } catch (IOException e) {
                new ExceptionDialog(this, e);
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(mainFrame);
        setVisible(true);
    }

    private void btnSaveAction(){
        int ret = JOptionPane.showConfirmDialog(
                this,
                "The application will shutdown to apply changes, do you want to continue?",
                "Configuration Save",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if(ret != JOptionPane.OK_OPTION) return;

        new WaitExecution(this, btnSave, () -> {
            ConfigurationDto configuration = configurationRepository.load();
            configuration.setDataDirectory(fcDataDir.getValue());
            configurationRepository.save(configuration);
            System.exit(0);
        }).execute();
    }
}
