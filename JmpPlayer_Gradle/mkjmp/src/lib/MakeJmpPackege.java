package lib;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Platform;
import function.Utility;
import jlib.JMPLIB;

public class MakeJmpPackege extends JFrame {

    private JPanel contentPane;
    private JTextField textFieldJarPath;
    private JTextField textFieldDataPath;
    private JTextField textFieldPluginName;
    private JPanel panel;
    private JTextField textFieldOutput;
    private JTextField textFieldResPath;
    private JCheckBox chckbxAddBlankData;
    private JButton btnRead;
    private JTextField textFieldCompVersion;
    private JTextField textField_vname;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        setupLookAndFeel();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MakeJmpLib.call(args);
            }
        });
    }

    /**
     * ルックアンドフィールの設定
     */
    private static void setupLookAndFeel() {
        try {
            String lf = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lf);
        }
        catch (Exception e) {
            System.out.println("lferror");

            // 念のためMetalを再設定
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
            catch (Exception e2) {
            }
        }
    }

    /**
     * Create the frame.
     */
    public MakeJmpPackege(String jar, String data, String res, String out, boolean appExitFlag, boolean showExploler, String version, String versionName) {
        setTitle("mkJMP");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (appExitFlag == false) {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
        setBounds(100, 100, 616, 405);
        setTransferHandler(new DropFileHandler(new mkjDEvent()));
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        panel = new JPanel();
        panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "JAR", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panel.setBounds(20, 10, 564, 105);
        contentPane.add(panel);
        panel.setLayout(null);

        textFieldJarPath = new JTextField();
        textFieldJarPath.setBounds(84, 15, 468, 19);
        panel.add(textFieldJarPath);
        textFieldJarPath.setColumns(10);

        textFieldPluginName = new JTextField();
        textFieldPluginName.setEnabled(false);
        textFieldPluginName.setBounds(357, 44, 195, 19);
        panel.add(textFieldPluginName);
        textFieldPluginName.setColumns(10);

        JButton btnReadJar = new JButton("Read");
        btnReadJar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar Files(*.jar)", "jar");
                filechooser.setFileFilter(filter);

                File dir = new File(Platform.getCurrentPath());
                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            // ファイルロード
                            loadFileJar(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        btnReadJar.setBounds(461, 74, 91, 21);
        panel.add(btnReadJar);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(
                new TitledBorder(UIManager.getBorder("TitledBorder.border"), "DATA", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panel_1.setBounds(20, 125, 564, 80);
        contentPane.add(panel_1);
        panel_1.setLayout(null);

        textFieldDataPath = new JTextField();
        textFieldDataPath.setBounds(84, 15, 468, 19);
        panel_1.add(textFieldDataPath);
        textFieldDataPath.setColumns(10);

        JButton buttonData = new JButton("Read");
        buttonData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();

                File dir = new File(Platform.getCurrentPath());
                filechooser.setCurrentDirectory(dir);
                filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getParent();
                        if (file.isDirectory() == false) {
                            // ファイルロード
                            loadFileData(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        buttonData.setBounds(461, 44, 91, 21);
        panel_1.add(buttonData);

        JButton btnExport = new JButton("Export");
        btnExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                export();

                if (showExploler == true) {
                    try {
                        Utility.openExproler(textFieldOutput.getText());
                    }
                    catch (IOException e1) {
                    }
                }
            }
        });
        btnExport.setBounds(493, 345, 91, 21);
        contentPane.add(btnExport);

        textFieldOutput = new JTextField();
        textFieldOutput.setBounds(20, 305, 564, 19);
        contentPane.add(textFieldOutput);
        textFieldOutput.setColumns(10);

        JPanel panel_2 = new JPanel();
        panel_2.setLayout(null);
        panel_2.setBorder(
                new TitledBorder(UIManager.getBorder("TitledBorder.border"), "RES", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panel_2.setBounds(20, 215, 564, 80);
        contentPane.add(panel_2);

        textFieldResPath = new JTextField();
        textFieldResPath.setColumns(10);
        textFieldResPath.setBounds(84, 15, 468, 19);
        panel_2.add(textFieldResPath);

        JButton buttonRes = new JButton("Read");
        buttonRes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();

                File dir = new File(Platform.getCurrentPath());
                filechooser.setCurrentDirectory(dir);
                filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getParent();
                        if (file.isDirectory() == false) {
                            // ファイルロード
                            loadFileRes(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        buttonRes.setBounds(461, 44, 91, 21);
        panel_2.add(buttonRes);

        textFieldJarPath.setTransferHandler(new DropFileHandler(new JarDEvent()));

        textFieldCompVersion = new JTextField();
        textFieldCompVersion.setBounds(138, 44, 96, 19);
        panel.add(textFieldCompVersion);
        textFieldCompVersion.setColumns(10);

        JLabel lblVersion = new JLabel("Version");
        lblVersion.setBounds(94, 50, 50, 13);
        panel.add(lblVersion);
        textFieldDataPath.setTransferHandler(new DropFileHandler(new DataDEvent()));

        chckbxAddBlankData = new JCheckBox("Add empty DATA Folder");
        chckbxAddBlankData.setBounds(84, 40, 216, 21);
        panel_1.add(chckbxAddBlankData);
        chckbxAddBlankData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textFieldDataPath.setEnabled(!chckbxAddBlankData.isSelected());
            }
        });
        textFieldResPath.setTransferHandler(new DropFileHandler(new ResDEvent()));
        textFieldOutput.setTransferHandler(new DropFileHandler(new OutputDEvent()));

        JButton btnWrite = new JButton(MakeJmpLib.PKG_PROJECT_CFG_EX.toUpperCase() + " Write");
        btnWrite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = textFieldPluginName.getText();
                String path = Platform.getCurrentPath(true) + name + "." + MakeJmpLib.PKG_PROJECT_CFG_EX;
                saveFileMkj(path);
            }
        });
        btnWrite.setBounds(287, 345, 91, 21);
        contentPane.add(btnWrite);

        btnRead = new JButton(MakeJmpLib.PKG_PROJECT_CFG_EX.toUpperCase() + " Read");
        btnRead.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File ret = Utility.openLoadFileDialog(null, new File(Platform.getCurrentPath()));
                if (ret != null) {
                    loadFileMkj(ret.getPath());
                }
            }
        });
        btnRead.setBounds(390, 345, 91, 21);
        contentPane.add(btnRead);

        loadFileJar(jar);
        loadFileData(data);
        loadFileRes(res);
        loadFileOutput(out);
        textFieldCompVersion.setText(JMPLIB.BUILD_VERSION);
        
        JLabel lblVersionName = new JLabel("Name");
        lblVersionName.setBounds(94, 78, 50, 13);
        panel.add(lblVersionName);
        
        textField_vname = new JTextField();
        textField_vname.setColumns(10);
        textField_vname.setBounds(138, 75, 96, 19);
        textField_vname.setText(JMPLIB.VERSION_NAME);
        panel.add(textField_vname);
    }

    public void export() {
        String jar = textFieldJarPath.getText();
        String data = textFieldDataPath.getText();
        String res = textFieldResPath.getText();
        String plg = textFieldPluginName.getText();
        String out = textFieldOutput.getText();
        String ver = textFieldCompVersion.getText();
        String verName = textField_vname.getText();

        MakeJmpConfig config = new MakeJmpConfig(plg, jar, res, chckbxAddBlankData.isSelected(), data, out, ver, verName);
        MakeJmpLib.exportPackage(config);
    }

    public void loadFileData(String path) {
        textFieldDataPath.setText(path);
    }

    public void loadFileRes(String path) {
        textFieldResPath.setText(path);
    }

    public void loadFileJar(String path) {
        textFieldJarPath.setText(path);
        textFieldPluginName.setText(Utility.getFileNameNotExtension(path));
        textFieldCompVersion.setText(JMPLIB.BUILD_VERSION);
    }

    public void loadFileOutput(String path) {
        textFieldOutput.setText(path);
    }

    public void loadFileMkj(String path) {
        MakeJmpConfig cfg = new MakeJmpConfig();
        try {
            cfg.read(new File(path));
            loadFileJar(cfg.getJar());
            loadFileRes(cfg.getRes());
            loadFileData(cfg.getData());
            chckbxAddBlankData.setSelected(cfg.isAddData());
            if (chckbxAddBlankData.isSelected() == true) {
                textFieldDataPath.setEnabled(false);
            }
            loadFileOutput(cfg.getOutput());
        }
        catch (IOException e) {
        }
    }

    public void saveFileMkj(String path) {
        MakeJmpConfig cfg = new MakeJmpConfig(textFieldPluginName.getText(), textFieldJarPath.getText(), textFieldResPath.getText(),
                chckbxAddBlankData.isSelected(), textFieldDataPath.getText(), textFieldOutput.getText(), textFieldCompVersion.getText(),
                textField_vname.getText());
        try {
            cfg.write(new File(path));
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
        }
    }

    public interface DropFileEvent {
        abstract void catchDropEvent(File file);
    }

    public class JarDEvent implements DropFileEvent {

        @Override
        public void catchDropEvent(File file) {
            loadFileJar(file.getAbsolutePath());

            if (textFieldOutput.getText().isEmpty() == true) {
                textFieldOutput.setText(file.getParent());
            }
        }
    }

    public class DataDEvent implements DropFileEvent {

        @Override
        public void catchDropEvent(File file) {
            loadFileData(file.getAbsolutePath());
        }
    }

    public class ResDEvent implements DropFileEvent {

        @Override
        public void catchDropEvent(File file) {
            loadFileRes(file.getAbsolutePath());
        }
    }

    public class OutputDEvent implements DropFileEvent {

        @Override
        public void catchDropEvent(File file) {
            loadFileOutput(file.getAbsolutePath());
        }
    }

    public class mkjDEvent implements DropFileEvent {

        @Override
        public void catchDropEvent(File file) {
            loadFileMkj(file.getAbsolutePath());
        }
    }

    /**
     *
     * ドラッグ＆ドロップハンドラー
     *
     */
    public class DropFileHandler extends TransferHandler {
        DropFileEvent fEvent;

        public DropFileHandler(DropFileEvent event) {
            fEvent = event;
        }

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
                @SuppressWarnings("unchecked")
                List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

                // 一番先頭のファイルを取得
                if ((files != null) && (files.size() > 0)) {
                    String path = files.get(0).getPath();

                    // ドロップアイテム取得
                    fEvent.catchDropEvent(new File(path));
                }
                return true;
            }
            catch (Exception e) {
                /* 受け取らない */
            }
            return false;
        }
    }
}
