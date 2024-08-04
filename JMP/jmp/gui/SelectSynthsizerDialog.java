package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.sound.midi.MidiDevice;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import function.Utility;
import jmp.JMPFlags;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.SoundManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;

public class SelectSynthsizerDialog extends JMPDialog {
    public static final int MAX_ROW_COUNT = 15;

    private MidiDevice.Info[] infosOfRecv = null;
    private MidiDevice.Info[] infosOfTrans = null;
    private static final String NameNoSepareter = "-";

    private final JPanel contentPanel = new JPanel();
    private JLabel lblMidiDevices = new JLabel("MIDI OUT Device");
    private JLabel lblDebugmode;
    JComboBox<String> comboRecvMode;
    private JLabel lblVendorOfRecv;
    private JLabel lblVersionOfRecv;
    private JLabel lblDescriptionOfRecv;
    private JLabel lblPortOfRecv;
    private JTabbedPane tabbedPane;
    private JPanel panel;
    private JPanel panel_1;
    private JLabel labelMidiOutDevice;
    private JComboBox<String> comboTransMode;
    private JLabel lblVendorOfTrans;
    private JLabel lblVersionOfTrans;
    private JLabel lblDescriptionOfTrans;
    private JLabel lblPortOfTrans;

    public static final String DEFAULT_ITEM_NAME = "Using Default";

    public static final int INDEX_OF_AUTO_SELECTION = 0;
    public static final int INDEX_OF_NONE = 1;
    public static final int NUMBER_OF_CUSTOM_SYNTH = 2;

    private JLabel labelDevelop;
    private JCheckBox chckbxStartupShowDialog;

