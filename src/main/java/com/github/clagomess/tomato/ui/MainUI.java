package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.ui.collection.CollectionNewUI;
import com.github.clagomess.tomato.ui.component.IconFactory;
import com.github.clagomess.tomato.ui.main.collection.CollectionTreeUI;
import com.github.clagomess.tomato.ui.main.request.RequestUI;
import com.github.clagomess.tomato.ui.workspace.WorkspaceSwitchUI;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    private final CollectionTreeUI collectionTreeUi;
    private final RequestUI requestUi;

    public MainUI(){
        this.collectionTreeUi = new CollectionTreeUI();
        this.requestUi = new RequestUI();

        setTitle("Tomato");
        setIconImage(IconFactory.ICON_FAVICON.getImage());
        setVisible(true);
        setMinimumSize(new Dimension(1000, 500));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, collectionTreeUi, requestUi);
        splitPane.setDividerLocation(200);

        add(splitPane);
        pack();
    }

    public JMenuBar getMenu(){
        var mainUI = this;
        JMenuBar menuBar = new JMenuBar();

        // workspace
        menuBar.add(new JMenu("Workspace"){{
            add(new JMenuItem("Switch"){{
                addActionListener(l -> new WorkspaceSwitchUI(mainUI));
            }});
            add(new JMenuItem("New Workspace"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("Edit Workspaces"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
        }});

        // collection
        menuBar.add(new JMenu("Collection"){{
            add(new JMenuItem("Edit Collections"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("New Collection"){{
                addActionListener(l -> new CollectionNewUI(mainUI, null));
            }});
            add(new JMenuItem("Import"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("Export"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
        }});

        // environment
        menuBar.add(new JMenu("Environment"){{
            add(new JMenuItem("Edit Environments"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("Import"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("Export"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
        }});

        // about
        menuBar.add(new JMenu("Settings"){{
            add(new JMenuItem("Configuration"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("About"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
        }});

        return menuBar;
    }
}
