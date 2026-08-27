package io.github.clagomess.tomato.ui.request;

import io.github.clagomess.tomato.controller.request.RequestMoveFrameController;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.ui.BaseFrame;
import io.github.clagomess.tomato.ui.collection.CollectionComboBox;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RequestMoveFrame extends BaseFrame {
    private final JButton btnMove = new JButton("Move");
    private final JLabel lblRequestName = new JLabel();
    private final CollectionComboBox cbCollectionDestination;
    private final RequestHeadDto requestHead;

    private final RequestMoveFrameController controller = new RequestMoveFrameController();

    public RequestMoveFrame(
            Component parent,
            RequestHeadDto requestHead
    ){
        setTitle("Move Request");
        setMinimumSize(new Dimension(300, 100));
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
            controller.moveRequest(
                    requestHead,
                    cbCollectionDestination.getSelectedItem()
            );
            setVisible(false);
            dispose();
        }).execute();
    }
}