    /**
     * コンストラクタ
     */
    public SelectSynthsizerDialog(boolean isVisibleMidiOut, boolean isVisibleMidiIn) {
        /*
         * Window Builder
         */
        super();
        setAlwaysOnTop(true);
        setFont(new Font("Dialog", Font.BOLD, 12));
        setResizable(false);
        setTitle("シンセサイザー選択");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setBounds(100, 100, 465, 275);
        getContentPane().setLayout(new BorderLayout());

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        contentPanel.setBackground(getJmpBackColor());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(null);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        lblDebugmode = new JLabel("DebugMode");
        lblDebugmode.setBounds(12, 86, 410, 13);
        {
            JButton okButton = new JButton("OK");
            okButton.setBounds(359, 211, 84, 21);
            contentPanel.add(okButton);
            okButton.setBackground(Color.WHITE);
            okButton.setActionCommand("OK");
            getRootPane().setDefaultButton(okButton);

            tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            tabbedPane.setBackground(Color.DARK_GRAY);
            tabbedPane.setBounds(12, 10, 431, 195);
            contentPanel.add(tabbedPane);

            panel = new JPanel();
            panel.setBackground(Color.WHITE);
            tabbedPane.addTab("MIDI OUT", null, panel, null);
            tabbedPane.setBackgroundAt(0, Color.LIGHT_GRAY);
            panel.setLayout(null);

            comboRecvMode = new JComboBox<String>();
            comboRecvMode.setBounds(12, 33, 402, 22);
            comboRecvMode.setMaximumRowCount(MAX_ROW_COUNT);
            panel.add(comboRecvMode);

            lblVendorOfRecv = new JLabel("Vendor : ");
            lblVendorOfRecv.setBounds(12, 62, 410, 13);
            panel.add(lblVendorOfRecv);
            lblVendorOfRecv.setForeground(Color.DARK_GRAY);

            lblVersionOfRecv = new JLabel("Version : ");
            lblVersionOfRecv.setBounds(12, 85, 410, 13);
            panel.add(lblVersionOfRecv);
            lblVersionOfRecv.setForeground(Color.DARK_GRAY);

            lblDescriptionOfRecv = new JLabel("Description : ");
            lblDescriptionOfRecv.setVerticalAlignment(SwingConstants.TOP);
            lblDescriptionOfRecv.setBounds(22, 115, 400, 43);
            panel.add(lblDescriptionOfRecv);
            lblDescriptionOfRecv.setForeground(Color.DARK_GRAY);

            lblPortOfRecv = new JLabel("");
            lblPortOfRecv.setBounds(12, 131, 249, 13);
            panel.add(lblPortOfRecv);
            lblPortOfRecv.setForeground(Color.LIGHT_GRAY);
            lblMidiDevices.setBounds(12, 10, 210, 13);
            panel.add(lblMidiDevices);
            lblMidiDevices.setForeground(Color.BLACK);

            panel_1 = new JPanel();
            panel_1.setBackground(Color.WHITE);
            tabbedPane.addTab("MIDI IN", null, panel_1, null);
            panel_1.setLayout(null);

            labelMidiOutDevice = new JLabel("MIDI IN Device");
            labelMidiOutDevice.setForeground(Color.BLACK);
            labelMidiOutDevice.setBounds(12, 10, 210, 13);
            panel_1.add(labelMidiOutDevice);

            comboTransMode = new JComboBox<String>();
            comboTransMode.setBounds(12, 33, 402, 22);
            comboTransMode.setMaximumRowCount(MAX_ROW_COUNT);
            panel_1.add(comboTransMode);

            lblVendorOfTrans = new JLabel("Vendor : ");
            lblVendorOfTrans.setForeground(Color.DARK_GRAY);
            lblVendorOfTrans.setBounds(12, 62, 410, 13);
            panel_1.add(lblVendorOfTrans);

            lblVersionOfTrans = new JLabel("Version : ");
            lblVersionOfTrans.setForeground(Color.DARK_GRAY);
            lblVersionOfTrans.setBounds(12, 85, 410, 13);
            panel_1.add(lblVersionOfTrans);

            lblDescriptionOfTrans = new JLabel("Description : ");
            lblDescriptionOfTrans.setForeground(Color.DARK_GRAY);
            lblDescriptionOfTrans.setVerticalAlignment(SwingConstants.TOP);
            lblDescriptionOfTrans.setBounds(22, 115, 400, 43);
            panel_1.add(lblDescriptionOfTrans);

            lblPortOfTrans = new JLabel("");
            lblPortOfTrans.setForeground(Color.LIGHT_GRAY);
            lblPortOfTrans.setBounds(12, 131, 249, 13);
            panel_1.add(lblPortOfTrans);

            labelDevelop = new JLabel("開発者モード");
            labelDevelop.setForeground(Color.YELLOW);
            labelDevelop.setHorizontalAlignment(SwingConstants.RIGHT);
            labelDevelop.setBounds(339, 10, 104, 13);
            contentPanel.add(labelDevelop);

            chckbxStartupShowDialog = new JCheckBox("起動時に毎回表示するか");
            chckbxStartupShowDialog.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JMPCore.getDataManager().setShowStartupDeviceSetup(chckbxStartupShowDialog.isSelected());
                }
            });
            chckbxStartupShowDialog.setBackground(Color.DARK_GRAY);
            chckbxStartupShowDialog.setForeground(Color.WHITE);
            chckbxStartupShowDialog.setBounds(12, 211, 310, 21);
            contentPanel.add(chckbxStartupShowDialog);
            comboRecvMode.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    try {
                        String listName = comboRecvMode.getSelectedItem().toString().trim();
                        int devIndex = getOrgDeviceIndex(listName);

                        String vendor = "Vendor : ";
                        String version = "Version : ";
                        String description = "";
                        String port = "";

                        MidiDevice.Info info = null;

                        if (comboRecvMode.getSelectedIndex() == INDEX_OF_AUTO_SELECTION) {
                            vendor += "";
                            version += "";
                            description += "Automatically select an available synthesizer.";
                        }
                        else if (comboRecvMode.getSelectedIndex() == INDEX_OF_NONE) {
                            vendor += "";
                            version += "";
                            description += "Don't use synthesizer.";
                        }
                        else {
                            info = infosOfRecv[devIndex];
                        }

                        if (info != null) {
                            vendor += info.getVendor();
                            version += info.getVersion();
                            description += info.getDescription();
                        }

                        lblVendorOfRecv.setText(vendor);
                        lblVersionOfRecv.setText(version);
                        lblDescriptionOfRecv.setText(description);
                        lblPortOfRecv.setText(port);
                    }
                    catch (Exception e1) {
                    }
                    finally {
                    }
                }
            });
            comboTransMode.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    try {
                        String listName = comboTransMode.getSelectedItem().toString().trim();
                        int devIndex = getOrgDeviceIndex(listName);

                        String vendor = "Vendor : ";
                        String version = "Version : ";
                        String description = "";
                        String port = "";

                        MidiDevice.Info info = null;

                        if (listName.equals("") == false) {
                            info = infosOfTrans[devIndex];
                        }

                        if (info != null) {
                            vendor += info.getVendor();
                            version += info.getVersion();
                            description += info.getDescription();
                        }

                        lblVendorOfTrans.setText(vendor);
                        lblVersionOfTrans.setText(version);
                        lblDescriptionOfTrans.setText(description);
                        lblPortOfTrans.setText(port);
                    }
                    catch (Exception e1) {
                    }
                    finally {
                    }
                }
            });
            okButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    commit();
                }
            });
        }

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBackground(Color.DARK_GRAY);
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(null);
        }

        {
            if (isVisibleMidiOut == false) {
                tabbedPane.removeTabAt(0);
            }
            if (isVisibleMidiIn == false && isVisibleMidiOut == true) {
                tabbedPane.removeTabAt(1);
            }
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            if (JMPCore.isFinishedInitialize() == true) {
                if (JMPCore.getSoundManager().isPlay() == true) {
                    JMPCore.getSoundManager().stop();
                }
                if (JMPCore.isEnableStandAlonePlugin() == true || JMPFlags.LibraryMode == true) {
                    // スタンドアロン・ライブラリは非表示
                    chckbxStartupShowDialog.setVisible(false);
                }
                else {
                    chckbxStartupShowDialog.setVisible(true);
                }
                chckbxStartupShowDialog.setSelected(JMPCore.getDataManager().isShowStartupDeviceSetup());
            }
            updateLanguage();
        }
        super.setVisible(b);
    }

    @Override
    public void showWindow() {
        start();
    }

    @Override
    public void hideWindow() {
        close();
    }

    public void start() {
        if (JMPCore.isFinishedInitialize() == true) {
            if (JMPCore.getSoundManager().isPlay() == true) {
                JMPCore.getSoundManager().stop();
            }
        }

        JMPCore.getSoundManager().setCommitDeviceSelectAction(false);

        if (JMPFlags.DebugMode == true) {
            labelDevelop.setText("開発者モード");
        }
        else {
            labelDevelop.setText("");
        }

        LanguageManager lm = JMPCore.getLanguageManager();
        String itemListNameDefault = "● " + lm.getLanguageStr(LangID.Automatic_selection);
        String itemListNameNone = "● " + lm.getLanguageStr(LangID.Dont_choose_a_synthesizer);

        // レシーバー
        comboRecvMode.removeAllItems();
        infosOfRecv = JMPCore.getSoundManager().getMidiToolkit().getMidiDeviceInfo(false, true);
        comboRecvMode.addItem(itemListNameDefault);
        comboRecvMode.addItem(itemListNameNone);
        for (int i = 0; i < infosOfRecv.length; i++) {
            String line = createItemName(i, infosOfRecv[i].getName());
            comboRecvMode.addItem(line);
        }

        // トランスミッター
        comboTransMode.removeAllItems();
        infosOfTrans = JMPCore.getSoundManager().getMidiToolkit().getMidiDeviceInfo(true, false);
        comboTransMode.addItem("");
        for (int i = 0; i < infosOfTrans.length; i++) {
            String line = createItemName(i, infosOfTrans[i].getName());
            comboTransMode.addItem(line);
        }

        // 設定値を復元(MIDIOUT)
        {
            String saveInfoOfRecv = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT);
            if (saveInfoOfRecv.isEmpty() == false) {
                comboRecvMode.setSelectedIndex(0);
                if (saveInfoOfRecv.equals(SoundManager.NULL_RECEIVER_NAME) == true) {
                    comboRecvMode.setSelectedIndex(INDEX_OF_NONE);
                }
                else {
                    for (int i = 0; i < infosOfRecv.length; i++) {
                        if (saveInfoOfRecv.equals(infosOfRecv[i].getName()) == true) {
                            comboRecvMode.setSelectedIndex(i + NUMBER_OF_CUSTOM_SYNTH);
                            break;
                        }
                    }
                }
            }
            else {
                comboRecvMode.setSelectedIndex(INDEX_OF_AUTO_SELECTION);
            }
        }

        // 設定値を復元(MIDIIN)
        {
            String saveInfoOfTrans = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIIN);
            if (saveInfoOfTrans.isEmpty() == false) {
                comboTransMode.setSelectedIndex(0);
                for (int i = 0; i < infosOfTrans.length; i++) {
                    if (saveInfoOfTrans.equals(infosOfTrans[i].getName()) == true) {
                        comboTransMode.setSelectedIndex(i + 1);
                        break;
                    }
                }
            }
            else {
                comboTransMode.setSelectedIndex(0);
            }
        }

        setVisible(true);
    }

    protected void commit() {
        JMPCore.getSoundManager().setCommitDeviceSelectAction(true);

        JMPCore.getWindowManager().closeBuiltinSynthFrame();

        /* MIDI_OUT */
        String midiOutName = "";
        switch (comboRecvMode.getSelectedIndex()) {
            case INDEX_OF_AUTO_SELECTION:
                // 自動選択
                midiOutName = "";
                break;
            case INDEX_OF_NONE:
                // NULLシンセ
                midiOutName = SoundManager.NULL_RECEIVER_NAME;
                break;
            default:
                midiOutName = getOrgDeviceName(comboRecvMode.getSelectedItem().toString().trim());
                break;
        }

        /* MIDI_IN */
        String midiInName = "";
        switch (comboTransMode.getSelectedIndex()) {
            case 0:
                // 未選択
                midiInName = "";
                break;
            default:
                midiInName = getOrgDeviceName(comboTransMode.getSelectedItem().toString().trim());
                break;
        }

        // 設定値変更を行うことでSoundManagerに通知され、デバイスへの接続が実施される
        JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIOUT, midiOutName);
        JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIIN, midiInName);

        close();
    }

    /**
     * 閉じる
     */
    public void close() {
        this.setVisible(false);
    }

    private String createItemName(int index, String name) {
        String ret = String.format("%s%s %s", index + 1, NameNoSepareter, name);
        return ret;
    }

    protected int getOrgDeviceIndex(String name) {
        String devIndex = name;
        String[] sName = devIndex.split(NameNoSepareter);
        devIndex = sName[0].trim();
        int index = Utility.tryParseInt(devIndex, -1);
        if (index != -1) {
            index--;
        }
        return index;
    }

    protected String getOrgDeviceName(String name) {
        return name.substring(3);
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        WindowManager wm = JMPCore.getWindowManager();
        LanguageManager lm = JMPCore.getLanguageManager();
        setFont(wm.getCurrentFont(getFont()));
        setTitle(lm.getLanguageStr(LangID.MIDI_device_settings));
        wm.changeFont(chckbxStartupShowDialog, LangID.Whether_to_display_every_time_at_startup);
        wm.changeFont(comboRecvMode);
        wm.changeFont(comboTransMode);
    }

    @Override
    public void updateBackColor() {
        super.updateBackColor();
        this.setBackground(getJmpBackColor());
        chckbxStartupShowDialog.setBackground(getJmpBackColor());
    }
}
