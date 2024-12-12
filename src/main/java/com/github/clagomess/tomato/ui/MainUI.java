package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.ui.collection.CollectionNewUI;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImageIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSliderAltIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTransferAltIcon;
import com.github.clagomess.tomato.ui.main.collection.CollectionTreeUI;
import com.github.clagomess.tomato.ui.main.request.RequestTabPaneUI;
import com.github.clagomess.tomato.ui.workspace.WorkspaceNewUI;
import com.github.clagomess.tomato.ui.workspace.WorkspaceSwitchUI;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    public MainUI(){
        setTitle("Tomato");
        setIconImage(new FaviconImageIcon().getImage());
        setVisible(true);
        setMinimumSize(new Dimension(1200, 650));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new CollectionTreeUI(),
                new RequestTabPaneUI()
        );
        splitPane.setDividerLocation(250);

        add(splitPane);
        pack();
    }

    public JMenuBar getMenu(){
        var mainUI = this;
        JMenuBar menuBar = new JMenuBar();

        // workspace
        menuBar.add(new JMenu("Workspace"){{
            add(new JMenuItem("Switch", new BxTransferAltIcon()){{
                addActionListener(l -> new WorkspaceSwitchUI(mainUI));
            }});
            add(new JMenuItem("New Workspace"){{
                addActionListener(l -> new WorkspaceNewUI(mainUI));
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
            add(new JMenuItem("Import", new BxImportIcon()){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("Export", new BxExportIcon()){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
        }});

        // environment
        menuBar.add(new JMenu("Environment"){{
            add(new JMenuItem("Edit Environments"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("Import", new BxImportIcon()){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("Export", new BxExportIcon()){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
        }});

        // about
        menuBar.add(new JMenu("Settings"){{
            add(new JMenuItem("Configuration", new BxSliderAltIcon()){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
            add(new JMenuItem("About"){{
                addActionListener(l -> {}); //@TODO: Implements
            }});
        }});

        return menuBar;
    }
}
