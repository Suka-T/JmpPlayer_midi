package jmp.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import function.Platform;
import function.Utility;
import jlib.gui.IJmpWindow;
import jmp.core.JMPCore;
import jmp.core.SoundManager;
import jmp.core.WindowManager;
import jmp.gui.ui.IJMPComponentUI;
import jmp.lang.DefineLanguage.LangID;
import jmp.util.JmpUtil;

public class FilePickupDialog extends JDialog implements IJMPComponentUI, IJmpWindow {

    private final JPanel contentPanel = new JPanel();
    private DefaultListModel<String> listModel = null;
    private JList<String> list;
    private JLabel lblDirectory;
    private File dir = null;
    private File selectionFile = null;
    private Map<String, File> map = null;
    private JButton playButton;
    private JButton btnDelete;
    private JButton btnOpenDir;
    private JButton cancelButton;

    /**
     * Create the dialog.
     */
    public FilePickupDialog() {
        setModal(true);
        setTitle("Directory Viewer");
        setBounds(100, 100, 601, 224);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            listModel = new DefaultListModel<String>();
        }
        {
            lblDirectory = new JLabel("Directory : ");
            contentPanel.add(lblDirectory, BorderLayout.NORTH);
        }
        {
            JScrollPane scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            list = new JList<String>(listModel);
            scrollPane.setViewportView(list);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                playButton = new JButton("Play");
                playButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        File f = getSelectedFile();
                        if (f == null) {
                            return;
                        }
                        JMPCore.getSoundManager().stop();
                        JMPCore.getFileManager().loadFileToPlay(f);
                        setVisible(false);
                    }
                });
                playButton.setActionCommand("OK");
                buttonPane.add(playButton);
                getRootPane().setDefaultButton(playButton);
            }
            {
                btnDelete = new JButton("Delete");
                btnDelete.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int[] indexs = list.getSelectedIndices();
                        if (indexs.length <= 0) {
                            return;
                        }

                        for (int i = 0; i < indexs.length; i++) {
                            String name = listModel.getElementAt(indexs[i]);
                            JmpUtil.deleteFileDirectory(map.get(name));
                        }
                        updateList();
                    }
                });
                buttonPane.add(btnDelete);
            }
            {
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                {
                    btnOpenDir = new JButton("Open");
                    btnOpenDir.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            try {
                                Utility.openExproler(dir);
                            }
                            catch (IOException e1) {
                            }
                        }
                    });
                    buttonPane.add(btnOpenDir);
                }
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    public File getSelectedFile() {
        int[] indexs = list.getSelectedIndices();
        if (indexs.length <= 0) {
            return null;
        }
        String name = listModel.getElementAt(indexs[0]);
        return map.get(name);
    }

    public void setDirectory(File f, File selection) {
        dir = f;
        selectionFile = selection;
        updateList();
    }

    public void updateList() {
        listModel.removeAllElements();
        if (dir == null) {
            dir = new File(Platform.getCurrentPath());
            // lblDirectory.setText("None");
            // return;
        }

        lblDirectory.setText(dir.getAbsolutePath());
        map = JMPCore.getFileManager().getFileMap(dir);

        int index = 0;
        int focus = 0;
        SoundManager sm = JMPCore.getSoundManager();
        for (String key : map.keySet()) {
            File f = map.get(key);
            if (sm.checkMusicFileExtention(f) == true) {
                listModel.addElement(key);

                if (selectionFile != null) {
                    String suchName = f.getName();
                    String focusName = selectionFile.getName();
                    if (suchName.equals(focusName)) {
                        focus = index;
                    }
                }
                index++;
            }
        }

        if (listModel.getSize() > 0) {
            list.setSelectedIndex(focus);
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            updateList();
        }
        super.setVisible(b);
    }

    @Override
    public void updateLanguage() {
        IJmpWindow.super.updateLanguage();
        WindowManager wm = JMPCore.getWindowManager();
        wm.changeFont(cancelButton, LangID.Close);
        wm.changeFont(btnDelete, LangID.Remove);
        wm.changeFont(playButton, LangID.Playback);
        wm.changeFont(btnOpenDir, LangID.Open_with_Explorer);
    }

    @Override
    public void updateBackColor() {
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
    }

}
