package com.github.clagomess.tomato.ui.request;

import com.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.collection.CollectionComboBox;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;
import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

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

        setLayout(new MigLayout(
                "insets 10",
                "[grow]"
        ));
        add(new JLabel("Request"), "wrap");
        add(lblRequestName, "width 300!, wrap");
        add(new JLabel("Parent"), "wrap");
        add(cbCollectionDestination, "width 300!, wrap");
        add(btnMove, "align right");

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
            requestPublisher.getOnChange().publish(
                    new RequestKey(requestHead),
                    new PublisherEvent<>(DELETED, requestHead)
            );

            // update target collection
            var newRequestHead = RequestMapper.INSTANCE.clone(this.requestHead);
            newRequestHead.setParent(destination);
            newRequestHead.setPath(new File(
                    destination.getPath(),
                    this.requestHead.getPath().getName()
            ));

            requestPublisher.getOnChange().publish(
                    new RequestKey(newRequestHead),
                    new PublisherEvent<>(INSERTED, newRequestHead)
            );

            setVisible(false);
            dispose();
        }).execute();
    }
}
