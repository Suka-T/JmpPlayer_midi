package jmp.convert.mml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

import function.Utility;
import jlib.midi.IMidiToolkit;
import jmp.convert.IJMPDocumentReader;
import jmp.core.JMPCore;

public class MusicMacroReader implements IJMPDocumentReader {

    public static enum MmlTone {
        C, D, E, F, G, A, B, R
    }

    // private boolean loadResult = false;
    private File file = null;

    private Vector<MmlObject> objects = null;

    public class MmlObject {
        public int col = 0;
        public int row = 0;

        public MmlObject(int col, int row) {
            this.col = col;
            this.row = row;
        };
    }

    public class MmlChannelObject extends MmlObject {
        public List<Integer> chLst = null;

        public MmlChannelObject(int col, int row, List<Integer> chLst) {
            super(col, row);
            
            this.chLst = new ArrayList<Integer>();
            for (int ti : chLst) this.chLst.add(ti); 
        }
    }

    public class MmlTempoObject extends MmlObject {
        public int tempo = 0;

        public MmlTempoObject(int col, int row, int tempo) {
            super(col, row);
            this.tempo = tempo;
        }
    }

    public class MmlVolumeObject extends MmlObject {
        public int volume = 0;

        public MmlVolumeObject(int col, int row, int volume) {
            super(col, row);
            this.volume = volume;
        }
    }

    public class MmlFmObject extends MmlObject {
        public int fmNum = 0;

        public MmlFmObject(int col, int row, int fmNum) {
            super(col, row);
            this.fmNum = fmNum;
        }
    }
    
    public class MmlOctaveObject extends MmlObject {
        public int oct = 0;

        public MmlOctaveObject(int col, int row, int oct) {
            super(col, row);
            this.oct = oct;
        }
    }

    public class MmlOctaveControllerObject extends MmlObject {
        public int amount = 0;

        public MmlOctaveControllerObject(int col, int row, int amount) {
            super(col, row);
            this.amount = amount;
        }
    }

    public class MmlLengthObject extends MmlObject {
        public int length = 0;

        public MmlLengthObject(int col, int row, int length) {
            super(col, row);
            this.length = length;
        }
    }

    public class MmlNoteObject extends MmlObject {
        public MmlTone tone = MmlTone.C;
        public int length = -1;
        public int alter = 0;
        public int dottCnt = 0;

        public MmlNoteObject(int col, int row, MmlTone tone, int length, int alter, int dottCnt) {
            super(col, row);
            this.tone = tone;
            this.length = length;
            this.alter = alter;
            this.dottCnt = dottCnt;
        }
    }

    public class MmlTupletObject extends MmlObject {
        public List<MmlNoteObject> tones;
        public int length = -1;
        public int dottCnt = 0;

        public MmlTupletObject(int col, int row, List<MmlNoteObject> tones, int length, int dottCnt) {
            super(col, row);
            this.tones = tones;
            this.length = length;
            this.dottCnt = dottCnt;
        }
    }

    public MusicMacroReader() {
        objects = new Vector<MusicMacroReader.MmlObject>();
    }

    public void load() throws IOException {

        // loadResult = false;

        List<String> contents = Utility.getTextFileContents(file.getPath());
        compile(contents);
    }

    public void load(File file) throws IOException {
        this.file = file;
        load();
    }

    public void compile(String str) {
        List<String> contents = new ArrayList<String>();
        String[] split = str.split("\r\n|\r|\n");
        for (int i = 0; i < split.length; i++) {
            contents.add(split[i]);
        }
        compile(contents);
    }

