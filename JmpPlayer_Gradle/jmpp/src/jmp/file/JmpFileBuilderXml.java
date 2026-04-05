package jmp.file;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jmp.core.JMPCore;

public class JmpFileBuilderXml implements IJmpFileBuilder {

    private static String JMP_XML_ROOT = "jmpp";
    private static String JMP_XML_INFO = "info";
    private static String JMP_XML_NAME = "name";
    private static String JMP_XML_VERSION = "version";
    private static String JMP_XML_DATA = "data";
    private static String JMP_XML_OBJ = "param";
    private static String JMP_XML_KEY = "key";

    private String[] keyset = null;
    private Map<String, String> database = null;

    private String appName = "Unknown";
    private String version = "Unknown";

    public JmpFileBuilderXml(Map<String, String> database, String[] keyset) {
        this.database = database;
        this.keyset = keyset;
    }

    @Override
    public boolean read(File file) {
        boolean ret = true;
        if (file.exists() == false) {
            return false;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            Element root = document.getDocumentElement();
            NodeList rootChildrens = root.getChildNodes();

            for (int i = 0; i < rootChildrens.getLength(); i++) {
                Node node = rootChildrens.item(i);
                if (node.getNodeName().equals(JMP_XML_INFO)) {
                    readNodeInfo(node);
                }
                else if (node.getNodeName().equals(JMP_XML_DATA)) {
                    readNodeData(node);
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    private void readNodeInfo(Node node) {
        NodeList lst = node.getChildNodes();
        for (int i = 0; i < lst.getLength(); i++) {
            if (lst.item(i).getNodeName().equals(JMP_XML_NAME) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                appName = el.getTextContent();
            }
            else if (lst.item(i).getNodeName().equals(JMP_XML_VERSION) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                version = el.getTextContent();
            }
        }
    }

    private void readNodeData(Node node) {
        NodeList lst = node.getChildNodes();
        for (int i = 0; i < lst.getLength(); i++) {
            if (lst.item(i).getNodeName().equals(JMP_XML_OBJ) == true) {
                Node objNode = lst.item(i);
                Element ele = (Element) objNode;

                String key = "";
                String value = "";
                if (ele.hasAttribute(JMP_XML_KEY) == true) {
                    key = ele.getAttribute(JMP_XML_KEY);
                    value = ele.getTextContent();
                }

                boolean isContainsKey = false;
                if (keyset == null) {
                    // キーセット未指定の場合は全て追加
                    isContainsKey = true;
                }
                else {
                    for (String ckey : keyset) {
                        if (key.equals(ckey) == true) {
                            isContainsKey = true;
                            break;
                        }
                    }
                }

                if (isContainsKey == true) {
                    database.put(key, value);
                }
            }
        }
    }

    @Override
    public boolean write(File file) {
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            return false;
        }
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement(JMP_XML_ROOT);
        document.appendChild(rootElement);

        /*
         * info
         */
        Element infoElement = document.createElement(JMP_XML_INFO);
        rootElement.appendChild(infoElement);
        // AppName
        Element infoNameElement = document.createElement(JMP_XML_NAME);
        infoNameElement.setTextContent(JMPCore.APPLICATION_NAME);
        infoElement.appendChild(infoNameElement);
        // バージョン
        Element infoNameVersionElement = document.createElement(JMP_XML_VERSION);
        infoNameVersionElement.setTextContent(String.valueOf(JMPCore.APPLICATION_VERSION));
        infoElement.appendChild(infoNameVersionElement);

        /*
         * data
         */
        Element dataElement = document.createElement(JMP_XML_DATA);
        rootElement.appendChild(dataElement);
        for (String key : database.keySet()) {
            String value = database.get(key);

            Element objElement = document.createElement(JMP_XML_OBJ);
            objElement.setAttribute(JMP_XML_KEY, key);
            objElement.setTextContent(value);
            dataElement.appendChild(objElement);
        }

        // Transformerインスタンスの生成
        Transformer transformer = null;
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            return false;
        }

        // Transformerの設定
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // 改行指定
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty("encoding", "UTF-8"); // エンコーディング

        // XMLファイルの作成
        try {
            transformer.transform(new DOMSource(document), new StreamResult(file));
        }
        catch (TransformerException e) {
            return false;
        }
        return true;
    }

    @Override
    public String getAppName() {
        return new String(appName);
    }

    @Override
    public String getVersion() {
        return new String(version);
    }

}
