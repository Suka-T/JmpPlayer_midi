package jmp.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.WindowManager;
import jmp.file.FileResult;
import jmp.file.IFileResultCallback;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;

public class HistoryDialog extends JMPDialog implements IFileResultCallback {

    private final JPanel contentPanel = new JPanel();
    private DefaultListModel<String> historyModel = null;
    private JList<String> history = null;
    private JButton buttonClear;
    private JButton playButton;
    private JButton cancelButton;

    /**
     * Create the dialog.
     */
    public HistoryDialog() {
        super();
        setTitle("履歴");
        setBounds(100, 100, 600, 480);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        contentPanel.setBackground(getJmpBackColor());

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JScrollPane scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            {
                historyModel = new DefaultListModel<String>();
                history = new JList<String>(historyModel);
                scrollPane.setViewportView(history);
                updateHistoryData();
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            buttonPane.setBackground(getJmpBackColor());
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                playButton = new JButton("再生");
                playButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String item = history.getSelectedValue();
                        if (item != null) {
                            // 履歴に残さずロードする
                            JMPFlags.NoneHistoryLoadFlag = true;
                            JMPCore.getSoundManager().stop();
                            JMPCore.getFileManager().loadFileToPlay(item);
                        }
                    }
                });
                {
                    buttonClear = new JButton("履歴クリア");
                    buttonClear.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JMPCore.getDataManager().clearHistory();
                            updateHistoryData();
                        }
                    });
                    buttonPane.add(buttonClear);
                }
                playButton.setActionCommand("okCommand");
                buttonPane.add(playButton);
                getRootPane().setDefaultButton(playButton);
            }
            {
                cancelButton = new JButton("閉じる");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }

        // ロード後のコールバックを登録
        JMPCore.getFileManager().addLoadResultCallback(this);
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            updateHistoryData();
        }
        super.setVisible(b);
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        LanguageManager lm = JMPCore.getLanguageManager();
        WindowManager wm = JMPCore.getWindowManager();
        setTitle(lm.getLanguageStr(LangID.History));
        setFont(wm.getCurrentFont(getFont()));
        wm.changeFont(buttonClear, LangID.Clear);
        wm.changeFont(playButton, LangID.Playback);
        wm.changeFont(cancelButton, LangID.Close);
        wm.changeFont(history);

    }

    public void updateHistoryData() {
        int backupSelectedIndex = history.getSelectedIndex();
        historyModel.removeAllElements();

        for (String line : JMPCore.getDataManager().getHistory()) {
            historyModel.addElement(line);
        }

        if (backupSelectedIndex != -1 && JMPCore.getDataManager().getHistorySize() > 0) {
            history.setSelectedIndex(backupSelectedIndex);
        }
    }

    @Override
    public void begin(FileResult result) {
    }

    @Override
    public void end(FileResult result) {
        if (result.status == true) {
            // ロード後更新する
            updateHistoryData();

            if (JMPCore.getDataManager().getHistorySize() > 0) {
                history.setSelectedIndex(0);
            }
        }
    }
}