    public void compile(List<String> contents) {
        objects.clear();

        for (int r = 0; r < contents.size(); r++) {
            String line = contents.get(r);
            System.out.println(line);
            for (int c = 0; c < line.length(); c++) {
                String token = "";
                char cr = line.charAt(c);
                if (c == 0) {
                    if (cr == '#' || cr == ';') {
                        break;
                    }

                    List<Integer> chLst = new ArrayList<Integer>();
                    while (cr != ' ') {
                        chLst.add(toChannel(cr));
                        c++;
                        cr = line.charAt(c);
                    }
                    objects.add(new MmlChannelObject(c, r, chLst));
                }
                else {
                    if (isSymbol(cr) == true) {
                        int saveR = c;
                        int saveC = c;
                        char command = cr;
                        c++;
                        boolean isTuplet = false;
                        if (isTuplet(command) == true) {
                            isTuplet = true;
                        }
                        for (; c < line.length(); c++) {
                            cr = line.charAt(c);
                            if (isSymbol(cr) == true && isTuplet == false) {
                                c--;
                                break;
                            }
                            if (cr == '}') {
                                isTuplet = false;
                            }

                            token += new String(new char[] { cr });
                        }

                        // 解析
                        token = token.trim();
                        if (isTempo(command) == true) {
                            int tempo = Utility.tryParseInt(token, 120);
                            objects.add(new MmlTempoObject(saveC, saveR, tempo));
                        }
                        else if (isVolume(command) == true) {
                            int volume = Utility.tryParseInt(token, 0);
                            objects.add(new MmlVolumeObject(saveC, saveR, volume));
                        }
                        else if (isFmCommand(command) == true) {
                            int fm = Utility.tryParseInt(token, 1);
                            objects.add(new MmlFmObject(saveC, saveR, fm));
                        }
                        else if (isOctave(command) == true) {
                            int oct = Utility.tryParseInt(token, 0);
                            objects.add(new MmlOctaveObject(saveC, saveR, oct));
                        }
                        else if (isOctaveController(command) == true) {
                            int amount = toOctaveController(command);
                            objects.add(new MmlOctaveControllerObject(saveC, saveR, amount));
                        }
                        else if (isLength(command) == true) {
                            int length = Utility.tryParseInt(token, 0);
                            objects.add(new MmlLengthObject(saveC, saveR, length));
                        }
                        else if (isTone(command) == true) {
                            MmlTone tone = toTone(command);
                            if (token.isEmpty() == true) {
                                objects.add(new MmlNoteObject(saveC, saveR, tone, -1, 0, 0));
                            }
                            else {
                                int dottCnt = 0;
                                int alter = 0;
                                int start = 0;
                                if (isAlter(token.charAt(start)) == true) {
                                    alter = toAlter(token.charAt(start));
                                    start++;
                                }

                                String lToken = "";
                                for (int i = start; i < token.length(); i++) {
                                    if (token.charAt(i) == 0x2e) {// Dot
                                        dottCnt++;;
                                    }
                                    else {
                                        lToken += token.charAt(i);
                                    }
                                }

                                if (lToken.isEmpty() == true) {
                                    objects.add(new MmlNoteObject(saveC, saveR, tone, -1, alter, dottCnt));
                                }
                                else {
                                    objects.add(new MmlNoteObject(saveC, saveR, tone, Utility.tryParseInt(lToken, -1), alter, dottCnt));
                                }
                            }
                        }
                        else if (isTuplet(command) == true) {
                            MmlNoteObject note = null;
                            List<MmlNoteObject> tones = new ArrayList<MmlNoteObject>();
                            int length = -1;
                            int dottCnt = 0;
                            for (int i = 0; i < token.length(); i++) {
                                if (token.charAt(i) == '}') {
                                    if (note != null) {
                                        tones.add(note);
                                    }
                                    i++;
                                    String lToken = "";
                                    for (int j = i; j < token.length(); j++) {
                                        if (token.charAt(i) == 0x2e) { // Dot
                                            dottCnt++;
                                        }
                                        else {
                                            lToken += token.charAt(j);
                                        }
                                    }
                                    token = lToken.trim();
                                    length = Utility.tryParseInt(token, -1);
                                    break;
                                }
                                if (isTone(token.charAt(i)) == true) {
                                    if (note != null) {
                                        tones.add(note);
                                    }
                                    note = new MmlNoteObject(saveC, saveR, toTone(token.charAt(i)), -1, 0, dottCnt);
                                }
                                else if (isAlter(token.charAt(i)) == true) {
                                    if (note != null) {
                                        note.alter = toAlter(token.charAt(i));
                                    }
                                }
                            }

                            objects.add(new MmlTupletObject(saveC, saveR, tones, length, dottCnt));
                        }
                    }
                }
            }
        }
    }

