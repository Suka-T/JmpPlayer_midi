package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import function.Platform;
import function.Utility;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage;
import jmp.lang.DefineLanguage.LangID;

public class LicenseReaderDialog extends JMPDialog implements WindowListener {

    private final JPanel contentPanel = new JPanel();
    private ButtonGroup btnGroupAcc;
    private JLabel labelAccept = new JLabel("上記の条件に：");
    private JRadioButton rdbtnReject = new JRadioButton("同意しない");
    private JRadioButton rdbtnAccept = new JRadioButton("同意する");
    // private JTextArea textAreaLisence = new JTextArea();
    private JEditorPane textAreaLisence = new JEditorPane();
    private JLabel labelClipMes = new JLabel("クリップボードにコピーしました");
    private JButton buttonCopy = new JButton("原文のコピー");

    /**
     * Create the dialog.
     */
    public LicenseReaderDialog() {
        super();
        setModal(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);
        setTitle("License");
        setBounds(100, 100, 725, 550);
        getContentPane().setLayout(new BorderLayout());
        addWindowListener(this);

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        rdbtnReject.setForeground(Color.WHITE);

        rdbtnReject.setSelected(true);
        rdbtnReject.setBounds(570, 442, 113, 21);
        contentPanel.add(rdbtnReject);
        rdbtnAccept.setForeground(Color.WHITE);

        rdbtnAccept.setBounds(453, 442, 113, 21);
        contentPanel.add(rdbtnAccept);
        labelAccept.setForeground(Color.WHITE);

        labelAccept.setBounds(408, 423, 160, 13);
        contentPanel.add(labelAccept);

        buttonCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // labelClipMes.setVisible(true);
                // Utility.setClipboard(textAreaLisence.getText());
                String contents = JMPCore.getLanguageManager().getReadmeContent();
                File defaultDirectory = new File(Platform.getCurrentPath());
                File saveFile = Utility.openSaveFileDialog(LicenseReaderDialog.this, defaultDirectory, "license.txt");
                if (saveFile != null) {
                    try {
                        Utility.outputTextFile(saveFile.getPath(), contents);
                        Utility.openExproler(saveFile);
                    }
                    catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        buttonCopy.setBounds(12, 424, 106, 21);
        contentPanel.add(buttonCopy);
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(null);
        }

        btnGroupAcc = new ButtonGroup();
        btnGroupAcc.add(rdbtnAccept);
        btnGroupAcc.add(rdbtnReject);

        labelClipMes.setForeground(new Color(0, 191, 255));
        labelClipMes.setFont(new Font("MS UI Gothic", Font.BOLD, 12));
        labelClipMes.setBounds(12, 456, 251, 13);
        labelClipMes.setVisible(false);
        contentPanel.add(labelClipMes);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 681, 403);
        contentPanel.add(scrollPane);
        textAreaLisence.setFont(new Font("Dialog", Font.PLAIN, 13));
        scrollPane.setViewportView(textAreaLisence);
        textAreaLisence.setEditable(false);
        {
            JButton okButton = new JButton("OK");
            okButton.setBounds(598, 478, 95, 21);
            contentPanel.add(okButton);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });
            okButton.setActionCommand("OK");
            getRootPane().setDefaultButton(okButton);
        }

        contentPanel.setBackground(getJmpBackColor());
        rdbtnReject.setBackground(getJmpBackColor());
        rdbtnAccept.setBackground(getJmpBackColor());
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            if (JMPFlags.ActivateFlag == true) {
                rdbtnAccept.setSelected(true);
                labelAccept.setVisible(false);
                rdbtnReject.setVisible(false);
                rdbtnAccept.setVisible(false);
            }
            else {
                labelAccept.setVisible(true);
                rdbtnReject.setVisible(true);
                rdbtnAccept.setVisible(true);
            }

            String content = JMPCore.getLanguageManager().getReadmeContent();
            textAreaLisence.setText("");
            textAreaLisence.setText(content);
            labelClipMes.setVisible(false);
        }
        super.setVisible(b);
    }

    public void start() {
        setVisible(true);
    }

    public void close() {
        if (rdbtnAccept.isSelected() == true) {
            /* 使用可能 */
            JMPFlags.ActivateFlag = true;
        }
        else {
            JMPFlags.ActivateFlag = false;
        }
        setVisible(false);
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();
        WindowManager wm = JMPCore.getWindowManager();
        LanguageManager lm = JMPCore.getLanguageManager();

        setTitle(lm.getLanguageStr(LangID.License));
        setFont(wm.getCurrentFont(getFont()));
        wm.changeFont(textAreaLisence, DefineLanguage.INDEX_LANG_JAPANESE);
        wm.changeFont(labelAccept, LangID.Above_conditions);
        wm.changeFont(rdbtnReject, LangID.Reject);
        wm.changeFont(rdbtnAccept, LangID.Accept);
        wm.changeFont(labelClipMes, "");
        wm.changeFont(buttonCopy, LangID.Original_copy);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (JMPFlags.ActivateFlag == false) {
            rdbtnAccept.setSelected(false);
            rdbtnReject.setSelected(true);
        }
        close();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
