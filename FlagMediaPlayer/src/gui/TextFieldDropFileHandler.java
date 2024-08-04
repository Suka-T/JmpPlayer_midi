package gui;

import java.io.File;

import javax.swing.text.JTextComponent;

public class TextFieldDropFileHandler extends DropFileCallbackHandler {
    public TextFieldDropFileHandler(JTextComponent field) {
        super(new IDropFileCallback() {
            @Override
            public void catchDropFile(File file) {
                field.setText(file.getPath());
            }
        });
    }
}
