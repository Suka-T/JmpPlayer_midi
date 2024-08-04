package jmsynth;

import java.io.File;
import java.util.HashMap;
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

import jmsynth.envelope.Envelope;
import jmsynth.modulate.Modulator;
import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.OscillatorConfig;
import jmsynth.oscillator.OscillatorFactory;
import jmsynth.oscillator.OscillatorSet;

public class JMSynthFile {

    public static final String EXTENSION_CONFIG = "jst";

    public static final String WAVE_STR_NONE = "NONE";
    public static final String WAVE_STR_SAW = OscillatorFactory.OSCILLATOR_NAME_SAW;
    public static final String WAVE_STR_TRIANGLE = OscillatorFactory.OSCILLATOR_NAME_TRIANGLE;
    public static final String WAVE_STR_SQUARE = OscillatorFactory.OSCILLATOR_NAME_SQUARE;
    public static final String WAVE_STR_PULSE_25 = OscillatorFactory.OSCILLATOR_NAME_PULSE25;
    public static final String WAVE_STR_PULSE_12_5 = OscillatorFactory.OSCILLATOR_NAME_PULSE125;
    public static final String WAVE_STR_SINE = OscillatorFactory.OSCILLATOR_NAME_SINE;
    public static final String WAVE_STR_LOW_SINE = OscillatorFactory.OSCILLATOR_NAME_SINE;
    public static final String WAVE_STR_LONG_NOIS = OscillatorFactory.OSCILLATOR_NAME_NOISE_L;
    public static final String WAVE_STR_SHORT_NOIS = OscillatorFactory.OSCILLATOR_NAME_NOISE_S;

    public static final String[] WAVE_STR_ITEMS = new String[] { //
            WAVE_STR_NONE, //
            WAVE_STR_SINE, //
            WAVE_STR_SAW, //
            WAVE_STR_TRIANGLE, //
            WAVE_STR_SQUARE, //
            WAVE_STR_PULSE_25, //
            WAVE_STR_PULSE_12_5, //
            WAVE_STR_LONG_NOIS, //
            WAVE_STR_SHORT_NOIS //
    };//
    public static final String[] WAVE_STR_ITEMS_TONE_ONRY = new String[] { //ノイズ無し 
            WAVE_STR_NONE, //
            WAVE_STR_SINE, //
            WAVE_STR_SAW, //
            WAVE_STR_TRIANGLE, //
            WAVE_STR_SQUARE, //
            WAVE_STR_PULSE_25, //
            WAVE_STR_PULSE_12_5, //
    };//
    public static final String[] WAVE_STR_ITEMS_NOISE_ONRY = new String[] { //ノイズのみ 
            WAVE_STR_LONG_NOIS, //
            WAVE_STR_SHORT_NOIS //
    };//
    
