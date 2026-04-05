package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import function.Utility;
import jlib.midi.MidiByte;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;

public class MidiDataTransportDialog extends JMPDialog {

    private static final int POPUP_LIMIT_WIDTH = 300;

    private final JPanel contentPanel = new JPanel();
    private JTextField textField;
    private JLabel labelInfo;
    private JComboBox<String> comboBoxCommand;
    private JComboBox<String> comboBoxChannel;
    private JComboBox<String> comboBoxData1;
    private JComboBox<String> comboBoxData2;
    private JLabel lblCommand;
    private JLabel lblChannel;
    private JLabel lblData;
    private JLabel lblData_1;
    private JLabel lblMidihex;
    private JButton sendButton;

    public class CstmPopupMenuListener implements PopupMenuListener {

        private boolean adjust = false;

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            @SuppressWarnings("unchecked")
            JComboBox<String> cmb = (JComboBox<String>) e.getSource();
            Dimension dim = cmb.getSize();
            if (dim.width >= POPUP_LIMIT_WIDTH) {
                return;
            }
            if (adjust == false) {
                adjust = true;
                cmb.setSize(POPUP_LIMIT_WIDTH, dim.height);
                cmb.showPopup();
            }
            cmb.setSize(dim);
            adjust = false;
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }

    }

    /**
     * Create the dialog.
     */
    public MidiDataTransportDialog() {
        super();
        setTitle("MIDIメッセージセンダー");
        setResizable(false);
        setBounds(100, 100, 450, 240);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(getJmpBackColor());
        contentPanel.setBackground(getJmpBackColor());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setJmpIcon();
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        textField = new JTextField();
        textField.addInputMethodListener(new InputMethodListener() {
            public void caretPositionChanged(InputMethodEvent event) {
            }

            public void inputMethodTextChanged(InputMethodEvent event) {
            }
        });
        textField.setBounds(6, 136, 418, 19);
        contentPanel.add(textField);
        textField.setColumns(10);

        lblMidihex = new JLabel("MIDIデータバイト（Hex）");
        lblMidihex.setForeground(Color.WHITE);
        lblMidihex.setBounds(6, 113, 297, 13);
        contentPanel.add(lblMidihex);

        labelInfo = new JLabel("   ");
        labelInfo.setBounds(12, 174, 297, 13);
        contentPanel.add(labelInfo);
        {
            sendButton = new JButton("Send");
            sendButton.setBounds(319, 165, 105, 21);
            contentPanel.add(sendButton);
            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    send();
                }
            });
        }

        comboBoxCommand = new JComboBox<String>();
        comboBoxCommand.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                changedCommandCombo();
            }
        });
        comboBoxCommand.addPopupMenuListener(new CstmPopupMenuListener());
        comboBoxCommand.setBounds(6, 43, 100, 19);
        contentPanel.add(comboBoxCommand);

        comboBoxChannel = new JComboBox<String>();
        comboBoxChannel.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                genMessageString();
            }
        });
        comboBoxChannel.setBounds(112, 43, 100, 19);
        contentPanel.add(comboBoxChannel);

        comboBoxData1 = new JComboBox<String>();
        comboBoxData1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                genMessageString();
            }
        });
        comboBoxData1.setEditable(true);
        comboBoxData1.setBounds(218, 43, 100, 19);
        comboBoxData1.addPopupMenuListener(new CstmPopupMenuListener());
        contentPanel.add(comboBoxData1);

        comboBoxData2 = new JComboBox<String>();
        comboBoxData2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                genMessageString();
            }
        });
        comboBoxData2.setEditable(true);
        comboBoxData2.setBounds(324, 43, 100, 19);
        comboBoxData2.addPopupMenuListener(new CstmPopupMenuListener());
        contentPanel.add(comboBoxData2);

        lblCommand = new JLabel("Command");
        lblCommand.setForeground(Color.WHITE);
        lblCommand.setBounds(6, 22, 100, 19);
        contentPanel.add(lblCommand);

        lblChannel = new JLabel("Channel");
        lblChannel.setForeground(Color.WHITE);
        lblChannel.setBounds(112, 22, 100, 19);
        contentPanel.add(lblChannel);

        lblData = new JLabel("Data1");
        lblData.setForeground(Color.WHITE);
        lblData.setBounds(218, 22, 100, 19);
        contentPanel.add(lblData);

        lblData_1 = new JLabel("Data2");
        lblData_1.setForeground(Color.WHITE);
        lblData_1.setBounds(324, 22, 100, 19);
        contentPanel.add(lblData_1);

        updateAllComboBox();

    }

    private void updateAllComboBox() {
        comboBoxCommand.removeAllItems();
        comboBoxCommand.addItem("");
        for (String item : MidiByte.cloneCommandIdentList(true)) {
            comboBoxCommand.addItem(item);
        }
        comboBoxCommand.setSelectedIndex(0);

        comboBoxChannel.removeAllItems();
        for (int i = 1; i <= 16; i++) {
            comboBoxChannel.addItem("" + i);
        }
        comboBoxChannel.setSelectedIndex(0);

        comboBoxData1.setSelectedItem("0");
        comboBoxData2.setSelectedItem("0");

        comboBoxChannel.setEnabled(false);
        comboBoxData1.setEnabled(false);
        comboBoxData2.setEnabled(false);
    }

    private void changedCommandCombo() {
        String sCommand = comboBoxCommand.getSelectedItem().toString();
        if (sCommand.isEmpty() == true) {
            comboBoxChannel.setEnabled(false);
            comboBoxData1.setEnabled(false);
            comboBoxData2.setEnabled(false);
            return;
        }
        comboBoxData1.setEnabled(true);
        comboBoxData2.setEnabled(true);

        comboBoxData1.removeAllItems();
        comboBoxData2.removeAllItems();

        comboBoxData1.setSelectedItem("0");
        comboBoxData2.setSelectedItem("0");

        int command = MidiByte.convertChannelCommandStringToByte(sCommand);
        if (MidiByte.isChannelMessage(command) == true) {
            comboBoxChannel.setEnabled(true);
            if (command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON || command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF) {
                for (String item : MidiByte.cloneNoteNumberIdentList(true)) {
                    comboBoxData1.addItem(item);
                }

                if (command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON) {
                    comboBoxData2.setSelectedItem("80");
                }
                else if (command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF) {
                    comboBoxData2.setSelectedItem("0");
                }
            }
            else if (command == MidiByte.Status.Channel.ChannelVoice.Fst.CONTROL_CHANGE) {
                for (String item : MidiByte.cloneControlChangeIdentList(true)) {
                    comboBoxData1.addItem(item);
                }
            }
        }
        else {
            comboBoxChannel.setEnabled(false);
        }
        genMessageString();
    }

    private void genMessageString() {
        String sCommand = comboBoxCommand.getSelectedItem().toString();
        if (sCommand.isEmpty() == true) {
            return;
        }

        if (comboBoxData1.getSelectedItem() == null || comboBoxData2.getSelectedItem() == null) {
            return;
        }

        String mes = "";
        String sChannel = comboBoxChannel.getSelectedItem().toString();
        String sData1 = comboBoxData1.getSelectedItem().toString();
        String sData2 = comboBoxData2.getSelectedItem().toString();

        sData1 = sData1.isEmpty() ? "NN" : sData1;
        sData2 = sData2.isEmpty() ? "NN" : sData2;

        int data1 = Utility.tryParseInt(sData1, 0);
        int data2 = Utility.tryParseInt(sData2, 0);
        int command = MidiByte.convertChannelCommandStringToByte(sCommand);
        if (MidiByte.isChannelMessage(command) == true) {
            switch (command) {
                case MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON:
                case MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF: {
                    int tmp = MidiByte.convertNoteStringToByte(sData1);
                    if (tmp != -1) {
                        data1 = tmp;
                    }
                    break;
                }
                case MidiByte.Status.Channel.ChannelVoice.Fst.CONTROL_CHANGE: {
                    int tmp = MidiByte.convertControlChangeStringToByte(sData1);
                    if (tmp != -1) {
                        data1 = tmp;
                    }
                    break;
                }
                default:
                    break;
            }

            // チャンネルバイトのマージ
            int channel = Utility.tryParseInt(sChannel, -1);
            if (channel != -1) {
                // 0基点
                channel--;
            }
            else {
                // コンバート失敗
                channel = 0;
            }
            command |= channel;
        }

        String s = "";
        s = Integer.toHexString(command);
        mes += (s.length() == 1) ? ("0" + s) : s;
        if (sData1.equalsIgnoreCase("NN") == false) {
            s = Integer.toHexString(data1);
            s = (s.length() == 1) ? ("0" + s) : s;
        }
        else {
            s = sData1;
        }
        mes += s;
        if (sData2.equalsIgnoreCase("NN") == false) {
            s = Integer.toHexString(data2);
            s = (s.length() == 1) ? ("0" + s) : s;
        }
        else {
            s = sData2;
        }
        mes += s;

        textField.setText(mes);
    }

    private void send() {
        LanguageManager lm = JMPCore.getLanguageManager();
        String sBin = textField.getText();
        if (sBin.isEmpty() == false) {
            byte[] data = Utility.tryParseHexBinary(sBin);
            int length = data.length;
            if (data.length <= 0) {
                labelInfo.setForeground(Color.RED);
                labelInfo.setText(lm.getLanguageStr(LangID.Invalid_byte_data));
            }
            else {
                int ch = MidiByte.getChannel(data, length);
                int status = MidiByte.getStatus(data, length);
                int data1 = MidiByte.getData1(data, length);
                int data2 = MidiByte.getData2(data, length);
                labelInfo.setForeground(Color.WHITE);
                String str = String.format("ch=%d, stat=%d, data1=%d, data2=%d,", ch + 1, status, data1, data2);
                labelInfo.setText(str);

                JMPCore.getSoundManager().getMidiController().sendMidiMessage(data, 0);

                if (MidiByte.getCommand(data, data.length) == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON) {
                    // 対になるNoteOffに変更する
                    String sChannel = comboBoxChannel.getSelectedItem().toString();
                    String sData1 = comboBoxData1.getSelectedItem().toString();
                    changedCommandCombo();
                    comboBoxCommand.setSelectedItem(MidiByte.convertByteToChannelCommandString(MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF));
                    comboBoxChannel.setSelectedItem(sChannel);
                    comboBoxData1.setSelectedItem(sData1);
                    comboBoxData2.setSelectedItem("0");
                }
            }
        }
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        WindowManager wm = JMPCore.getWindowManager();
        LanguageManager lm = JMPCore.getLanguageManager();
        setTitle(lm.getLanguageStr(LangID.MIDI_message_sender));
        setFont(wm.getCurrentFont(getFont()));
        wm.changeFont(lblMidihex, LangID.MIDI_Data_byte);
        wm.changeFont(sendButton, LangID.Send);
    }
}
