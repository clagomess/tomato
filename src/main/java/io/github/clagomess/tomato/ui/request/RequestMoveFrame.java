package io.github.clagomess.tomato.ui.request;

import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.repository.RequestRepository;
import io.github.clagomess.tomato.mapper.CloneMapper;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.base.PublisherEvent;
import io.github.clagomess.tomato.publisher.key.RequestKey;
import io.github.clagomess.tomato.ui.collection.CollectionComboBox;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.DELETED;
import static io.github.clagomess.tomato.publisher.base.EventTypeEnum.INSERTED;

public class RequestMoveFrame extends JFrame {
    private final JButton btnMove = new JButton("Move");
    private final JLabel lblRequestName = new JLabel();
    private final CollectionComboBox cbCollectionDestination;
    private final RequestHeadDto requestHead;

    private final CloneMapper cloneMapper = CloneMapper.INSTANCE;
    private final RequestRepository requestRepository = new RequestRepository();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    public RequestMoveFrame(
            Component parent,
            RequestHeadDto requestHead
    ){
        setTitle("Move Request");
        setIconImages(FaviconImage.getFrameIconImage());
        setMinimumSize(new Dimension(300, 100));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
            if(destination == null) throw new TomatoException("Destination not selected");

            requestRepository.move(requestHead, destination);

            // update source collection
            requestPublisher.getOnChange().publish(
                    new RequestKey(requestHead),
                    new PublisherEvent<>(DELETED, requestHead)
            );

            // update target collection
            var newRequestHead = cloneMapper.clone(this.requestHead);
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
