package jmp.file;

import java.util.Map;

public class JmpFileBuilderFactory {

    public static final String BUILDER_TYPE_XML = "xml";
    public static final String BUILDER_TYPE_TEXT = "txt";

    private String type = BUILDER_TYPE_XML;

    public JmpFileBuilderFactory(String type) {
        this.type = type;
    }

    public IJmpFileBuilder createFileBuilder(Map<String, String> database, String[] keyset) {
        if (type.equalsIgnoreCase(BUILDER_TYPE_XML) == true) {
            return new JmpFileBuilderXml(database, keyset);
        }
        else {
            return new JmpFileBuilderText(database, keyset);
        }
    }

}
