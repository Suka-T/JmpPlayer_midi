package jmp.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import function.Platform;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.WindowManager;
import jmp.gui.ui.JMPDialog;
import jmp.lang.DefineLanguage.LangID;

public class VersionInfoDialog extends JMPDialog {

    private JButton btnLicense;
    private JButton btnClose;

    public VersionInfoDialog() {
        super();
        setResizable(false);
        setBounds(50, 60, 380, 180);
        setTitle("Version");
        getContentPane().setLayout(null);

        btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        btnClose.setBounds(260, 107, 91, 21);
        getContentPane().add(btnClose);

        JLabel lblVersionname = new JLabel("Version :");
        lblVersionname.setForeground(Color.WHITE);
        lblVersionname.setFont(UIManager.getFont("Menu.font"));
        lblVersionname.setHorizontalAlignment(SwingConstants.RIGHT);
        lblVersionname.setBounds(99, 33, 94, 13);
        getContentPane().add(lblVersionname);

        JLabel lblAppname = new JLabel("AppName :");
        lblAppname.setForeground(Color.WHITE);
        lblAppname.setFont(UIManager.getFont("Menu.font"));
        lblAppname.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAppname.setBounds(99, 10, 94, 13);
        getContentPane().add(lblAppname);

        JLabel lblAppDetail = new JLabel();
        lblAppDetail.setForeground(Color.WHITE);
        lblAppDetail.setFont(UIManager.getFont("Menu.font"));
        lblAppDetail.setBounds(205, 10, 122, 13);
        getContentPane().add(lblAppDetail);
        lblAppDetail.setText(JMPCore.APPLICATION_NAME);

        JLabel lblVerDetail = new JLabel();
        lblVerDetail.setForeground(Color.WHITE);
        lblVerDetail.setFont(UIManager.getFont("Menu.font"));
        lblVerDetail.setBounds(205, 33, 122, 13);
        getContentPane().add(lblVerDetail);
        lblVerDetail.setText(JMPCore.APPLICATION_VERSION);

        btnLicense = new JButton("License");
        btnLicense.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LicenseReaderDialog dialog = (LicenseReaderDialog) JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LICENSE);
                dialog.start();
            }
        });
        btnLicense.setBounds(157, 107, 91, 21);
        getContentPane().add(btnLicense);

        JLabel iconLabel = new JLabel("");
        iconLabel.setBounds(23, 10, 64, 64);

        Image image = JMPCore.getResourceManager().getJmpImageIcon();
        if (image != null) {
            iconLabel.setIcon(new ImageIcon(image));
        }
        getContentPane().add(iconLabel);

        JLabel lblJmplib = new JLabel("JMPLIB");
        lblJmplib.setForeground(Color.WHITE);
        lblJmplib.setFont(UIManager.getFont("Menu.font"));
        lblJmplib.setBounds(205, 61, 122, 13);
        lblJmplib.setText(JMPCore.LIBRALY_VERSION);
        getContentPane().add(lblJmplib);

        JLabel labelBuild = new JLabel("LIB Version :");
        labelBuild.setHorizontalAlignment(SwingConstants.RIGHT);
        labelBuild.setForeground(Color.WHITE);
        labelBuild.setFont(UIManager.getFont("Menu.font"));
        labelBuild.setBounds(99, 61, 94, 13);
        getContentPane().add(labelBuild);

        JLabel lblJava = new JLabel("Java Runtime :");
        lblJava.setHorizontalAlignment(SwingConstants.RIGHT);
        lblJava.setForeground(Color.WHITE);
        lblJava.setFont(UIManager.getFont("Menu.font"));
        lblJava.setBounds(99, 84, 94, 13);
        getContentPane().add(lblJava);

        JLabel lblJreVersion = new JLabel("0");
        lblJreVersion.setForeground(Color.WHITE);
        lblJreVersion.setFont(UIManager.getFont("Menu.font"));
        lblJreVersion.setBounds(205, 84, 122, 13);
        lblJreVersion.setText(Platform.getJavaVersion());
        getContentPane().add(lblJreVersion);

        if (JMPFlags.DebugMode == false) {
            lblJava.setVisible(false);
            lblJreVersion.setVisible(false);
        }
    }

    public void start() {
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();

        LanguageManager lm = JMPCore.getLanguageManager();
        WindowManager wm = JMPCore.getWindowManager();

        setTitle(lm.getLanguageStr(LangID.Version));
        setFont(wm.getCurrentFont(getFont()));
        wm.changeFont(btnLicense, LangID.License);
        wm.changeFont(btnClose, LangID.Close);
    }
}
