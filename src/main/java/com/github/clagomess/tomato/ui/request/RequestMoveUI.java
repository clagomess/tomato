package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.collection.CollectionComboBox;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class RequestMoveUI extends JFrame {
    private final JButton btnMove = new JButton("Move");
    private final JLabel lblRequestName = new JLabel();
    private final CollectionComboBox cbCollectionDestination;
    private final RequestHeadDto requestHead;

    private final RequestRepository requestRepository = new RequestRepository();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestMoveUI(
            Component parent,
            RequestHeadDto requestHead
    ){
        setTitle("Move Request");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        this.requestHead = requestHead;

        lblRequestName.setText(requestHead.getName());
        cbCollectionDestination = new CollectionComboBox(requestHead.getParent());

        JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        panel.add(new JLabel("Request"), "wrap");
        panel.add(lblRequestName, "width 300!, wrap");
        panel.add(new JLabel("Parent"), "wrap");
        panel.add(cbCollectionDestination, "width 300!, wrap");
        panel.add(btnMove, "align right");
        add(panel);

        getRootPane().setDefaultButton(btnMove);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        // set data
        btnMove.addActionListener(l -> btnMoveAction());
    }

    private void btnMoveAction(){
        new WaitExecution(this, () -> {
            CollectionTreeDto destination = cbCollectionDestination.getSelectedItem();
            if(destination == null) throw new Exception("Destination not selected");

            requestRepository.move(requestHead, destination);

            // update source collection
            var key = new RequestPublisher.RequestKey(
                    requestHead.getParent().getId(),
                    requestHead.getId()
            );

            requestPublisher.getOnDelete().publish(
                    key,
                    new RequestPublisher.RequestId(requestHead.getId())
            );

            // update target collection
            this.requestHead.setParent(destination);
            this.requestHead.setPath(new File(
                    destination.getPath(),
                    this.requestHead.getPath().getName()
            ));

            requestPublisher.getOnInsert().publish(
                    new RequestPublisher.ParentCollectionId(destination.getId()),
                    requestHead
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
