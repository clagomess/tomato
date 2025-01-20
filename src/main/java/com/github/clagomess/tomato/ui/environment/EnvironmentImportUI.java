package com.github.clagomess.tomato.ui.environment;

import com.github.clagomess.tomato.io.converter.PostmanConverter;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.ui.component.FileChooser;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EnvironmentImportUI extends JFrame {
    private final JButton btnImport = new JButton("Import");
    private final FileChooser fileChooser = new FileChooser();
    private final JComboBox<String> cbType = new JComboBox<>(new String[]{
            "Postman Environment",
    });

    private final PostmanConverter postmanConverter = new PostmanConverter();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public EnvironmentImportUI(
            Component parent
    ){
        setTitle("Import Environment");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        panel.add(new JLabel("File"), "wrap");
        panel.add(fileChooser, "width 300!, wrap");
        panel.add(new JLabel("Type"), "wrap");
        panel.add(cbType, "width 300!, wrap");
        panel.add(btnImport, "align right");
        add(panel);

        getRootPane().setDefaultButton(btnImport);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnImport.addActionListener(l -> btnImportAction());
    }

    private void btnImportAction(){
        new WaitExecution(this, btnImport, () -> {
            var id = postmanConverter.pumpEnvironment(fileChooser.getValue());

            environmentPublisher.getOnInsert().publish(id);

            setVisible(false);
            dispose();
        }).execute();
    }
}
