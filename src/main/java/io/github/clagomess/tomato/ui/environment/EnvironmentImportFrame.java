package io.github.clagomess.tomato.ui.environment;

import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.converter.InterfaceConverter;
import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.ui.component.ConverterComboBox;
import io.github.clagomess.tomato.ui.component.FileChooser;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class EnvironmentImportFrame extends JFrame {
    private final JButton btnImport = new JButton("Import");
    private final FileChooser fileChooser = new FileChooser();
    private final ConverterComboBox cbConverter = new ConverterComboBox();

    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    public EnvironmentImportFrame(
            Component parent
    ){
        setTitle("Import Environment");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("File"), "wrap");
        add(fileChooser, "width 300!, wrap");
        add(new JLabel("Type"), "wrap");
        add(cbConverter, "width 300!, wrap");
        add(btnImport, "align right");

        getRootPane().setDefaultButton(btnImport);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnImport.addActionListener(l -> btnImportAction());
    }

    private void btnImportAction(){
        new WaitExecution(this, btnImport, () -> {
            InterfaceConverter converter = cbConverter.getSelectedItem();
            if(converter == null) throw new TomatoException("Type is empty");

            var id = converter.pumpEnvironment(fileChooser.getValue());

            environmentPublisher.getOnChange()
                    .publish(new PublisherEvent<>(INSERTED, id));

            setVisible(false);
            dispose();
        }).execute();
    }
}