    private static OscillatorFactory sOscFc = new OscillatorFactory();
    static Map<String, IOscillator> oscMap = new HashMap<String, IOscillator>() {
        {
            put(WAVE_STR_NONE, null);
            put(WAVE_STR_SINE, sOscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_SINE));
            put(WAVE_STR_SAW, sOscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_SAW));
            put(WAVE_STR_TRIANGLE, sOscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
            put(WAVE_STR_SQUARE, sOscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_SQUARE));
            put(WAVE_STR_PULSE_25, sOscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_PULSE25));
            put(WAVE_STR_PULSE_12_5, sOscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_PULSE125));
            put(WAVE_STR_LONG_NOIS, sOscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
            put(WAVE_STR_SHORT_NOIS, sOscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_NOISE_S));
        }
    };

    public static IOscillator toOscillator(String sWave) {
        IOscillator type = oscMap.get(sWave);
        return type;
    }

    public static String toWaveStr(IOscillator type) {
        String sWave = WAVE_STR_LONG_NOIS;
        if (type == null) {
            return "";
        }
        
        for (String s : oscMap.keySet()) {
            IOscillator o = oscMap.get(s);
            if (o.getOscillatorName().equals(type.getOscillatorName()) == true) {
                return s;
            }
        }
        return sWave;
    }

    // グラフィック用
    public static int toYCord(IOscillator type, double f, int overallLeval, boolean isReverse) {
        int y = -1;
        OscillatorConfig oscConfig = new OscillatorConfig();
        oscConfig.setWaveReverse(isReverse);
        y = type.makeWave(f, overallLeval, oscConfig);
        return y;
    }

    private static final String S_TRUE = "TRUE";
    private static final String S_FALSE = "FALSE";

    public static final String XML_NODE_ROOT = "jmsynth";
    public static final String XML_NODE_INFO = "info";
    public static final String XML_NODE_INFO_NAME = "name";
    public static final String XML_NODE_INFO_VERSION = "version";
    public static final String XML_NODE_INFO_NUMOFCHANNEL = "num_of_channel";
    public static final String XML_NODE_SOUNDSOURCE = "sound_source";
    public static final String XML_NODE_CHANNEL = "channel";
    public static final String XML_ATTR_CHANNEL = "no";
    public static final String XML_NODE_WAVE = "wave";
    public static final String XML_ATTR_TYPE = "type";
    public static final String XML_ATTR_REVERSE = "reverse";
    public static final String XML_ATTR_NESSIM = "nes_sim";
    public static final String XML_NODE_ENVELOPE = "envelope";
    public static final String XML_ATTR_A = "a";
    public static final String XML_ATTR_MAX_A = "ma";
    public static final String XML_ATTR_D = "d";
    public static final String XML_ATTR_MAX_D = "md";
    public static final String XML_ATTR_S = "s";
    public static final String XML_ATTR_R = "r";
    public static final String XML_ATTR_MAX_R = "mr";
    public static final String XML_NODE_MOD = "modulator";
    public static final String XML_ATTR_DEPTH = "depth";

    public static boolean saveSynthConfig(File file, JMSoftSynthesizer synth) {
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            return false;
        }
        Document document = documentBuilder.newDocument();

        Element configElement = document.createElement(XML_NODE_ROOT);
        document.appendChild(configElement);

        /*
         * info
         */
        Element infoElement = document.createElement(XML_NODE_INFO);
        configElement.appendChild(infoElement);
        apendInfoElement(document, infoElement, synth);

        /*
         * soundsouce
         */
        Element soundSourceElement = document.createElement(XML_NODE_SOUNDSOURCE);
        configElement.appendChild(soundSourceElement);
        apendSoundSourceElement(document, soundSourceElement, synth);

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

    private static void apendInfoElement(Document document, Element rootElement, JMSoftSynthesizer synth) {
        // シンセ名
        Element infoNameElement = document.createElement(XML_NODE_INFO_NAME);
        infoNameElement.setTextContent(String.valueOf(JMSoftSynthesizer.INFO_NAME));
        rootElement.appendChild(infoNameElement);
        // バージョン
        Element infoNameVersionElement = document.createElement(XML_NODE_INFO_VERSION);
        infoNameVersionElement.setTextContent(String.valueOf(JMSoftSynthesizer.INFO_VERSION));
        rootElement.appendChild(infoNameVersionElement);
        // チャンネル数
        Element infoNumOfChannelElement = document.createElement(XML_NODE_INFO_NUMOFCHANNEL);
        infoNumOfChannelElement.setTextContent(String.valueOf(synth.getNumberOfChannel()));
        rootElement.appendChild(infoNumOfChannelElement);
    }

    private static void apendSoundSourceElement(Document document, Element rootElement, JMSoftSynthesizer synth) {
        // channel
        for (int ch = 0; ch < synth.getNumberOfChannel(); ch++) {
            Element channelElement = document.createElement(XML_NODE_CHANNEL);
            channelElement.setAttribute(XML_ATTR_CHANNEL, String.valueOf(ch));
            rootElement.appendChild(channelElement);

            apendSynthElement(document, channelElement, synth.getOscillatorSet(ch), synth.isWaveReverse(ch), synth.isValidNesSimulate(ch), synth.getEnvelope(ch), synth.getModulator(ch));
        }
    }

    private static void apendSynthElement(Document document, Element rootElement, OscillatorSet waveSet, boolean waveReverse, boolean nesSim, Envelope env, Modulator mod) {
        // wave
        Element waveElement = document.createElement(XML_NODE_WAVE);
        
        String str = "";
        for (int i = 0; i < waveSet.size(); i++) {
            if (i != 0) {
                str += ",";
            }
            str += toWaveStr(waveSet.getOscillator(i));
        }
        waveElement.setAttribute(XML_ATTR_TYPE, str);
        waveElement.setAttribute(XML_ATTR_REVERSE, waveReverse ? S_TRUE : S_FALSE);
        waveElement.setAttribute(XML_ATTR_NESSIM, nesSim ? S_TRUE : S_FALSE);
        rootElement.appendChild(waveElement);

        // envelope
        Element envElement = document.createElement(XML_NODE_ENVELOPE);
        envElement.setAttribute(XML_ATTR_A, String.valueOf(env.getAttackTime()));
        envElement.setAttribute(XML_ATTR_MAX_A, String.valueOf(env.getMaxAttackMills()));
        envElement.setAttribute(XML_ATTR_D, String.valueOf(env.getDecayTime()));
        envElement.setAttribute(XML_ATTR_MAX_D, String.valueOf(env.getMaxDecayMills()));
        envElement.setAttribute(XML_ATTR_S, String.valueOf(env.getSustainLevel()));
        envElement.setAttribute(XML_ATTR_R, String.valueOf(env.getReleaseTime()));
        envElement.setAttribute(XML_ATTR_MAX_R, String.valueOf(env.getMaxReleaseMills()));
        rootElement.appendChild(envElement);

        // mod
        Element modElement = document.createElement(XML_NODE_MOD);
        modElement.setAttribute(XML_ATTR_DEPTH, String.valueOf(mod.getDepth()));
        rootElement.appendChild(modElement);
    }

    public static boolean loadSynthConfig(File file, JMSoftSynthesizer synth) {
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
                if (node.getNodeName().equals(XML_NODE_INFO)) {
                    readNodeInfo(node, synth);
                }
                else if (node.getNodeName().equals(XML_NODE_SOUNDSOURCE)) {
                    readNodeSoundSource(node, synth);
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;

    }

    private static void readNodeInfo(Node node, JMSoftSynthesizer synth) {
        NodeList lst = node.getChildNodes();
        for (int i = 0; i < lst.getLength(); i++) {
            if (lst.item(i).getNodeName().equals(XML_NODE_INFO_NAME) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                System.out.println("info_name " + el.getTextContent());
            }
            else if (lst.item(i).getNodeName().equals(XML_NODE_INFO_VERSION) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                System.out.println("info_version " + el.getTextContent());
            }
            else if (lst.item(i).getNodeName().equals(XML_NODE_INFO_NUMOFCHANNEL) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                System.out.println("num_of_channel " + el.getTextContent());
            }
        }
    }

    private static void readNodeSoundSource(Node node, JMSoftSynthesizer synth) {
        NodeList lst = node.getChildNodes();
        for (int i = 0; i < lst.getLength(); i++) {
            if (lst.item(i).getNodeName().equals(XML_NODE_CHANNEL) == true) {
                Node chNode = lst.item(i);
                readNodeChannel(chNode, synth);
            }
        }
    }
    
    public static OscillatorSet parseOscText(String str) {
        String[] ss = str.split(",");
        
        OscillatorSet oscSet = new OscillatorSet();
        for (int i  = 0; i < ss.length; i++) {
            IOscillator waveType = toOscillator(ss[i]);
            if (waveType == null) {
            }
            else if (waveType.getOscillatorName() == WAVE_STR_LONG_NOIS || waveType.getOscillatorName() == WAVE_STR_SHORT_NOIS) {
                return new OscillatorSet(waveType);
            }
            else {
                oscSet.addOscillators(waveType);
            }
        }
        return oscSet;
    }

    private static void readNodeChannel(Node node, JMSoftSynthesizer synth) {
        if (!(node instanceof Element)) {
            return;
        }

        int ch = -1;
        Element channelElement = (Element) node;
        if (channelElement.hasAttribute(XML_ATTR_CHANNEL) == true) {
            ch = Integer.valueOf(channelElement.getAttribute(XML_ATTR_CHANNEL));
        }

        if (ch < 0 || ch >= synth.getNumberOfChannel()) {
            return;
        }

        NodeList chLst = channelElement.getChildNodes();
        for (int i = 0; i < chLst.getLength(); i++) {
            Node chChild = chLst.item(i);
            if (!(chChild instanceof Element)) {
                continue;
            }

            if (chChild.getNodeName().equals(XML_NODE_WAVE)) {
                Element waveElement = (Element) chChild;
                if (waveElement.hasAttribute(XML_ATTR_TYPE) == true) {
                    synth.clearOscillator(ch);
                    
                    OscillatorSet oscSet = parseOscText(waveElement.getAttribute(XML_ATTR_TYPE));
                    synth.setOscillator(ch, oscSet);
                }
                if (waveElement.hasAttribute(XML_ATTR_REVERSE) == true) {
                    synth.setWaveReverse(ch, waveElement.getAttribute(XML_ATTR_REVERSE).equalsIgnoreCase(S_TRUE) ? true : false);
                }
                if (waveElement.hasAttribute(XML_ATTR_NESSIM) == true) {
                    synth.setValidNesSimulate(ch, waveElement.getAttribute(XML_ATTR_NESSIM).equalsIgnoreCase(S_TRUE) ? true : false);
                }
            }
            else if (chChild.getNodeName().equals(XML_NODE_ENVELOPE)) {
                Element envElement = (Element) chChild;
                Envelope env = synth.getEnvelope(ch);
                if (envElement.hasAttribute(XML_ATTR_A) == true) {
                    env.setAttackTime(Double.valueOf(envElement.getAttribute(XML_ATTR_A)));
                }
                if (envElement.hasAttribute(XML_ATTR_MAX_A) == true) {
                    env.setMaxAttackMills(Integer.valueOf(envElement.getAttribute(XML_ATTR_MAX_A)));
                }
                if (envElement.hasAttribute(XML_ATTR_D) == true) {
                    env.setDecayTime(Double.valueOf(envElement.getAttribute(XML_ATTR_D)));
                }
                if (envElement.hasAttribute(XML_ATTR_MAX_D) == true) {
                    env.setMaxDecayMills(Integer.valueOf(envElement.getAttribute(XML_ATTR_MAX_D)));
                }
                if (envElement.hasAttribute(XML_ATTR_S) == true) {
                    env.setSustainLevel(Double.valueOf(envElement.getAttribute(XML_ATTR_S)));
                }
                if (envElement.hasAttribute(XML_ATTR_R) == true) {
                    env.setReleaseTime(Double.valueOf(envElement.getAttribute(XML_ATTR_R)));
                }
                if (envElement.hasAttribute(XML_ATTR_MAX_R) == true) {
                    env.setMaxReleaseMills(Integer.valueOf(envElement.getAttribute(XML_ATTR_MAX_R)));
                }
            }
            else if (chChild.getNodeName().equals(XML_NODE_MOD)) {
                Element modElement = (Element) chChild;
                Modulator mod = synth.getModulator(ch);
                if (modElement.hasAttribute(XML_ATTR_DEPTH) == true) {
                    mod.setDepth(Integer.valueOf(modElement.getAttribute(XML_ATTR_DEPTH)));
                }
            }
        }
    }
}
