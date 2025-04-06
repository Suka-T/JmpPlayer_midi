package jmp.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Utility;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.ui.DropFileCallbackHandler;
import jmp.gui.ui.IDropFileCallback;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;
import jmp.util.JmpUtil;

public class MidiWavSynchronizerDialog extends JMPDialog { //extends JDialog implements IJmpWindow {

    private final JPanel contentPanel = new JPanel();
    private JTextField textFieldMidiPath;
    private JTextField textFieldMediaFilePath;
    private JLabel lblMidi;
    private JLabel lblMedia;
    private JButton btnMidiConfig;
    private JButton playbackButton;
    private JButton loadButton;
    private JButton cancelButton;
    private JButton btnToWav;
    private JPanel buttonPane;

    /**
     * Create the dialog.
     */
    public MidiWavSynchronizerDialog() {
        super();
        setResizable(false);
        setTitle("Synchronized MIDI and WAV playback");
        setBounds(100, 100, 450, 260);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        
        lblMidi = new JLabel("MIDI File Path");
        lblMidi.setBounds(12, 10, 205, 13);
        contentPanel.add(lblMidi);
        
        textFieldMidiPath = new JTextField();
        textFieldMidiPath.setBounds(12, 33, 372, 19);
        contentPanel.add(textFieldMidiPath);
        textFieldMidiPath.setColumns(10);
        textFieldMidiPath.setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                if (textFieldMidiPath.isEnabled() == false) {
                    return;
                }
                setMidiPath(file.getPath());
            }
        }));
        
        lblMedia = new JLabel("WAV File Path");
        lblMedia.setBounds(12, 94, 205, 13);
        contentPanel.add(lblMedia);
        
        textFieldMediaFilePath = new JTextField();
        textFieldMediaFilePath.setColumns(10);
        textFieldMediaFilePath.setBounds(12, 117, 372, 19);
        contentPanel.add(textFieldMediaFilePath);
        textFieldMediaFilePath.setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                if (textFieldMediaFilePath.isEnabled() == false) {
                    return;
                }
                setMediaPath(file.getPath());
            }
        }));
        
        JButton btnOpenMidi = new JButton("...");
        btnOpenMidi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                SystemManager system = JMPCore.getSystemManager();
                String[] exMIDI = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MIDI));
                JFileChooser filechooser = new JFileChooser();
                filechooser.setFileFilter(createFileFilter("MIDI Files", exMIDI));

                File dir = new File(JMPCore.getDataManager().getPlayListPath());
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
                            setMidiPath(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        btnOpenMidi.setBounds(383, 33, 39, 21);
        contentPanel.add(btnOpenMidi);
        
        JButton btnOpenWav = new JButton("...");
        btnOpenWav.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                SystemManager system = JMPCore.getSystemManager();
                String[] exWAV = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_WAV));
                JFileChooser filechooser = new JFileChooser();
                filechooser.setFileFilter(createFileFilter("WAV Files", exWAV));

                File dir = new File(JMPCore.getDataManager().getPlayListPath());
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
                            setMediaPath(path);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        btnOpenWav.setBounds(383, 117, 39, 21);
        contentPanel.add(btnOpenWav);
        
        btnMidiConfig = new JButton("MIDI Device Setting");
        btnMidiConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_SETUP).showWindow();
            }
        });
        btnMidiConfig.setBounds(261, 62, 161, 21);
        contentPanel.add(btnMidiConfig);
        
        btnToWav = new JButton("Path To .wav");
        btnToWav.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path = textFieldMidiPath.getText();
                int lastDotIndex = path.lastIndexOf(".");
                if (lastDotIndex != -1) {
                    String wavPath = path.substring(0, lastDotIndex + 1) + "wav";
                    File file = new File(wavPath);
                    if (file.exists() == true) {
                        textFieldMediaFilePath.setText(wavPath);
                    }
                    else {
                        textFieldMediaFilePath.setText("");
                    }
                }
                else {
                    textFieldMediaFilePath.setText("");
                }
            }
        });
        btnToWav.setBounds(22, 62, 116, 21);
        contentPanel.add(btnToWav);
        {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                playbackButton = new JButton("Play");
                playbackButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        executeLoadToPlay();
                    }
                });
                buttonPane.add(playbackButton);
                getRootPane().setDefaultButton(playbackButton);
            }
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                loadButton = new JButton("Load");
                loadButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        executeLoad();
                    }
                });
                buttonPane.add(loadButton);
            }
            {
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        hideWindow();
                    }
                });
                buttonPane.add(cancelButton);
            }
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
    
    public void setMidiPath(String path) {
        textFieldMidiPath.setText(path);
    }
    
    public void setMediaPath(String path) {
        textFieldMediaFilePath.setText(path);
    }
    
    public void executeLoadToPlay() {
        File f1 = new File(textFieldMidiPath.getText());
        File f2 = new File(textFieldMediaFilePath.getText());
        JMPCore.getFileManager().loadDualFileToPlay(f1, f2);
        
        hideWindow();
    }

    
    public void executeLoad() {
        File f1 = new File(textFieldMidiPath.getText());
        File f2 = new File(textFieldMediaFilePath.getText());
        JMPCore.getFileManager().loadDualFile(f1, f2);
        
        hideWindow();
    }

    @Override
    public void showWindow() {
        setVisible(true);
    }

    @Override
    public void hideWindow() {
        setVisible(false);
    }

    @Override
    public boolean isWindowVisible() {
        return isVisible();
    }

    @Override
    public void setDefaultWindowLocation() {
    }

    @Override
    public void repaintWindow() {
        repaint();
    }
    
    @Override
    public void updateLanguage() {
        WindowManager wm = JMPCore.getWindowManager();
        LanguageManager lm = JMPCore.getLanguageManager();
        setTitle(lm.getLanguageStr(LangID.Synchronized_MIDI_and_WAV_play));
        setFont(wm.getCurrentFont(getFont()));
        wm.changeFont(lblMidi, LangID.Midi_File);
        wm.changeFont(lblMedia, LangID.Wav_File);
        wm.changeFont(btnMidiConfig, LangID.MIDI_device_settings);
        wm.changeFont(playbackButton, LangID.Playback);
        wm.changeFont(loadButton, LangID.Load);
        wm.changeFont(cancelButton, LangID.Close);
    }
    
    @Override
    public void updateBackColor() {
        super.updateBackColor();
        contentPanel.setBackground(getJmpBackColor());
        buttonPane.setBackground(getJmpBackColor());
        lblMidi.setBackground(getJmpBackColor());
        lblMidi.setForeground(Utility.getForegroundColor(getJmpBackColor()));
        lblMedia.setBackground(getJmpBackColor());
        lblMedia.setForeground(Utility.getForegroundColor(getJmpBackColor()));
    }
}
