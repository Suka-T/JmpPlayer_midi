package jmp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import function.Platform;
import function.Utility;
import jlib.gui.IJmpWindow;
import jmp.core.DataManager;
import jmp.core.JMPCommand;
import jmp.core.JMPCore;
import jmp.core.WindowManager;
import jmp.util.JmpUtil;

public class DebugLogConsole extends JDialog implements IJmpWindow {

    private static String SText = "";
    private final JPanel contentPanel = new JPanel();
    private JTextPane txtpnTest;
    private JScrollPane scrollPane;

    /**
     * Create the dialog.
     */
    public DebugLogConsole() {
        setTitle("Console");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 619, 443);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            {
                txtpnTest = new JTextPane();
                txtpnTest.setText("Test");
                txtpnTest.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
                txtpnTest.setEditable(false);
                scrollPane.setViewportView(txtpnTest);
                txtpnTest.setForeground(new Color(60, 179, 113));
                txtpnTest.setBackground(new Color(0, 0, 0));
                {
                    inpuTextField = new JTextField();
                    inpuTextField.addKeyListener(new KeyAdapter() {
                        String mnemLog = "";
                        int mnemIndex = -1;
                        
                        @Override
                        public void keyTyped(KeyEvent e) {
                        }
                        @Override
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                                int amount = e.getKeyCode() == KeyEvent.VK_UP ? 1 : -1;
                                if (mnemIndex == -1) {
                                    mnemIndex = 0;
                                    if (mnemLog.isEmpty() == false) {
                                        inpuTextField.setText(mnemLog);
                                        return;
                                    }
                                }
                                else {
                                    mnemIndex += amount;
                                    if (mnemIndex < 0) {
                                        mnemIndex = JMPCommand.SMnemonicList.size() - 1;
                                    }
                                    else if (mnemIndex >= JMPCommand.SMnemonicList.size()) {
                                        mnemIndex = 0;
                                    }
                                }
                                String cmd = JMPCommand.SMnemonicList.get(mnemIndex);
                                inpuTextField.setText(cmd);
                            }
                            else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                String cmd = inpuTextField.getText();
                                JMPCommand.parse(cmd);
                                mnemLog = cmd;
                                mnemIndex = -1;
                                inpuTextField.setText("");
                            }
                        }
                    });
                    inpuTextField.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
                    inpuTextField.setForeground(new Color(0, 100, 0));
                    contentPanel.add(inpuTextField, BorderLayout.SOUTH);
                    inpuTextField.setColumns(10);
                }
                DefaultCaret crList = (DefaultCaret) txtpnTest.getCaret();
                crList.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                {
                    JButton btnClear = new JButton("Clear");
                    btnClear.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            clearText();
                        }
                    });
                    buttonPane.add(btnClear);
                }
            }
        }
    }

    @Override
    public void setVisible(boolean b) {
        updateConfig(DataManager.CFG_KEY_LANGUAGE);
        updateText();
        super.setVisible(b);
        // if (b == false) {
        // clearText();
        // }
    }

    private static final boolean REVERSE = false;
    private static String LN = Platform.getNewLine();
    private JTextField inpuTextField;

    public static void print(String str) {
        addText(str, false, REVERSE);
    }

    public static void println(String str) {
        addText(str, true, REVERSE);
    }
    
    public static void clear() {
        SText = "";
    }

    public static void addText(String str, boolean ln, boolean reverse) {
        // if (isVisible() == false) {
        // return;
        // }
        //
        
        try {
            int addBites = str.getBytes("UTF-8").length;
            int currentBites = SText.getBytes("UTF-8").length;
            if (currentBites + addBites > 1024 * 3) {
                // 3M以上は記録しない
                SText = "";
            }
            
        }
        catch (Exception ex) {
            SText = "";
        }
        
        String text = SText;

        String s1, s2, s3;
        if (reverse == true) {
            s1 = str;
            s2 = ln ? LN : "";
            s3 = text;
        }
        else {
            s1 = text;
            s2 = str;
            s3 = ln ? LN : "";
        }
        String newText = Utility.stringsCombin(s1, s2, s3);
        SText = newText;
    }

    public void clearText() {
        SText = "";
        updateText();
    }

    public void updateText() {
        txtpnTest.setText(SText);
    }

    @Override
    public void showWindow() {
        this.setVisible(true);
    }

    @Override
    public void hideWindow() {
        this.setVisible(false);
    }

    @Override
    public boolean isWindowVisible() {
        return this.isVisible();
    }

    @Override
    public void setDefaultWindowLocation() {
    }

    @Override
    public void repaintWindow() {
        this.repaint();
    }
    
    @Override
    public void updateConfig(String key) {
        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_LANGUAGE) == true) {
            WindowManager wm = JMPCore.getWindowManager();
            txtpnTest.setFont(wm.getCurrentFont(txtpnTest.getFont()));
            inpuTextField.setFont(wm.getCurrentFont(inpuTextField.getFont()));
        }
        IJmpWindow.super.updateConfig(key);
    }

}