    public Sequence convertToMidi() throws InvalidMidiDataException {
        IMidiToolkit toolkit = JMPCore.getSoundManager().getMidiToolkit();
        final int BaseDuration = 480;
        final int FixedVelocity = 80;

        Sequence sequence = new Sequence(Sequence.PPQ, BaseDuration);
        for (int trackIndex = 0; trackIndex < 'Z' - 'A' + 1; trackIndex++) {
            sequence.createTrack();
        }
        
        List<Integer> chLst = new ArrayList<Integer>();
        int curCh = -1;
        int octave = 4;
        int defaultLength = 4;
        int volume = FixedVelocity;

        long[] position = new long['Z' - 'A' + 1];
        for (int i=0; i<position.length; i++) {
            position[i] = 480;
        }
        for (MmlObject obj : objects) {
            if (obj instanceof MmlChannelObject) {
                MmlChannelObject o = (MmlChannelObject) obj;
                
                chLst.clear();
                for (int ti : o.chLst) chLst.add(ti); 
            }
            
            if (chLst.isEmpty()) {
                chLst.add(0); // CH指定無い場合はA固定 
            }
            
            if (obj instanceof MmlOctaveObject) {
                MmlOctaveObject o = (MmlOctaveObject) obj;
                octave = o.oct;
            }
            if (obj instanceof MmlVolumeObject) {
                MmlVolumeObject o = (MmlVolumeObject) obj;
                volume = o.volume;
            }
            else if (obj instanceof MmlOctaveControllerObject) {
                MmlOctaveControllerObject o = (MmlOctaveControllerObject) obj;
                octave += o.amount;
            }
            else if (obj instanceof MmlLengthObject) {
                MmlLengthObject o = (MmlLengthObject) obj;
                defaultLength = o.length;
            }
            else if (obj instanceof MmlFmObject) {
                MmlFmObject o = (MmlFmObject) obj;
                
                for (int trackIndex : chLst) {
                    curCh = trackIndex % 16;
                    sequence.getTracks()[trackIndex].add(toolkit.createProgramChangeEvent(position[trackIndex], curCh, o.fmNum - 1));
                }
            }
            else if (obj instanceof MmlTempoObject) {
                MmlTempoObject o = (MmlTempoObject) obj;
                
                for (int trackIndex : chLst) {
                    curCh = trackIndex % 16;
                    sequence.getTracks()[trackIndex].add(toolkit.createTempoEvent(position[trackIndex], o.tempo));
                }
            }
            else if (obj instanceof MmlNoteObject) {
                MmlNoteObject o = (MmlNoteObject) obj;
                
                for (int trackIndex : chLst) {
                    curCh = trackIndex % 16;
                    int length = defaultLength;
                    if (o.length != -1) {
                        length = o.length;
                    }
    
                    int duration = (int) ((double) BaseDuration * ((double) 4 / (double) length));
                    if (o.dottCnt != 0) {
                        int addDuration = duration;
                        for (int i = 0; i < o.dottCnt; i++) {
                            addDuration /= 2;
                            duration += addDuration;
                        }
                    }
                    if (o.tone == MmlTone.R) {
                        position[trackIndex] += duration;
                    }
                    else {
                        int midiNumber = convertToMidiNumber(o.tone, o.alter, octave);
                        sequence.getTracks()[trackIndex].add(toolkit.createNoteOnEvent(position[trackIndex], curCh, midiNumber, volume));
                        sequence.getTracks()[trackIndex].add(toolkit.createNoteOffEvent(position[trackIndex] + (int) ((double) duration * 1.0), curCh, midiNumber, 0));
                        position[trackIndex] += duration;
                    }
                }
            }
            else if (obj instanceof MmlTupletObject) {
                MmlTupletObject o = (MmlTupletObject) obj;
                
                for (int trackIndex : chLst) {
                    curCh = trackIndex % 16;
                    int length = defaultLength;
                    if (o.length != -1) {
                        length = o.length;
                    }
    
                    int duration = (int) ((double) BaseDuration * ((double) 4 / (double) length));
                    if (o.dottCnt != 0) {
                        int addDuration = duration;
                        for (int i = 0; i < o.dottCnt; i++) {
                            addDuration /= 2;
                            duration += addDuration;
                        }
                    }
                    if (o.tones.size() <= 0) {
                        position[trackIndex] += duration;
                    }
                    else {
                        long nextPosition = position[trackIndex] + duration;
                        int dDuration = duration / o.tones.size();
                        for (MmlNoteObject note : o.tones) {
                            int midiNumber = convertToMidiNumber(note.tone, note.alter, octave);
                            sequence.getTracks()[trackIndex].add(toolkit.createNoteOnEvent(position[trackIndex], curCh, midiNumber, volume));
                            sequence.getTracks()[trackIndex].add(toolkit.createNoteOffEvent(position[trackIndex] + (int) ((double) dDuration * 1.0), curCh, midiNumber, 0));
                            position[trackIndex] += dDuration;
                        }
                        position[trackIndex] = nextPosition;
                    }
                }
            }
        }
        return sequence;
    }

