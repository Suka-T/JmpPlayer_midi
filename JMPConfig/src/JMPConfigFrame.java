import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import jlib.core.JMPCoreAccessor;
import jlib.gui.IJmpWindow;

public class JMPConfigFrame extends JFrame {

    public static final Color COLOR_NOTE_ON = Color.GREEN;
    public static final Color COLOR_NOTE_OFF = Color.BLACK;

    public static final String TYPE_CONFIG = "CFG";
    public static final String TYPE_SYSTEM = "SYS";
    public static final String TYPE_WINDOW = "WIN";

    private JPanel contentPane;
    private JTable table;
    private JPanel panel;
    private JButton btnApply;
    private JButton btnUpdate;
    private DefaultTableModel model;
    private JLabel label;
    private JLabel label_1;
    private JLabel label_2;
    private JLabel label_3;
    private JLabel label_4;

    private JLabel[] noteMonitor = null;
    private JPanel panel_1;
    private JLabel label_5;
    private JLabel label_6;
    private JLabel label_7;
    private JLabel label_8;
    private JLabel label_9;
    private JLabel label_10;
    private JLabel label_11;
    private JButton btnPlayStop;

    private Color[] chColor;

    /**
     * Create the frame.
     */
    public JMPConfigFrame() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        setBounds(100, 100, 569, 395);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String type = model.getValueAt(row, 2).toString();
                if (isSelected == true) {
                    // 選択行
                }
                else {
                    if (type.equals(TYPE_CONFIG) == true) {
                        setBackground(Color.GREEN);
                    }
                    else if (type.equals(TYPE_WINDOW) == true) {
                        setBackground(Color.ORANGE);
                    }
                    else {
                        setBackground(Color.LIGHT_GRAY);
                    }
                }
                return this;
            }
        });
        model.addColumn("Key");
        model.addColumn("Value");
        model.addColumn("Type");
        scrollPane.setViewportView(table);

        panel = new JPanel();
        contentPane.add(panel, BorderLayout.SOUTH);

        btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });
        panel.add(btnUpdate);

        btnApply = new JButton("Apply");
        btnApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyTable();
            }
        });
        panel.add(btnApply);

        panel_1 = new JPanel();
        contentPane.add(panel_1, BorderLayout.NORTH);

        label = new JLabel("■");
        panel_1.add(label);

        label_1 = new JLabel("■");
        panel_1.add(label_1);

        label_2 = new JLabel("■");
        panel_1.add(label_2);

        label_3 = new JLabel("■");
        panel_1.add(label_3);

        label_4 = new JLabel("■");
        panel_1.add(label_4);

        label_5 = new JLabel("■");
        panel_1.add(label_5);

        label_6 = new JLabel("■");
        panel_1.add(label_6);

        label_7 = new JLabel("■");
        panel_1.add(label_7);

        label_8 = new JLabel("■");
        panel_1.add(label_8);

        label_9 = new JLabel("■");
        panel_1.add(label_9);

        label_10 = new JLabel("■");
        panel_1.add(label_10);

        label_11 = new JLabel("■");
        panel_1.add(label_11);

        btnPlayStop = new JButton("Play/Stop");
        btnPlayStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionPlayStop();
            }
        });
        panel_1.add(btnPlayStop);

        noteMonitor = new JLabel[] { label, label_1, label_2, label_3, label_4, label_5, label_6, label_7, label_8, label_9, label_10, label_11, };

        String ckKeyFormat = "ch_color_%d";
        chColor = new Color[16];
        for (int ch = 0; ch < chColor.length; ch++) {
            String key = String.format(ckKeyFormat, (ch + 1));
            String value = JMPCoreAccessor.getSystemManager().getCommonRegisterValue(key);
            if (value.isEmpty() == false) {
                chColor[ch] = JMPCoreAccessor.getSystemManager().getUtilityToolkit().convertCodeToHtmlColor(value);
            }
            else {
                chColor[ch] = COLOR_NOTE_ON;
            }
        }

        for (JLabel lb : noteMonitor) {
            lb.setForeground(COLOR_NOTE_OFF);
        }

        setTransferHandler(new TransferHandler() {
            /**
             * ドロップされたものを受け取るか判断 (アイテムのときだけ受け取る)
             */
            @Override
            public boolean canImport(TransferSupport support) {
                if (support.isDrop() == false) {
                    // ドロップ操作でない場合は受け取らない
                    return false;
                }

                if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == false) {
                    // ファイルでない場合は受け取らない
                    return false;
                }

                return true;
            }

            /**
             * ドロップされたアイテムを受け取る
             */
            @Override
            public boolean importData(TransferSupport support) {
                // ドロップアイテム受理の確認
                if (canImport(support) == false) {
                    return false;
                }

                // ドロップ処理
                Transferable t = support.getTransferable();
                try {
                    // ドロップアイテム取得
                    Object item = t.getTransferData(DataFlavor.javaFileListFlavor);

                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) item;

                    // 一番先頭のファイルを取得
                    if ((files != null) && (files.size() > 0)) {
                        String path = files.get(0).getPath();
                        File file = new File(path);
                        actionFileDrop(file);
                    }
                    return true;
                }
                catch (Exception e) {
                    /* 受け取らない */
                }
                return false;
            }
        });
    }

    private void actionFileDrop(File file) {
        /*
         * ファイルをロードする
         */
        JMPCoreAccessor.getFileManager().loadFile(file);
    }

    private void actionPlayStop() {
        /*
         * 再生/停止を操作する。
         */
        JMPCoreAccessor.getSoundManager().togglePlayStop();
    }

    private void applyTable() {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            String key = model.getValueAt(i, 0).toString();
            String val = model.getValueAt(i, 1).toString();
            String type = model.getValueAt(i, 2).toString();

            if (type.equals(TYPE_CONFIG) == true) {
                /*
                 * Configパラメータを設定する。
                 */
                JMPCoreAccessor.getDataManager().setConfigParam(key, val);
            }
            else if (type.equals(TYPE_WINDOW) == true) {
                /*
                 * Windowのステータス(SHOW/HIDE)を設定する。
                 */
                IJmpWindow win = JMPCoreAccessor.getWindowManager().getWindow(key);
                if (val.equalsIgnoreCase("SHOW") == true) {
                    if (win.isWindowVisible() == false) {
                        win.showWindow();
                    }
                }
                else {
                    if (win.isWindowVisible() == true) {
                        win.hideWindow();
                    }
                }
            }
            else if (type.equals(TYPE_SYSTEM) == true) {
                /*
                 * System共通レジスタ値を取得する。
                 */
                JMPCoreAccessor.getSystemManager().setCommonRegisterValue(key, val);
            }
        }
    }

    private void updateTable() {
        /* テーブルアイテムを更新 */
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        /*
         * Configパラメータを全て取得する。
         */
        for (String key : JMPCoreAccessor.getDataManager().getKeySet()) {
            Object[] rows = { key, JMPCoreAccessor.getDataManager().getConfigParam(key), TYPE_CONFIG };
            model.addRow(rows);
        }
        /*
         * Windowのステータス(SHOW/HIDE)を取得する。
         */
        for (String key : JMPCoreAccessor.getWindowManager().getWindowNameList()) {
            IJmpWindow win = JMPCoreAccessor.getWindowManager().getWindow(key);
            if (win != null) {
                Object[] rows = { key, win.isWindowVisible() ? "SHOW" : "HIDE", TYPE_WINDOW };
                model.addRow(rows);
            }
        }
        /*
         * System共通レジスタ値を取得する。
         */
        for (String key : JMPCoreAccessor.getSystemManager().getCommonKeySet()) {
            Object[] rows = { key, JMPCoreAccessor.getSystemManager().getCommonRegisterValue(key), TYPE_SYSTEM };
            model.addRow(rows);
        }
    }

    public void setNoteOn(int channel, int midiNumber) {
        if (0 <= channel && channel < noteMonitor.length) {
            int index = midiNumber % 12;
            if (0 <= index && index < 12) {
                noteMonitor[index].setForeground(chColor[channel]);
            }
        }
    }

    public void setNoteOff(int channel, int midiNumber) {
        if (0 <= channel && channel < noteMonitor.length) {
            int index = midiNumber % 12;
            if (0 <= index && index < 12) {
                noteMonitor[index].setForeground(COLOR_NOTE_OFF);
            }
        }
    }

}
