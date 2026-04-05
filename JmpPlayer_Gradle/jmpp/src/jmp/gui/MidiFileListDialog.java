package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import function.Platform;
import function.Utility;
import jmp.core.DataManager;
import jmp.core.FileManager.AutoPlayMode;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SoundManager;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.ui.DropFileCallbackHandler;
import jmp.gui.ui.IDropFileCallback;
import jmp.gui.ui.JMPFrame;
import jmp.lang.DefineLanguage.LangID;
import jmp.util.JmpUtil;

public class MidiFileListDialog extends JMPFrame {

    // private DefaultListModel<String> model;
    // private JList<String> midiFileList;
    private JTable midiFileList;
    private DefaultTableModel model;
    private final JPanel contentPanel = new JPanel();

    private Map<String, File> midiFileMap = null;

    private JList<String> playList;

    // 固有設定
    public static final int COLUMN_TYPE = 0;
    public static final int COLUMN_NAME = 1;

    private static final int COL_SIZE = 25;
    private static final int ROW_SIZE = 20;
    private JLabel lblPath;

    private static final int WINDOW_WIDTH = 870;
    private static final int WINDOW_HEIGHT = 510;

    enum FileFilterType {
        FOLDER, MIDI, WAV, TXT, MUSIC, OTHER, ALL,
    }

    static Map<FileFilterType, Boolean> filterDatabase = new HashMap<FileFilterType, Boolean>() {
        {
            put(FileFilterType.FOLDER, true);
            put(FileFilterType.MIDI, true);
            put(FileFilterType.WAV, true);
            put(FileFilterType.TXT, true);
            put(FileFilterType.MUSIC, true);
            put(FileFilterType.OTHER, false);
        }
    };

    public class FileFilterPanel extends JPanel implements MouseListener {
        public FileFilterType type = FileFilterType.OTHER;

        public FileFilterPanel(FileFilterType type) {
            super();
            this.type = type;
            addMouseListener(this);
        }

