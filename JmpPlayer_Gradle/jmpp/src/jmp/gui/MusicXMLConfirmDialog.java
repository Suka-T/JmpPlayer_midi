package jmp.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import function.Utility;
import jlib.gui.IJmpWindow;
import jmp.core.JMPCore;
import jmp.core.WindowManager;
import jmp.gui.ui.IJMPComponentUI;
import jmp.lang.DefineLanguage.LangID;

public class MusicXMLConfirmDialog extends JDialog implements IJmpWindow, IJMPComponentUI {

    private final JPanel contentPanel = new JPanel();

    private boolean isAutoAssignChannel = true;
    private boolean isAutoAssignProgramChange = true;
    private JCheckBox chckbxAutoAssignChannel;
    private JCheckBox chckbxAutoAssignProgramChange;
    private JPanel buttonPane;
    private JButton okButton;

    /**
     * Create the dialog.
     */
    public MusicXMLConfirmDialog() {
        setResizable(false);
        setModal(true);
        setBounds(100, 100, 450, 188);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JMPCore.getWindowManager().register(this);

        chckbxAutoAssignChannel = new JCheckBox("Automatically assign MIDI channel");
        chckbxAutoAssignChannel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        chckbxAutoAssignChannel.setBounds(8, 25, 318, 21);
        contentPanel.add(chckbxAutoAssignChannel);

        chckbxAutoAssignProgramChange = new JCheckBox("Automatically assign Program change number");
        chckbxAutoAssignProgramChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        chckbxAutoAssignProgramChange.setBounds(8, 76, 318, 21);
        contentPanel.add(chckbxAutoAssignProgramChange);
        {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        isAutoAssignChannel = chckbxAutoAssignChannel.isSelected();
                        isAutoAssignProgramChange = chckbxAutoAssignProgramChange.isSelected();
                        setVisible(false);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b == true) {
            updateBackColor();
            chckbxAutoAssignChannel.setSelected(isAutoAssignChannel);
            chckbxAutoAssignProgramChange.setSelected(isAutoAssignProgramChange);
        }
        super.setVisible(b);
    }

    public boolean isAutoAssignProgramChange() {
        return isAutoAssignProgramChange;
    }

    public void setAutoAssignProgramChange(boolean isAutoAssignProgramChange) {
        this.isAutoAssignProgramChange = isAutoAssignProgramChange;
    }

    public boolean isAutoAssignChannel() {
        return isAutoAssignChannel;
    }

    public void setAutoAssignChannel(boolean isAutoAssignChannel) {
        this.isAutoAssignChannel = isAutoAssignChannel;
    }

    @Override
    public void updateBackColor() {
        setBackground(getJmpBackColor());
        getContentPane().setBackground(getJmpBackColor());
        buttonPane.setBackground(getJmpBackColor());
        contentPanel.setBackground(getJmpBackColor());
        chckbxAutoAssignChannel.setBackground(getJmpBackColor());
        chckbxAutoAssignProgramChange.setBackground(getJmpBackColor());

        chckbxAutoAssignChannel.setForeground(Utility.getForegroundColor(getJmpBackColor()));
        chckbxAutoAssignProgramChange.setForeground(Utility.getForegroundColor(getJmpBackColor()));
    }

    @Override
    public void updateLanguage() {
        WindowManager wm = JMPCore.getWindowManager();
        wm.changeFont(chckbxAutoAssignChannel, LangID.Automatically_assign_MIDI_channel);
        wm.changeFont(chckbxAutoAssignProgramChange, LangID.Automatically_assign_Program_change_number);
    }

    @Override
    public void initializeLayout() {
    }

    @Override
    public void showWindow() {
    }

    @Override
    public void hideWindow() {
    }

    @Override
    public boolean isWindowVisible() {
        return false;
    }

    @Override
    public void setDefaultWindowLocation() {
    }

    @Override
    public void repaintWindow() {
        repaint();
    }
}
