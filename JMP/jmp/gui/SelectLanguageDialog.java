package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage;
import jmp.lang.DefineLanguage.LangID;

public class SelectLanguageDialog extends JMPDialog {

    private final JPanel contentPanel = new JPanel();
    private JComboBox<String> comboBoxLang;
    private JButton closeButton;
    private JLabel lblTest;

    /**
     * Create the dialog.
     */
    public SelectLanguageDialog() {
        setResizable(false);
        setTitle("Language");
        setBounds(100, 100, 365, 125);
        getContentPane().setLayout(new BorderLayout());
        setModal(true);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        contentPanel.setBackground(getJmpBackColor());

        comboBoxLang = new JComboBox<String>();
        comboBoxLang.setFont(new Font("Dialog", Font.PLAIN, 15));
        comboBoxLang.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (JMPCore.isFinishedInitialize() == true) {
                    int selected = comboBoxLang.getSelectedIndex();
                    if (selected >= 0) {
                        updateLanguage(selected);
                    }
                }
            }
        });
        comboBoxLang.setBounds(12, 10, 317, 28);
        contentPanel.add(comboBoxLang);
        {
            closeButton = new JButton("Apply");
            closeButton.setBounds(249, 57, 80, 21);
            contentPanel.add(closeButton);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int selected = comboBoxLang.getSelectedIndex();
                    if (selected >= 0) {
                        JMPCore.getDataManager().setLanguage(selected);
                    }
                    hideWindow();
                }
            });
            closeButton.setActionCommand("Cancel");
        }

        lblTest = new JLabel("test");
        lblTest.setBackground(new Color(0, 0, 0));
        lblTest.setForeground(new Color(175, 238, 238));
        lblTest.setHorizontalAlignment(SwingConstants.CENTER);
        lblTest.setFont(new Font("Dialog", Font.BOLD, 26));
        lblTest.setBounds(12, 48, 235, 38);
        contentPanel.add(lblTest);
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            LanguageManager lm = JMPCore.getLanguageManager();
            comboBoxLang.removeAllItems();
            for (int i = 0; i < DefineLanguage.NUMBER_OF_INDEX_LANG; i++) {

                // 無効な言語
                if (lm.isValidLanguageIndex(i) == false) {
                    continue;
                }

                comboBoxLang.addItem(JMPCore.getLanguageManager().getTitle(i, DefineLanguage.INDEX_LANG_ENGLISH));
            }

            int selected = JMPCore.getDataManager().getLanguage();
            if (lm.isValidLanguageIndex(selected) == false) {
                selected = 0;
            }

            comboBoxLang.setSelectedIndex(selected);

            updateLanguage(selected);
        }
        super.setVisible(b);
    }

    public void updateLanguage(int index) {
        WindowManager wm = JMPCore.getWindowManager();
        wm.changeFont(closeButton, LangID.Apply, index);
        wm.changeFont(lblTest, JMPCore.getLanguageManager().getTitle(index, index), index);
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();
        LanguageManager lm = JMPCore.getLanguageManager();
        WindowManager wm = JMPCore.getWindowManager();
        setTitle(lm.getLanguageStr(LangID.Language));
        setFont(wm.getCurrentFont(getFont()));
        wm.changeFont(comboBoxLang, DefineLanguage.INDEX_LANG_ENGLISH);
        updateLanguage(JMPCore.getDataManager().getLanguage());
    }
}