        @Override
        public void paint(Graphics g) {
            // super.paint(g);
            if (type == FileFilterType.ALL) {
                g.setColor(Color.BLUE);
                g.fillRect(0, 0, FileFilterPanel.this.getWidth(), FileFilterPanel.this.getHeight());
                g.setColor(Color.GREEN);

                boolean isSelected = true;
                // for (FileFilterType k : filterDatabase.keySet()) {
                // if (filterDatabase.get(k) == false) {
                // isSelected = false;
                // }
                // }

                if (isSelected == true) {
                    g.fillRect(5, 5, FileFilterPanel.this.getWidth() - 10, FileFilterPanel.this.getHeight() - 10);
                }
                g.drawRect(0, 0, FileFilterPanel.this.getWidth() - 1, FileFilterPanel.this.getHeight() - 1);
                return;
            }
            g.setColor(filterDatabase.get(type) == true ? Color.GREEN : getJmpBackColor());
            g.fillRect(0, 0, FileFilterPanel.this.getWidth(), FileFilterPanel.this.getHeight());

            Image folderIcon = JMPCore.getResourceManager().getFileFolderIcon();
            Image midiIcon = JMPCore.getResourceManager().getFileMidiIcon();
            Image wavIcon = JMPCore.getResourceManager().getFileWavIcon();
            Image xmlIcon = JMPCore.getResourceManager().getFileXmlIcon();
            Image musicIcon = JMPCore.getResourceManager().getFileMusicIcon();
            Image otherIcon = JMPCore.getResourceManager().getFileOtherIcon();
            switch (type) {
                case FOLDER:
                    g.drawImage(folderIcon, 0, 0, null);
                    break;
                case MIDI:
                    g.drawImage(midiIcon, 0, 0, null);
                    break;
                case MUSIC:
                    g.drawImage(musicIcon, 0, 0, null);
                    break;
                case WAV:
                    g.drawImage(wavIcon, 0, 0, null);
                    break;
                case TXT:
                    g.drawImage(xmlIcon, 0, 0, null);
                    break;
                case OTHER:
                default:
                    g.drawImage(otherIcon, 0, 0, null);
                    break;

            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (type == FileFilterType.ALL) {
                // boolean isSelected = true;
                // for (FileFilterType k : filterDatabase.keySet()) {
                // if (filterDatabase.get(k) == false) {
                // isSelected = false;
                // }
                // }
                //
                // if (isSelected == true) {
                // for (FileFilterType k : filterDatabase.keySet()) {
                // filterDatabase.put(k, false);
                // }
                // }
                // else {
                // for (FileFilterType k : filterDatabase.keySet()) {
                // filterDatabase.put(k, true);
                // }
                // }

                for (FileFilterType k : filterDatabase.keySet()) {
                    if (k == FileFilterType.OTHER) {
                        continue;
                    }
                    filterDatabase.put(k, true);
                }
            }
            else {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    filterDatabase.put(type, !filterDatabase.get(type));
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    for (FileFilterType t : filterDatabase.keySet()) {
                        filterDatabase.put(t, false);
                    }
                    filterDatabase.put(type, true);
                }
            }
            updateList();
            int selected = midiFileList.getSelectedRow();
            if (0 <= selected && selected < midiFileList.getModel().getRowCount()) {
                String name = (String) midiFileList.getModel().getValueAt(selected, COLUMN_NAME);
                if (midiFileMap.containsKey(name) == true) {
                    File midiFile = midiFileMap.get(name);
                    JMPCore.getSoundManager().syncNextlist(midiFile);
                }
            }
            else {
                JMPCore.getSoundManager().remakeNextlist();
            }
            MidiFileListDialog.this.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    /**
     * Create the dialog.
     */
    public MidiFileListDialog() {
        super();
        setResizable(false);
        setTitle("再生リスト");

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        this.setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                if (Utility.checkExtension(file, SoundManager.PLAYLIST_FILE_EXTENTION) == true) {
                    try {
                        JMPCore.getSoundManager().loadPlayList(file.getPath());
                    }
                    catch (IOException e) {
                    }
                }
                else {
                    updateList(file);
                }
            }
        }));

        model = JMPCore.getFileManager().getFileListModel();
        midiFileList = JMPCore.getFileManager().getFileList();
        midiFileList.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 12));
        midiFileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (e.getClickCount() == 2) {
                    int row = midiFileList.rowAtPoint(p);
                    if (row >= 0) {
                        executeCell(row);
                    }
                }
                else {
                    int row = midiFileList.rowAtPoint(p);
                    if (row >= 0) {
                        updateCellInfo(row);
                    }
                }
            }
        });
        midiFileList.setLocation(462, 0);
        midiFileList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        midiFileList.setForeground(Color.BLACK);
        midiFileList.setBackground(Color.WHITE);
        midiFileList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        setBounds(100, 100, WINDOW_WIDTH, WINDOW_HEIGHT);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBackground(getJmpBackColor());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        {
            scrollPane = new JScrollPane();
            scrollPane.setBounds(5, 55, 391, 374);
            contentPanel.add(scrollPane);
            {
                scrollPane.setViewportView(midiFileList);
            }
        }

        scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(478, 39, 364, 388);
        contentPanel.add(scrollPane_1);

        playList = JMPCore.getSoundManager().getPlayList();
        playList.setForeground(Color.WHITE);
        playList.setBackground(Color.BLACK);
        scrollPane_1.setViewportView(playList);
        btnExproler = new JButton("エクスプローラで開く");
        btnExproler.setBounds(5, 439, 180, 21);
        contentPanel.add(btnExproler);
        {
            addButton = new JButton("追加");
            addButton.setBounds(408, 210, 58, 21);
            contentPanel.add(addButton);
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (midiFileList != null) {
                        int[] selected = midiFileList.getSelectedRows();
                        if (selected.length > 0) {
                            for (int i = 0; i < selected.length; i++) {
                                String name = (String) midiFileList.getModel().getValueAt(selected[i], COLUMN_NAME);
                                if (midiFileMap.containsKey(name) == true) {
                                    File midiFile = midiFileMap.get(name);
                                    DefaultListModel<String> playList = (DefaultListModel<String>) JMPCore.getSoundManager().getPlayList().getModel();
                                    playList.addElement(midiFile.getPath());
                                }
                            }
                        }
                    }
                }
            });
            addButton.setActionCommand("");
            getRootPane().setDefaultButton(addButton);
        }

        btnLoad = new JButton("再生");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = JMPCore.getSoundManager().getPlayList().getSelectedIndex();
                if (index >= 0) {
                    if (JMPCore.getDataManager().isAutoPlay() == false) {
                        JMPCore.getDataManager().setAutoPlay(true);
                    }
                }
                else {
                    JMPCore.getSoundManager().getPlayList().setSelectedIndex(0);
                }

                JMPCore.getSoundManager().playCurrent();
            }
        });
        btnLoad.setBounds(751, 439, 91, 21);
        contentPanel.add(btnLoad);

        buttonClear = new JButton("クリア");
        buttonClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<String> m = (DefaultListModel<String>) playList.getModel();
                m.clear();
            }
        });
        buttonClear.setBounds(545, 439, 91, 21);
        contentPanel.add(buttonClear);

        buttonDelete = new JButton("削除");
        buttonDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JList<String> list = JMPCore.getSoundManager().getPlayList();
                DefaultListModel<String> m = (DefaultListModel<String>) list.getModel();
                int[] selected = list.getSelectedIndices();
                if (selected.length > 0) {
                    for (int i = selected.length - 1; i >= 0; i--) {
                        m.remove(selected[i]);
                    }
                }
            }
        });
        buttonDelete.setBounds(648, 439, 91, 21);
        contentPanel.add(buttonDelete);

        btnDirectLoad = new JButton("開く or 再生");
        btnDirectLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeSelected();
            }
        });
        btnDirectLoad.setBounds(305, 439, 91, 21);
        contentPanel.add(btnDirectLoad);

        lblPath = new JLabel("PATH");
        lblPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openPath();
            }
        });
        lblPath.setBackground(Color.BLACK);
        lblPath.setForeground(Color.WHITE);
        lblPath.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 14));
        lblPath.setBounds(5, 8, 382, 21);
        lblPath.setOpaque(true);
        LineBorder border = new LineBorder(Color.LIGHT_GRAY, 1, false);
        lblPath.setBorder(border);
        contentPanel.add(lblPath);

        JButton btnBack = new JButton("<");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String curPath = JMPCore.getDataManager().getPlayListPath();
                File cur = new File(curPath);

                cur = cur.getParentFile();
                if (cur != null && cur.getPath().equalsIgnoreCase("") == false) {
                    updateList(cur);
                }
                else {
                    openPath();
                }
            }
        });
        btnBack.setBounds(387, 8, 39, 21);
        contentPanel.add(btnBack);

        labelContinuePlayback = new JLabel("連続再生リスト");
        labelContinuePlayback.setForeground(Color.WHITE);
        labelContinuePlayback.setFont(new Font("MS UI Gothic", Font.BOLD, 15));
        labelContinuePlayback.setBounds(478, 16, 252, 21);
        contentPanel.add(labelContinuePlayback);

        JPanel panel = new JmpQuickLaunch();
        panel.setBounds(742, 8, 100, 20);
        contentPanel.add(panel);

        int _ffx = 5;
        int _ffMargin = 22;
        JPanel fileFilterPanel_FOLDER = new FileFilterPanel(FileFilterType.FOLDER);
        fileFilterPanel_FOLDER.setBounds(_ffx, 32, 20, 20);
        contentPanel.add(fileFilterPanel_FOLDER);
        _ffx += _ffMargin;
        JPanel fileFilterPanel_MIDI = new FileFilterPanel(FileFilterType.MIDI);
        fileFilterPanel_MIDI.setBounds(_ffx, 32, 20, 20);
        contentPanel.add(fileFilterPanel_MIDI);
        _ffx += _ffMargin;
        JPanel fileFilterPanel_WAV = new FileFilterPanel(FileFilterType.WAV);
        fileFilterPanel_WAV.setBounds(_ffx, 32, 20, 20);
        contentPanel.add(fileFilterPanel_WAV);
        _ffx += _ffMargin;
        JPanel fileFilterPanel_MUSIC = new FileFilterPanel(FileFilterType.MUSIC);
        fileFilterPanel_MUSIC.setBounds(_ffx, 32, 20, 20);
        contentPanel.add(fileFilterPanel_MUSIC);
        _ffx += _ffMargin;
        JPanel fileFilterPanel_XML = new FileFilterPanel(FileFilterType.TXT);
        fileFilterPanel_XML.setBounds(_ffx, 32, 20, 20);
        contentPanel.add(fileFilterPanel_XML);
        _ffx += _ffMargin;
        JPanel fileFilterPanel_OTHER = new FileFilterPanel(FileFilterType.OTHER);
        fileFilterPanel_OTHER.setBounds(_ffx, 32, 20, 20);
        contentPanel.add(fileFilterPanel_OTHER);
        _ffx += _ffMargin;
        JPanel fileFilterPanel_ALL = new FileFilterPanel(FileFilterType.ALL);
        fileFilterPanel_ALL.setBounds(_ffx, 32, 20, 20);
        contentPanel.add(fileFilterPanel_ALL);

        JButton btnOpenFolder = new JButton("...");
        btnOpenFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPath();
            }
        });
        btnOpenFolder.setBounds(426, 8, 39, 21);
        contentPanel.add(btnOpenFolder);
        _ffx += _ffMargin;

        btnExproler.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path = JMPCore.getDataManager().getPlayListPath();
                try {
                    Utility.openExproler(path);
                }
                catch (IOException e1) {
                }
            }
        });
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground(Color.DARK_GRAY);
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                buttonPane.setLayout(null);
            }
        }

        TableColumn colIco = midiFileList.getColumnModel().getColumn(COLUMN_TYPE);
        colIco.setMaxWidth(COL_SIZE);
        colIco.setMinWidth(COL_SIZE);
        // TableColumn colName =
        // midiFileList.getColumnModel().getColumn(COLUMN_NAME);
        // colName.setMaxWidth(150);
        // colName.setMinWidth(150);

        String listPath = JMPCore.getDataManager().getPlayListPath();
        if ((listPath.isEmpty() == true) || (Utility.isExsistFile(listPath) == false)) {
            updateList(Platform.getCurrentPath());
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            String path = JMPCore.getDataManager().getPlayListPath();
            if (path != null && path.isEmpty() == false) {
                updateList(path);
            }

            updateGUI();
        }
        super.setVisible(b);
    }

    public void updateGUI() {

        if (JMPCore.getDataManager().isAutoPlay() == true) {
            setVisibleExtendGUI(true);
        }
        else {
            setVisibleExtendGUI(false);
        }
    }

    public void setVisibleExtendGUI(boolean visible) {
        if (JMPCore.getFileManager().getAutoPlayMode() != AutoPlayMode.PLAY_LIST) {
            visible = false;
        }

        scrollPane_1.setVisible(visible);
        labelContinuePlayback.setVisible(visible);
        buttonClear.setVisible(visible);
        buttonDelete.setVisible(visible);
        btnLoad.setVisible(visible);
        addButton.setVisible(visible);

        if (visible == true) {
            this.setSize(WINDOW_WIDTH, this.getHeight());
            scrollPane.setSize(391, scrollPane.getHeight());
            btnDirectLoad.setLocation(305, btnDirectLoad.getY());
        }
        else {
            this.setSize(480, this.getHeight());
            scrollPane.setSize(460, scrollPane.getHeight());
            btnDirectLoad.setLocation(374, btnDirectLoad.getY());
        }
    }

    public void updateCellInfo(int row) {
        if (midiFileList != null) {
            if (row >= 0) {
                String name = (String) midiFileList.getModel().getValueAt(row, COLUMN_NAME);
                if (midiFileMap.containsKey(name) == true) {
                    LanguageManager lm = JMPCore.getLanguageManager();
                    File midiFile = midiFileMap.get(name);
                    if (midiFile.isDirectory() == true) {
                        btnDirectLoad.setText(lm.getLanguageStr(LangID.Open));
                    }
                    else {
                        btnDirectLoad.setText(lm.getLanguageStr(LangID.Playback));
                    }
                }
            }
        }
    }

    public void executeCell(int row) {
        if (midiFileList != null) {
            if (row >= 0) {
                String name = (String) midiFileList.getModel().getValueAt(row, COLUMN_NAME);
                if (midiFileMap.containsKey(name) == true) {
                    File midiFile = midiFileMap.get(name);
                    if (midiFile.isDirectory() == true) {
                        if (midiFile.exists() == true) {
                            updateList(midiFile);
                        }
                    }
                    else {
                        SoundManager sm = JMPCore.getSoundManager();
                        if (sm.isSupportedExtensionAccessor(Utility.getExtension(midiFile))) {
                            if (sm.isPlay() == true) {
                                sm.stop();
                            }
                        }

                        JMPCore.getSoundManager().syncNextlist(midiFile);

                        // 自動再生
                        JMPCore.getFileManager().loadFileToPlay(midiFile);
                    }
                }
            }
        }
    }

    public void executeSelected() {
        if (midiFileList != null) {
            int selected = midiFileList.getSelectedRow();
            executeCell(selected);
        }
    }

    private void openPath() {
        String path = JMPCore.getDataManager().getPlayListPath();
        openPath(path);
    }

    private void openPath(String path) {
        JFileChooser filechooser = new JFileChooser();
        // filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (path != null && path.isEmpty() == false) {
            filechooser.setCurrentDirectory(new File(path));
        }

        int selected = filechooser.showOpenDialog(getParent());
        if (selected == JFileChooser.APPROVE_OPTION) {
            File file = filechooser.getSelectedFile();
            if (file.exists() == true && file.canRead() == true) {
                updateList(file);
            }
        }
    }

    private void removeAllRows() {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    public void updateList() {
        File f = new File(JMPCore.getDataManager().getPlayListPath());
        updateList(f);
    }

    public void updateList(String path) {
        File f = new File(path);
        updateList(f);
    }

    public void updateList(File file) {
        if (file.canRead() == false) {
            return;
        }
        if (file.isDirectory() == false) {
            file = file.getParentFile();
            if (file.canRead() == false) {
                return;
            }
        }

        // 現在のフォーカスをバックアップ
        int selectedRow = midiFileList.getSelectedRow();
        String selectedRowName = "";
        if (selectedRow >= 0) {
            selectedRowName = model.getValueAt(selectedRow, 1).toString();
        }

        setCurrentPathText(file.getPath());

        midiFileMap = JMPCore.getFileManager().getFileMap(file);
        removeAllRows();

        SystemManager system = JMPCore.getSystemManager();
        String[] exMIDI = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MIDI));
        String[] exWAV = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_WAV));
        String[] exMUSICXML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSICXML));
        String[] exMUSIC = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MEDIA));
        String[] exMML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MML));

        /* リスト構築 */
        if (midiFileMap != null) {
            // ファイル名ソート
            Object[] keys = midiFileMap.keySet().toArray();
            Arrays.sort(keys);

            List<File> list = new LinkedList<File>();
            // Directory
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isDirectory() == true) {
                    if (midiFileMap.containsKey(sKey) == true) {
                        list.add(f);
                    }
                }
            }
            // MIDI
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exMIDI) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // WAV
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exWAV) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // MusicXML
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exMUSICXML) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // MML
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exMML) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // Music
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (f.isFile() == true) {
                    if (Utility.checkExtensions(sKey, exMUSIC) == true) {
                        if (midiFileMap.containsKey(sKey) == true) {
                            list.add(f);
                        }
                    }
                }
            }
            // Other
            for (Object key : keys) {
                String sKey = key.toString();
                File f = midiFileMap.get(sKey);
                if (list.contains(f) == false) {
                    list.add(f);
                }
            }

            // 除外ケース
            for (int i = list.size() - 1; i >= 0; i--) {
                File f = list.get(i);
                if (f.exists() == false || f.canRead() == false || f.isHidden() == true) {
                    list.remove(i);
                }
            }

            ImageIcon folderIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileFolderIcon());
            ImageIcon midiIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileMidiIcon());
            ImageIcon wavIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileWavIcon());
            ImageIcon xmlIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileXmlIcon());
            ImageIcon musicIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileMusicIcon());
            ImageIcon otherIcon = JmpUtil.convertImageIcon(JMPCore.getResourceManager().getFileOtherIcon());

            for (File f : list) {
                String name = "";
                for (String key : midiFileMap.keySet()) {
                    if (f == midiFileMap.get(key)) {
                        /* 合致するキーを表示する */
                        name = key;
                        break;
                    }
                }

                if (f.isDirectory() == true) {
                    if (filterDatabase.get(FileFilterType.FOLDER) == true) {
                        Object[] row = createFileListRows(folderIcon, "DI", name);
                        model.addRow(row);
                    }
                }
                else {
                    if (Utility.checkExtensions(name, exMIDI) == true) {
                        if (filterDatabase.get(FileFilterType.MIDI) == true) {
                            Object[] row = createFileListRows(midiIcon, "MI", name);
                            model.addRow(row);
                        }
                    }
                    else if (Utility.checkExtensions(name, exWAV) == true) {
                        if (filterDatabase.get(FileFilterType.WAV) == true) {
                            Object[] row = createFileListRows(wavIcon, "WA", name);
                            model.addRow(row);
                        }
                    }
                    else if (Utility.checkExtensions(name, exMUSICXML) == true) {
                        if (filterDatabase.get(FileFilterType.TXT) == true) {
                            Object[] row = createFileListRows(xmlIcon, "XM", name);
                            model.addRow(row);
                        }
                    }
                    else if (Utility.checkExtensions(name, exMML) == true) {
                        if (filterDatabase.get(FileFilterType.TXT) == true) {
                            Object[] row = createFileListRows(xmlIcon, "MM", name);
                            model.addRow(row);
                        }
                    }
                    else if (Utility.checkExtensions(name, exMUSIC) == true) {
                        if (filterDatabase.get(FileFilterType.MUSIC) == true) {
                            Object[] row = createFileListRows(musicIcon, "MU", name);
                            model.addRow(row);
                        }
                    }
                    else {
                        if (filterDatabase.get(FileFilterType.OTHER) == true) {
                            Object[] row = createFileListRows(otherIcon, "#", name);
                            model.addRow(row);
                        }
                    }
                }
            }

            midiFileList.setRowHeight(ROW_SIZE);
        }

        // フォーカスを復元
        if (selectedRow >= 0) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String s = model.getValueAt(i, 1).toString();
                if (s.equals(selectedRowName) == true) {
                    midiFileList.changeSelection(i, 1, false, false);
                    break;
                }
            }
        }
        JMPCore.getDataManager().setPlayListPath(file.getPath());
    }

    private Object[] createFileListRows(ImageIcon icon, String ex, String fileName) {
        if (icon != null) {
            Object[] row = { icon, fileName };
            return row;
        }
        else {
            Object[] row = { ex, fileName };
            return row;
        }
    }

    public void loadPlayList(String path) {
        if (path.endsWith(".jmpl") == true) {
            BufferedReader reader;
            String line = "";
            String result = "";

            File file = new File(path);
            if (file.isFile() == false) {
                return;
            }
            if (file.exists() == false) {
                return;
            }

            DefaultListModel<String> m = (DefaultListModel<String>) playList.getModel();
            m.removeAllElements();

            try {
                FileInputStream fs = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
                reader = new BufferedReader(isr);

                // ファイルを読み込む
                while ((line = reader.readLine()) != null) {
                    result = line.trim();
                    m.addElement(result);
                }

                reader.close();
            }
            catch (Exception e) {
            }
            finally {
                reader = null;
            }
        }
    }

    private static final int NODE_DISPLAY_LEVEL = 3;
    private static final String NODE_COLOR = "white";
    private static final String NODE_COLOR_CUR = "yellow";
    private static final String SEP_COLOR = "gray";
    private JLabel labelContinuePlayback;
    private JButton addButton;
    private JButton btnExproler;
    private JButton btnDirectLoad;
    private JButton buttonClear;
    private JButton buttonDelete;
    private JButton btnLoad;
    private JScrollPane scrollPane_1;
    private JScrollPane scrollPane;

    protected void setCurrentPathText(String path) {
        File file = new File(path);

        final String htmlFormat = "<html>%s</html>";
        final String fontFormat = "<font color=%s>%s</font>";

        String text = "";
        String[] nodes = Utility.getFileNodeNames(file);
        int start = nodes.length - NODE_DISPLAY_LEVEL;
        if (start < 0) {
            start = 0;
        }
        for (int i = start; i < nodes.length; i++) {
            if (i > 0) {
                text += String.format(fontFormat, SEP_COLOR, ">");
            }

            String nodeColor = NODE_COLOR;
            if (i == (nodes.length - 1)) {
                nodeColor = NODE_COLOR_CUR;
            }
            String node = nodes[i];
            if (node.equals("") == true) {
                node = String.format(fontFormat, nodeColor, "~");
            }
            text += String.format(fontFormat, nodeColor, node);
        }
        lblPath.setText(String.format(htmlFormat, text));
    }

    @Override
    public void updateLanguage() {
        LanguageManager lm = JMPCore.getLanguageManager();
        WindowManager wm = JMPCore.getWindowManager();

        super.updateLanguage();
        setTitle(lm.getLanguageStr(LangID.Playlist));
        setFont(wm.getCurrentFont(getFont()));
        wm.changeFont(midiFileList);
        wm.changeFont(labelContinuePlayback, LangID.Continuous_playback);
        wm.changeFont(btnDirectLoad, LangID.Playback);
        wm.changeFont(btnExproler, LangID.Open_with_Explorer);
        wm.changeFont(addButton, LangID.Add);
        wm.changeFont(buttonClear, LangID.Clear);
        wm.changeFont(buttonDelete, LangID.Remove);
        wm.changeFont(btnLoad, LangID.Continuous_playback);

    }

    @Override
    public void updateConfig(String key) {
        if (key.equalsIgnoreCase(DataManager.CFG_KEY_AUTOPLAY) == true) {
            updateGUI();
        }
    }
}
