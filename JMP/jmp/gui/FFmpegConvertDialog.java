package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Platform;
import function.Platform.KindOfPlatform;
import function.Utility;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.ui.DropFileCallbackHandler;
import jmp.gui.ui.IDropFileCallback;
import jmp.gui.ui.JMPDialog;
import jmp.gui.ui.MultiKeyActionTextField;
import jmp.lang.DefineLanguage.LangID;
import jmp.task.ICallbackFunction;
import process.IProcessingCallback;

public class FFmpegConvertDialog extends JMPDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField textFieldFFmpegExePath;
    private JTextField textFieldInputFile;
    private JLabel lblStatus;
    private JCheckBox chckbxLeave;
    private JCheckBox chckbxToPlay;
    private JButton convertButton;
    private JLabel lblPath;
    private JLabel lblInputFile;
    private JButton btnExpButton;
    private JTextField dstExtTextField;
    private JCheckBox chckbxInstalled;
    private JButton buttonOpenInput;
    private JButton btnOpenExe;

    private void syncConrolEnable(boolean b) {
        buttonOpenInput.setEnabled(b);
        textFieldInputFile.setEnabled(b);
        dstExtTextField.setEnabled(b);
        convertButton.setEnabled(b);
    }

    /**
     * Create the dialog.
     */
    public FFmpegConvertDialog() {
        super();
        setTitle("FFmpeg convert");
        setBounds(100, 100, 450, 240);
        setResizable(false);

        contentPanel.setBackground(getJmpBackColor());

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        textFieldFFmpegExePath = new MultiKeyActionTextField();
        textFieldFFmpegExePath.setBounds(12, 48, 373, 19);
        contentPanel.add(textFieldFFmpegExePath);
        textFieldFFmpegExePath.setColumns(10);
        textFieldFFmpegExePath.setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                if (textFieldFFmpegExePath.isEnabled() == false) {
                    return;
                }
                setExePath(file.getPath());
            }
        }));

        lblPath = new JLabel("FFmpeg path(.exe)");
        lblPath.setForeground(Color.WHITE);
        lblPath.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblPath.setBounds(12, 25, 373, 19);
        contentPanel.add(lblPath);

        lblInputFile = new JLabel("Input file");
        lblInputFile.setForeground(Color.WHITE);
        lblInputFile.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblInputFile.setBounds(12, 83, 373, 19);
        contentPanel.add(lblInputFile);

        textFieldInputFile = new MultiKeyActionTextField();
        textFieldInputFile.setColumns(10);
        textFieldInputFile.setBounds(12, 106, 373, 19);
        contentPanel.add(textFieldInputFile);
        textFieldInputFile.setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                if (textFieldInputFile.isEnabled() == false) {
                    return;
                }
                setInputPath(file.getPath());
            }
        }));

        lblStatus = new JLabel("");
        lblStatus.setBackground(new Color(0, 0, 0));
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setBounds(12, 201, 373, 19);
        contentPanel.add(lblStatus);

        chckbxLeave = new JCheckBox("Leave output file");
        chckbxLeave.setForeground(new Color(255, 255, 255));
        chckbxLeave.setFont(new Font("Dialog", Font.PLAIN, 12));
        chckbxLeave.setBackground(getJmpBackColor());
        chckbxLeave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getDataManager().setFFmpegLeaveOutputFile(chckbxLeave.isSelected());
            }
        });
        chckbxLeave.setBounds(224, 131, 174, 21);
        contentPanel.add(chckbxLeave);

        btnOpenExe = new JButton("...");
        btnOpenExe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                filechooser.setFileFilter(createFileFilter("EXE Files", "exe"));

                File dir = null;
                String exePath = textFieldFFmpegExePath.getText();
                if (Utility.isExsistFile(exePath) == true) {
                    File ef = new File(exePath);
                    dir = ef.getParentFile();
                }
                else {
                    dir = new File(Platform.getCurrentPath());
                }
                if (dir != null) {
                    if (dir.isDirectory() == false) {
                        dir = dir.getParentFile();
                    }
                }

                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            setExePath(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        btnOpenExe.setBounds(385, 47, 47, 21);
        contentPanel.add(btnOpenExe);

        buttonOpenInput = new JButton("...");
        buttonOpenInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                File dir = new File(Platform.getCurrentPath());
                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            setInputPath(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        buttonOpenInput.setBounds(385, 105, 47, 21);
        contentPanel.add(buttonOpenInput);

        chckbxToPlay = new JCheckBox("Play after convert");
        chckbxToPlay.setSelected(false);
        chckbxToPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        chckbxToPlay.setFont(new Font("Dialog", Font.PLAIN, 12));
        chckbxToPlay.setForeground(new Color(255, 255, 255));
        chckbxToPlay.setBackground(getJmpBackColor());
        chckbxToPlay.setBounds(12, 131, 174, 21);
        contentPanel.add(chckbxToPlay);

        dstExtTextField = new JTextField();
        dstExtTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        dstExtTextField.setText("wav");
        dstExtTextField.setBounds(362, 83, 70, 19);
        contentPanel.add(dstExtTextField);
        dstExtTextField.setColumns(10);

        chckbxInstalled = new JCheckBox("installed");
        chckbxInstalled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = chckbxInstalled.isSelected();
                JMPCore.getDataManager().setFFmpegInstalled(isSelected);
                updateGuiState();
            }
        });
        chckbxInstalled.setBounds(329, 24, 103, 21);
        contentPanel.add(chckbxInstalled);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground(getJmpBackColor());
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                convertButton = new JButton("Convert");
                convertButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        executeConvert();
                    }
                });

                btnExpButton = new JButton("Exploler");
                btnExpButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openOutputFolder();
                    }
                });
                buttonPane.add(btnExpButton);
                convertButton.setActionCommand("OK");
                buttonPane.add(convertButton);
                getRootPane().setDefaultButton(convertButton);
            }
        }

        dstExtTextField.setText("wav");

        updateGuiState();
        updateBackColor();
    }

    private void updateGuiState() {
        textFieldFFmpegExePath.setEnabled(!JMPCore.getDataManager().isFFmpegInstalled());
        btnOpenExe.setEnabled(!JMPCore.getDataManager().isFFmpegInstalled());
        if (JMPCore.getDataManager().isFFmpegInstalled() == true) {
            textFieldFFmpegExePath.setText(JMPCore.getSystemManager().getFFmpegCommand());
        }
        else {
            textFieldFFmpegExePath.setText(JMPCore.getDataManager().getFFmpegPath());
        }
    }

    private FileNameExtensionFilter createFileFilter(String exName, String... ex) {
        String exs = "";
        for (int i = 0; i < ex.length; i++) {
            if (i > 0) {
                exs += ", ";
            }
            exs += String.format("*.%s", ex[i]);
        }

        String description = String.format("%s (%s)", exName, exs);
        return new FileNameExtensionFilter(description, ex);
    }

    public void setExePath(String path) {
        if (JMPCore.getSystemManager().isFFmpegInstalled() == true) {
            return;
        }
        JMPCore.getDataManager().setFFmpegPath(path);

        textFieldFFmpegExePath.setText(JMPCore.getDataManager().getFFmpegPath());
    }

    public void setInputPath(String path) {
        textFieldInputFile.setText(path);
    }

    public void openOutputFolder() {
        File outdir = new File(JMPCore.getSystemManager().getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_FFMPEG_OUTPUT));
        if (outdir.exists() == true) {
            try {
                Utility.openExproler(outdir);
            }
            catch (IOException e2) {
            }
        }
    }

    private void executeConvert() {
        SystemManager system = JMPCore.getSystemManager();
        LanguageManager lm = JMPCore.getLanguageManager();
        String inpath = textFieldInputFile.getText();
        if (Utility.isExsistFile(inpath) == false) {
            lblStatus.setForeground(Color.RED);
            String name = String.format("\"%s\"", lm.getLanguageStr(LangID.Input_file));
            lblStatus.setText(lm.getLanguageStr(LangID.A_is_invalid).replace("[NAME]", name));
            return;
        }

        system.setFFmpegPath(textFieldFFmpegExePath.getText());
        if (system.isValidFFmpegWrapper() == false) {
            lblStatus.setForeground(Color.RED);
            String name = String.format("\"%s\"", lm.getLanguageStr(LangID.FFmpeg_path));
            lblStatus.setText(lm.getLanguageStr(LangID.A_is_invalid).replace("[NAME]", name));
            return;
        }
        String inname = Utility.getFileNameNotExtension(inpath);
        File outdir = new File(JMPCore.getSystemManager().getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_FFMPEG_OUTPUT));

        String dstExt = dstExtTextField.getText();
        if (dstExt.isEmpty() == true) {
            dstExt = "wav";
        }
        String outpath = Utility.pathCombin(outdir.getPath(), String.format("%s.%s", inname, dstExt));

        File in = new File(inpath);
        File out = new File(outpath);

        IProcessingCallback callback = new IProcessingCallback() {

            boolean isConverting = true;

            @Override
            public void end(int result) {
                isConverting = false;

                syncConrolEnable(true);

                if (result != 0) {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText(lm.getLanguageStr(LangID.Conversion_failed));
                    repaint();
                    return;
                }

                lblStatus.setForeground(Color.GREEN);

                String name = Utility.getFileNameAndExtension(out);
                lblStatus.setText(lm.getLanguageStr(LangID.Conversion_completed) + "(" + name + ")");
                repaint();

                if (chckbxToPlay.isSelected() == true) {
                    JMPCore.getFileManager().loadFileToPlay(out.getPath());
                }
                else {
                    JMPCore.getWindowManager().showFilePickupDialog(outdir, out);
                }
            }

            @Override
            public void begin() {
                lblStatus.setForeground(Color.LIGHT_GRAY);
                lblStatus.setText(lm.getLanguageStr(LangID.Now_converting));
                repaint();

                syncConrolEnable(false);

                JMPCore.getTaskManager().addCallbackPackage(1000, new ICallbackFunction() {

                    int ite = 0;

                    String[] ites = { ">-- ", "->- ", "--> " };

                    @Override
                    public void callback() {
                        if (isConverting == false) {
                            return;
                        }
                        lblStatus.setText(ites[ite] + dstExtTextField.getText());
                        ite++;
                        if (ite >= ites.length) {
                            ite = 0;
                        }
                        repaint();
                    }

                    @Override
                    public boolean isDeleteConditions(int count) {
                        if (isConverting == false) {
                            return true;
                        }
                        return ICallbackFunction.super.isDeleteConditions(count);
                    }
                });
            }
        };

        system.setFFmpegWrapperCallback(callback);

        try {
            system.executeConvert(in.getPath(), out.getPath());
        }
        catch (IOException e1) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Faild.");
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            updateConfig("");
        }
        super.setVisible(b);
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        WindowManager wm = JMPCore.getWindowManager();
        LanguageManager lm = JMPCore.getLanguageManager();

        setTitle(lm.getLanguageStr(LangID.FFmpeg_converter));
        setFont(wm.getCurrentFont(getFont()));
        wm.changeFont(lblPath, LangID.FFmpeg_path);
        wm.changeFont(lblInputFile, LangID.Input_file);
        wm.changeFont(convertButton, LangID.Convert);
        wm.changeFont(btnExpButton, LangID.Open_with_Explorer);
        wm.changeFont(chckbxToPlay, LangID.Play_after_convert);
        wm.changeFont(chckbxLeave, LangID.Leave_output_file);

    }

    @Override
    public void updateConfig(String key) {
        textFieldFFmpegExePath.setText(JMPCore.getDataManager().getFFmpegPath());
        chckbxLeave.setSelected(JMPCore.getDataManager().isFFmpegLeaveOutputFile());
        chckbxInstalled.setSelected(JMPCore.getDataManager().isFFmpegInstalled());
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            chckbxInstalled.setEnabled(false);
        }
        else {
            chckbxInstalled.setEnabled(true);
        }
        super.updateConfig(key);

        updateGuiState();
    }

    @Override
    public void updateBackColor() {
        super.updateBackColor();
        contentPanel.setBackground(getJmpBackColor());
        chckbxInstalled.setBackground(getJmpBackColor());
        chckbxInstalled.setForeground(Utility.getForegroundColor(getJmpBackColor()));
    }
}