    public static final int DEFAULT_BASE_OCTAVE = 4;

    private int convertToMidiNumber(MmlTone tone, int alter, int octave) {
        return convertToMidiNumber(tone, alter, octave, DEFAULT_BASE_OCTAVE);
    }

    private int convertToMidiNumber(MmlTone tone, int alter, int octave, int baseOctave) {
        int midiNumber = convertToMidiNumber(tone, alter);
        midiNumber += (12 * (octave - baseOctave));
        if (midiNumber < 0) {
            midiNumber = 0;
        }
        if (midiNumber > 127) {
            midiNumber = 127;
        }
        return midiNumber;
    }

    private int convertToMidiNumber(MmlTone tone, int alter) {
        int midiNumber = 0;
        switch (tone) {
            case C:
                midiNumber = 60;
                break;
            case D:
                midiNumber = 62;
                break;
            case E:
                midiNumber = 64;
                break;
            case F:
                midiNumber = 65;
                break;
            case G:
                midiNumber = 67;
                break;
            case A:
                midiNumber = 69;
                break;
            case B:
                midiNumber = 71;
                break;
            default:
                midiNumber = 60;
                break;
        }
        return midiNumber + alter;
    }

    public int toChannel(char c) {
        int ch = 0;
        if ('A' <= c && c <= 'P') {
            ch = c - 'A';
        }
        System.out.println("" + ch);
        if (ch < 0) {
            ch = 0;
        }
        else if (ch > 15) {
            ch = 15;
        }
        return ch;
    }

    public boolean isAbstractSymbol(char c, char a) {
        if (c == a) {
            return true;
        }
        return false;
    }

    public boolean isSymbol(char c) {
        if (isTempo(c) || isVolume(c) || isFmCommand(c) || isOctave(c) || isLength(c) || isTone(c) || isOctaveController(c) || isTuplet(c)) {
            return true;
        }
        return false;
    }

    public boolean isTempo(char c) {
        return isAbstractSymbol(c, 't');
    }

    public boolean isVolume(char c) {
        return isAbstractSymbol(c, 'v');
    }

    public boolean isFmCommand(char c) {
        return isAbstractSymbol(c, '@');
    }

    public boolean isOctave(char c) {
        return isAbstractSymbol(c, 'o');
    }
    
    public boolean isLength(char c) {
        return isAbstractSymbol(c, 'l');
    }

    public boolean isOctaveController(char c) {
        return isAbstractSymbol(c, '<') || isAbstractSymbol(c, '>');
    }

    public int toOctaveController(char c) {
        switch (c) {
            case '<':
                return -1;
            case '>':
                return 1;
            default:
                return 0;
        }
    }

    public boolean isTuplet(char c) {
        return isAbstractSymbol(c, '{');
    }

    public boolean isTone(char c) {
        if ('a' <= c && c <= 'g' || c == 'r') {
            return true;
        }
        else if ('A' <= c && c <= 'G' || c == 'R') {
            return true;
        }
        return false;
    }

    public MmlTone toTone(char c) {
        switch (c) {
            case 'c':
            case 'C':
                return MmlTone.C;
            case 'd':
            case 'D':
                return MmlTone.D;
            case 'e':
            case 'E':
                return MmlTone.E;
            case 'f':
            case 'F':
                return MmlTone.F;
            case 'g':
            case 'G':
                return MmlTone.G;
            case 'a':
            case 'A':
                return MmlTone.A;
            case 'b':
            case 'B':
                return MmlTone.B;
            case 'r':
            case 'R':
            default:
                return MmlTone.R;
        }
    }

    public boolean isAlter(char c) {
        switch (c) {
            case '#':
            case '+':
            case '-':
                return true;
            default:
                return false;
        }
    }

    public int toAlter(char c) {
        switch (c) {
            case '#':
            case '+':
                return 1;
            case '-':
                return -1;
            default:
                return 0;
        }
    }

}
