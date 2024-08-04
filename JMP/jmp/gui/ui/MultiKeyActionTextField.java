package jmp.gui.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.text.Document;

import function.Utility;

public class MultiKeyActionTextField extends JTextField {

    public MultiKeyActionTextField() {
        super();
        setMultiKeyAction();
    }

    public MultiKeyActionTextField(String text) {
        super(text);
        setMultiKeyAction();
    }

    public MultiKeyActionTextField(int columns) {
        super(columns);
        setMultiKeyAction();
    }

    public MultiKeyActionTextField(String text, int columns) {
        super(text, columns);
        setMultiKeyAction();
    }

    public MultiKeyActionTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        setMultiKeyAction();
    }

    public void setMultiKeyAction() {
        this.addKeyListener(new KeyListener() {

            boolean ctrlPressed = false;
            boolean cPressed = false;
            boolean vPressed = false;
            boolean xPressed = false;

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C:
                        cPressed = true;
                        break;
                    case KeyEvent.VK_V:
                        vPressed = true;
                        break;
                    case KeyEvent.VK_X:
                        xPressed = true;
                        break;
                    case KeyEvent.VK_CONTROL:
                        ctrlPressed = true;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C:
                        cPressed = false;
                        break;
                    case KeyEvent.VK_V:
                        vPressed = false;
                        break;
                    case KeyEvent.VK_X:
                        xPressed = false;
                        break;
                    case KeyEvent.VK_CONTROL:
                        ctrlPressed = false;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (ctrlPressed && cPressed) {
                    Utility.setClipboard(getText());
                }
                else if (ctrlPressed && vPressed) {
                    setText(Utility.getClipboardString());
                }
                else if (ctrlPressed && xPressed) {
                    Utility.setClipboard(new String(getText()));
                    setText("");
                }
            }
        });
    }

}
