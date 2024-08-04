package jmp.convert.musicxml;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jlib.midi.IMidiToolkit;
import jmp.convert.IJMPDocumentReader;
import jmp.convert.musicxml.MusicXMLElement.TiedType;
import jmp.core.JMPCore;
import jmp.util.JmpUtil;

public class MusicXMLReader implements IJMPDocumentReader {

    public static final int DEFAULT_BASE_OCTAVE = 4;

    private boolean loadResult = false;
    private File file = null;
    private MusicXML musicXML = null;

    private boolean isAutoAssignChannel = true;
    private boolean isAutoAssignProgramChange = true;

    public MusicXMLReader() {
        musicXML = new MusicXML();
    }

    public void load() throws SAXException, IOException, ParserConfigurationException {

        loadResult = false;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); // DTDエラー対処
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        Element root = document.getDocumentElement();
        readRootNodeList(root);

        if (musicXML.getParts().size() > 0) {
            loadResult = true;
        }

        // printResult();
    }

    public void load(File file) throws SAXException, IOException, ParserConfigurationException {
        this.file = file;
        load();
    }

    @SuppressWarnings("unused")
    private void printResult() {
        MusicXMLPartList partList = musicXML.getPartList();
        for (MusicXMLScorePart sp : partList.getScorePart()) {
            System.out.println("---- PART_ID(" + sp.getPartID() + ") ----");
            System.out.println(" :Port = " + sp.getPort());
            System.out.println(" :Channel = " + sp.getChannel());
            System.out.println(" :Program = " + sp.getProgram());
            System.out.println(" :Volume = " + sp.getVolume());
            System.out.println(" :Pan = " + sp.getPan());
        }

        for (int i = 0; i < musicXML.partSize(); i++) {
            MusicXMLPart part = musicXML.getPart(i);
            for (MusicXMLMeasure meas : part.getMeasures()) {
                System.out.println("---- MEASURE(" + meas.getNumber() + ") ----");
                for (MusicXMLElement element : meas.getElements()) {
                    if (element instanceof MusicXMLAttributes) {
                        MusicXMLAttributes attr = (MusicXMLAttributes) element;
                        System.out.println(" ● Attributes");
                        System.out.println(" :Divisions = " + attr.getDivisions());
                        System.out.println(" :Fifths = " + attr.getFifths());
                        System.out.println(" :Mode = " + attr.getMode());
                        System.out.println(" :Staves = " + attr.getStaves());
                        System.out.println(" :Beats = " + attr.getBeats());
                        System.out.println(" :BeatsType = " + attr.getBeatsType());
                    }
                    else if (element instanceof MusicXMLDirection) {
                        MusicXMLDirection dir = (MusicXMLDirection) element;
                        System.out.println(" ● Direction");
                        System.out.println(" :Tempo = " + dir.getTempo());
                    }
                    else if (element instanceof MusicXMLNote) {
                        MusicXMLNote note = (MusicXMLNote) element;
                        System.out.println(" ● Note");
                        System.out.println(" :Duration = " + note.getDuration());
                        if (note.isRest() == false) {
                            System.out.println(" :Step = " + note.getStep());
                            System.out.println(" :Octave = " + note.getOctave());
                        }
                        else {
                            System.out.println(" :Rest");
                        }
                        System.out.println(" :Accidental = " + note.getAccidental());
                        System.out.println(" :Type = " + note.getType());
                        System.out.println(" :Stem = " + note.getStem());
                        System.out.println(" :Staff = " + note.getStaff());
                        System.out.println(" :Voice = " + note.getVoice());
                    }
                }
            }
        }
    }

    public MusicXML getMusicXML() {
        return musicXML;
    }

    private void readRootNodeList(Element root) {
        NodeList scorePartwiseNodeList = root.getChildNodes();
        for (int i = 0; i < scorePartwiseNodeList.getLength(); i++) {
            Node node = scorePartwiseNodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getNodeName().equals("part") == true) {
                    MusicXMLPart mxmlPart = readPartNodeList(element);
                    musicXML.addPart(mxmlPart);
                }
                else if (element.getNodeName().equals("part-list") == true) {
                    MusicXMLPartList mxmlPartList = readPartListNodeList(element);
                    musicXML.setPartList(mxmlPartList);
                }
            }
        }
    }

    private MusicXMLPartList readPartListNodeList(Element element) {
        MusicXMLPartList mxmlPartList = new MusicXMLPartList();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("score-part") == true) {
                    MusicXMLScorePart musicXMLScorePart = readScoreListNodeList(childElement);
                    mxmlPartList.addScorePart(musicXMLScorePart);
                }
            }
        }
        return mxmlPartList;
    }

    private MusicXMLScorePart readScoreListNodeList(Element element) {
        String id = element.getAttribute("id");
        MusicXMLScorePart musicXMLScorePart = new MusicXMLScorePart(id);
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("midi-device") == true) {
                    musicXMLScorePart.setPort(childElement.getAttribute("port"));
                }
                else if (childElement.getNodeName().equals("midi-instrument") == true) {
                    NodeList nodeList2 = childElement.getChildNodes();
                    for (int i2 = 0; i2 < nodeList2.getLength(); i2++) {
                        Node node2 = nodeList2.item(i2);
                        if (node2.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement2 = (Element) node2;
                            if (childElement2.getNodeName().equals("midi-channel") == true) {
                                musicXMLScorePart.setChannel(childElement2.getTextContent());
                            }
                            else if (childElement2.getNodeName().equals("midi-program") == true) {
                                musicXMLScorePart.setProgram(childElement2.getTextContent());
                            }
                            else if (childElement2.getNodeName().equals("volume") == true) {
                                musicXMLScorePart.setVolume(childElement2.getTextContent());
                            }
                            else if (childElement2.getNodeName().equals("pan") == true) {
                                musicXMLScorePart.setPan(childElement2.getTextContent());
                            }
                        }
                    }
                }
            }
        }
        return musicXMLScorePart;
    }

    private MusicXMLPart readPartNodeList(Element element) {
        MusicXMLPart mxmlPart = new MusicXMLPart(element.getAttribute("id"));
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("measure") == true) {
                    MusicXMLMeasure mxmlMeasure = readMeasureNodeList(childElement);
                    mxmlPart.addMeasure(mxmlMeasure);
                }
            }
        }
        return mxmlPart;
    }

    private MusicXMLMeasure readMeasureNodeList(Element element) {
        int number = JmpUtil.toInt(element.getAttribute("number"), -1);
        MusicXMLMeasure mxmlMeasure = new MusicXMLMeasure(number);
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("attributes") == true) {
                    mxmlMeasure.addElement(readAttributesNodeList(childElement));
                }
                else if (childElement.getNodeName().equals("direction") == true) {
                    mxmlMeasure.addElement(readDirectionNodeList(childElement));
                }
                else if (childElement.getNodeName().equals("note") == true) {
                    mxmlMeasure.addElement(readNoteNodeList(childElement));
                }
                else if (childElement.getNodeName().equals("backup") == true) {
                    mxmlMeasure.addElement(readBackupNodeList(childElement));
                }
            }
        }
        return mxmlMeasure;
    }

    private MusicXMLDirection readDirectionNodeList(Element element) {
        MusicXMLDirection mxmlDirection = new MusicXMLDirection();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("sound") == true) {
                    // sound element
                    mxmlDirection.setTempo(childElement.getAttribute("tempo"));
                }
            }
        }
        return mxmlDirection;
    }

    private MusicXMLAttributes readAttributesNodeList(Element element) {
        MusicXMLAttributes mxmlAttributes = new MusicXMLAttributes();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("divisions") == true) {
                    // divisions element
                    mxmlAttributes.setDivisions(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("key") == true) {
                    // key element
                    NodeList keyNodeList = childElement.getChildNodes();
                    for (int j = 0; j < keyNodeList.getLength(); j++) {
                        Node keyNode = keyNodeList.item(j);
                        if (keyNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element keyElement = (Element) keyNode;
                            if (keyElement.getNodeName().equals("fifths") == true) {
                                mxmlAttributes.setFifths(keyElement.getTextContent());
                            }
                            if (keyElement.getNodeName().equals("mode") == true) {
                                mxmlAttributes.setMode(keyElement.getTextContent());
                            }
                        }
                    }
                }
                else if (childElement.getNodeName().equals("time") == true) {
                    // time element
                    NodeList timeNodeList = childElement.getChildNodes();
                    for (int j = 0; j < timeNodeList.getLength(); j++) {
                        Node timeNode = timeNodeList.item(j);
                        if (timeNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element timeElement = (Element) timeNode;
                            if (timeElement.getNodeName().equals("beats") == true) {
                                mxmlAttributes.setBeats(timeElement.getTextContent());
                            }
                            else if (timeElement.getNodeName().equals("beats-type") == true) {
                                mxmlAttributes.setBeatsType(timeElement.getTextContent());
                            }
                        }
                    }
                }
                else if (childElement.getNodeName().equals("staves") == true) {
                    // staves element
                    mxmlAttributes.setStaves(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("clef") == true) {
                    // clef element
                }
            }
        }
        return mxmlAttributes;
    }

    private MusicXMLNote readNoteNodeList(Element element) {
        MusicXMLNote mxmlNote = new MusicXMLNote();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("rest") == true) {
                    // rest element
                    mxmlNote.setRest(true);
                }
                else if (childElement.getNodeName().equals("chord") == true) {
                    // rest element
                    mxmlNote.setChord(true);
                }
                else if (childElement.getNodeName().equals("pitch") == true) {
                    // pitch element
                    NodeList pitchNodeList = childElement.getChildNodes();
                    for (int j = 0; j < pitchNodeList.getLength(); j++) {
                        Node pitchNode = pitchNodeList.item(j);
                        if (pitchNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element pitchElement = (Element) pitchNode;
                            if (pitchElement.getNodeName().equals("step") == true) {
                                mxmlNote.setStep(pitchElement.getTextContent());
                            }
                            else if (pitchElement.getNodeName().equals("octave") == true) {
                                mxmlNote.setOctave(pitchElement.getTextContent());
                            }
                            else if (pitchElement.getNodeName().equals("alter") == true) {
                                mxmlNote.setAlter(pitchElement.getTextContent());
                            }
                        }
                    }
                }
                else if (childElement.getNodeName().equals("duration") == true) {
                    // duration element
                    mxmlNote.setDuration(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("tie") == true) {
                    // tie element
                    String tieType = childElement.getAttribute("type");
                    mxmlNote.setTieType(tieType);
                }
                else if (childElement.getNodeName().equals("voice") == true) {
                    // voice element
                    mxmlNote.setVoice(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("type") == true) {
                    // type element
                    mxmlNote.setType(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("accidental") == true) {
                    // accidental element
                    mxmlNote.setAccidental(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("stem") == true) {
                    // stem element
                    mxmlNote.setStem(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("staff") == true) {
                    // staff element
                    mxmlNote.setStaff(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("notations") == true) {
                    // articulations element
                    MusicXMLNotations mxmlNotations = readNotationsNodeList(childElement);
                    mxmlNote.setNotations(mxmlNotations);
                }
                else if (childElement.getNodeName().equals("time-modification") == true) {
                    // time-modification element
                    MusicXMLTimeModification mxmlTimeModification = readTimeModificationNodeList(childElement);
                    mxmlNote.setTimeModification(mxmlTimeModification);
                }
            }
        }

        return mxmlNote;
    }

    private MusicXMLNotations readNotationsNodeList(Element element) {
        MusicXMLNotations mxmlNotations = new MusicXMLNotations();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("articulations") == true) {
                    // articulations element
                    MusicXMLArticulations mxmlArticulations = readArticulationsNodeList(childElement);
                    mxmlNotations.setArticulations(mxmlArticulations);
                }
                if (childElement.getNodeName().equals("tied") == true) {
                    String tiedType = childElement.getAttribute("type");
                    if (tiedType != null) {
                        mxmlNotations.setTied(tiedType);
                    }
                }
            }
        }
        return mxmlNotations;
    }

    private MusicXMLTimeModification readTimeModificationNodeList(Element element) {
        MusicXMLTimeModification mxmlTimeModification = new MusicXMLTimeModification();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("actual-notes") == true) {
                    mxmlTimeModification.setActualNotes(childElement.getTextContent());
                }
                if (childElement.getNodeName().equals("normal-notes") == true) {
                    mxmlTimeModification.setNormalNotes(childElement.getTextContent());
                }
            }
        }
        return mxmlTimeModification;
    }

    private MusicXMLArticulations readArticulationsNodeList(Element element) {
        MusicXMLArticulations mxmlArticulations = new MusicXMLArticulations();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("staccato") == true) {
                    // staccato element
                    mxmlArticulations.setStaccato(true);
                }
            }
        }
        return mxmlArticulations;
    }

    private MusicXMLBackup readBackupNodeList(Element element) {
        MusicXMLBackup mxmlBackup = new MusicXMLBackup();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("duration") == true) {
                    // duration element
                    mxmlBackup.setDuration(childElement.getTextContent());
                }
            }
        }
        return mxmlBackup;
    }

    public Sequence convertToMidi() throws InvalidMidiDataException {
        return convertToMidi(DEFAULT_BASE_OCTAVE);
    }

    public Sequence convertToMidi(int baseOctave) throws InvalidMidiDataException {
        IMidiToolkit toolkit = JMPCore.getSoundManager().getMidiToolkit();
        final int BaseDuration = 480;
        final int FixedVelocity = 80;

        Sequence sequence = new Sequence(Sequence.PPQ, BaseDuration);

        int partSize = musicXML.partSize();
        for (int i = 0; i < partSize; i++) {
            MusicXMLPart part = musicXML.getPart(i);
            sequence.createTrack();

            int channel = 0;
            int program = 0;
            MusicXMLScorePart scorePart = musicXML.getPartList().getScorePart(part.getId());
            if (scorePart != null) {
                if (isAutoAssignChannel() == true) {
                    channel = JmpUtil.toInt(scorePart.getChannel(), 0) - 1;
                    channel = (channel < 0) ? 0 : (channel > 15) ? 15 : channel; // 不定値チェック
                }

                if (isAutoAssignProgramChange() == true) {
                    program = JmpUtil.toInt(scorePart.getProgram(), 0);
                    program = (program < 0) ? 0 : program; // 不定値チェック
                }
            }

            Track track = sequence.getTracks()[i];

            int divisionValue = BaseDuration;
            long position = 100;

            // プログラムチェンジイベント作成
            track.add(toolkit.createProgramChangeEvent(position, channel, program));
            position += 10;

            position = 480;

            long pastDuration = 0;
            for (MusicXMLMeasure meas : part.getMeasures()) {
                for (MusicXMLElement element : meas.getElements()) {
                    if (element instanceof MusicXMLAttributes) {
                        MusicXMLAttributes mxAttr = (MusicXMLAttributes) element;
                        if (mxAttr.getDivisions().isEmpty() == false) {
                            divisionValue = BaseDuration / mxAttr.getDivisionsInt();
                        }
                    }
                    if (element instanceof MusicXMLDirection) {
                        /* テンポイベント作成 */
                        MusicXMLDirection mxDire = (MusicXMLDirection) element;
                        track.add(toolkit.createTempoEvent(position, mxDire.getTempoDouble()));
                    }
                    else if (element instanceof MusicXMLNote) {
                        MusicXMLNote mxNote = (MusicXMLNote) element;
                        if (mxNote.isChord() == false) {
                            position += pastDuration;
                        }

                        // アクセント記号の表現
                        boolean isStaccato = false;
                        TiedType tiedType = TiedType.NONE;
                        MusicXMLNotations notations = mxNote.getNotations();
                        if (notations != null) {
                            MusicXMLArticulations articulations = notations.getArticulations();
                            if (articulations != null) {
                                // スタッカート
                                isStaccato = articulations.isStaccato();
                            }

                            // タイ
                            tiedType = notations.getTied();
                        }

                        long duration = divisionValue * mxNote.getDurationInt();
                        if (mxNote.isRest() == false) {
                            /* NoteON, NoteOFFイベント作成 */
                            long newDuration = duration;
                            if (isStaccato == true) {
                                // スタッカート加工
                                newDuration *= 0.5;
                            }
                            int midiNumber = convertToMidiNumber(mxNote.getStep(), mxNote.getAlterInt(), mxNote.getOctaveInt(), baseOctave);

                            // NoteON発行
                            if (tiedType == TiedType.NONE || tiedType == TiedType.START) {
                                track.add(toolkit.createNoteOnEvent(position, channel, midiNumber, FixedVelocity));
                            }
                            // NoteOFF発行
                            if (tiedType == TiedType.NONE || tiedType == TiedType.STOP) {
                                track.add(toolkit.createNoteOffEvent(position + newDuration, channel, midiNumber, FixedVelocity));
                            }
                        }
                        else {
                            /* 休符はDuration加算だけ行う */
                        }
                        pastDuration = duration;
                    }
                    else if (element instanceof MusicXMLBackup) {
                        /* Backupイベント */
                        MusicXMLBackup mxBackup = (MusicXMLBackup) element;
                        long duration = divisionValue * mxBackup.getDurationInt();
                        position -= duration; // ポジションを戻す
                    }
                }
            }
        }
        return sequence;
    }

    private int convertToMidiNumber(String step, int alter, int octave, int baseOctave) {
        int midiNumber = convertToMidiNumber(step, alter);
        midiNumber += (12 * (octave - baseOctave));
        if (midiNumber < 0) {
            midiNumber = 0;
        }
        if (midiNumber > 127) {
            midiNumber = 127;
        }
        return midiNumber;
    }

    private int convertToMidiNumber(String step, int alter) {
        int midiNumber = 0;
        if (step.equalsIgnoreCase("C") == true) {
            midiNumber = 60;
        }
        else if (step.equalsIgnoreCase("D") == true) {
            midiNumber = 62;
        }
        else if (step.equalsIgnoreCase("E") == true) {
            midiNumber = 64;
        }
        else if (step.equalsIgnoreCase("F") == true) {
            midiNumber = 65;
        }
        else if (step.equalsIgnoreCase("G") == true) {
            midiNumber = 67;
        }
        else if (step.equalsIgnoreCase("A") == true) {
            midiNumber = 69;
        }
        else if (step.equalsIgnoreCase("B") == true) {
            midiNumber = 71;
        }
        else {
            midiNumber = 60;
        }
        return midiNumber + alter;
    }

    public boolean isLoadResult() {
        return loadResult;
    }

    public boolean isAutoAssignChannel() {
        return isAutoAssignChannel;
    }

    public void setAutoAssignChannel(boolean isAutoAssignChannel) {
        this.isAutoAssignChannel = isAutoAssignChannel;
    }

    public boolean isAutoAssignProgramChange() {
        return isAutoAssignProgramChange;
    }

    public void setAutoAssignProgramChange(boolean isAutoAssignProgramChange) {
        this.isAutoAssignProgramChange = isAutoAssignProgramChange;
    }
}
