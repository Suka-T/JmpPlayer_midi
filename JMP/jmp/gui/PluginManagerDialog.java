package jmp.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import function.Utility;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.PluginManager;
import jmp.core.SoundManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPFrame;
import jmp.lang.DefineLanguage.LangID;
import jmp.plugin.PluginWrapper;
import jmp.plugin.PluginWrapper.PluginState;

public class PluginManagerDialog extends JMPFrame {
    private static final String[] columnNames = new String[] { "Name", "Opened", "State" };

    private DefaultTableModel model;
    private JTable table;
    private JPanel panel;
    private JButton btnValid;
    private JButton btnInvalid;

    /**
     * Create the frame.
     */
    public PluginManagerDialog() {
        super();
        setTitle("Plugin manager");
        setBounds(100, 100, 616, 375);
        setJmpIcon();

        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        scrollPane.setViewportView(table);

        panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeState(PluginState.CONNECTED);
            }
        });
        panel.add(btnConnect);

        JButton btnDisconnect = new JButton("Disconnect");
        btnDisconnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeState(PluginState.DISCONNECTED);
            }
        });
        panel.add(btnDisconnect);

        JButton btnEnable = new JButton("Open");
        btnEnable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                if (selected == null || selected.length <= 0) {
                    return;
                }

                PluginManager pm = JMPCore.getPluginManager();
                for (int i = 0; i < selected.length; i++) {
                    String name = model.getValueAt(selected[i], 0).toString();
                    PluginWrapper plg = pm.getPluginWrapper(name);
                    if (plg != null) {
                        if (plg.isEnable() == true) {
                            plg.open();
                        }
                    }
                }

                updateTable();
            }
        });

        btnInvalid = new JButton("Invalid");
        btnInvalid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                if (selected == null || selected.length <= 0) {
                    return;
                }

                PluginManager pm = JMPCore.getPluginManager();
                for (int i = 0; i < selected.length; i++) {
                    String name = model.getValueAt(selected[i], 0).toString();
                    if (pm.getPluginState(name) != PluginState.INVALID) {
                        pm.toInvalidPlugin(name);
                    }
                }

                updateTable();
            }
        });

        btnValid = new JButton("Valid");
        btnValid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                if (selected == null || selected.length <= 0) {
                    return;
                }

                PluginManager pm = JMPCore.getPluginManager();
                for (int i = 0; i < selected.length; i++) {
                    String name = model.getValueAt(selected[i], 0).toString();
                    if (pm.getPluginState(name) == PluginState.INVALID) {
                        pm.toValidPlugin(name);
                    }
                }

                updateTable();
            }
        });
        panel.add(btnValid);
        panel.add(btnInvalid);
        panel.add(btnEnable);

        JButton btnDisable = new JButton("Close");
        btnDisable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = table.getSelectedRows();
                if (selected == null || selected.length <= 0) {
                    return;
                }

                PluginManager pm = JMPCore.getPluginManager();
                for (int i = 0; i < selected.length; i++) {
                    String name = model.getValueAt(selected[i], 0).toString();
                    PluginWrapper plg = pm.getPluginWrapper(name);
                    if (plg != null) {
                        plg.close();
                    }
                }

                updateTable();
            }
        });
        panel.add(btnDisable);

        JButton btnReflesh = new JButton("Reflesh");
        btnReflesh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTable(true);
            }
        });
        panel.add(btnReflesh);

        TableColumn col2 = table.getColumnModel().getColumn(1);
        col2.setMaxWidth(80);
        col2.setMinWidth(80);
        TableColumn col3 = table.getColumnModel().getColumn(2);
        col3.setMaxWidth(80);
        col3.setMinWidth(80);

        updateBackColor();
    }

    public void updateTable(boolean clear) {
        PluginManager pm = JMPCore.getPluginManager();
        if (clear == true) {
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                model.removeRow(i);
            }
            for (String name : pm.getPluginsNameSet()) {
                String[] row = new String[model.getColumnCount()];
                row[0] = name;
                model.addRow(row);
            }
        }
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            String name = model.getValueAt(i, 0).toString();
            PluginWrapper plg = pm.getPluginWrapper(name);
            String visibleState = plg.isOpen() == true ? "Open" : "Close";
            String pluginState = pm.getPluginState(name).toString();
            if (pm.getPluginState(name) == PluginState.INVALID) {
                visibleState = "----";
            }

            model.setValueAt(visibleState, i, 1);
            model.setValueAt(pluginState.toLowerCase(), i, 2);
        }
    }

    public void updateTable() {
        updateTable(false);
    }

    public void changeState(PluginState state) {
        int[] selected = table.getSelectedRows();
        if (selected == null || selected.length <= 0) {
            return;
        }

        PluginManager pm = JMPCore.getPluginManager();
        for (int i = 0; i < selected.length; i++) {
            String name = model.getValueAt(selected[i], 0).toString();
            if (pm.getPluginState(name) != PluginState.INVALID) {
                pm.setPluginState(name, state);
            }
        }

        updateTable();
    }

    @Override
    public void showWindow() {
        updateTable(true);
        updateEnableControll();
        super.showWindow();
    }

    @Override
    public void updateBackColor() {
        super.updateBackColor();
        getContentPane().setBackground(getJmpBackColor());
        panel.setBackground(getJmpBackColor());
        table.getParent().setBackground(Utility.convertHighLightColor(getJmpBackColor(), 100));
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();
        LanguageManager lm = JMPCore.getLanguageManager();
        WindowManager wm = JMPCore.getWindowManager();
        setTitle(lm.getLanguageStr(LangID.Plugin_manager));
        setFont(wm.getCurrentFont(getFont()));
    }

    private void updateEnableControll() {
        SoundManager sm = JMPCore.getSoundManager();
        btnValid.setEnabled(!sm.isPlay());
        btnInvalid.setEnabled(!sm.isPlay());
    }

    @Override
    public void processingAfterPlay() {
        super.processingAfterPlay();

        updateEnableControll();
    }

    @Override
    public void processingAfterStop() {
        super.processingAfterStop();

        updateEnableControll();
    }

}
