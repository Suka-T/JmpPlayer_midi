package jmsynth.app.component;

import java.util.ArrayList;
import java.util.List;

import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.OscillatorFactory;
import jmsynth.oscillator.OscillatorSet;

public class WavePreset {

    public static final String PRESET_NAME_SINE = "Sine";
    public static final String PRESET_NAME_SAW = "Saw";
    public static final String PRESET_NAME_TRI = "Triangle";
    public static final String PRESET_NAME_SQUARE = "Square";
    public static final String PRESET_NAME_PULSE25 = "Pulse25";
    public static final String PRESET_NAME_PULSE125 = "Pulse125";
    public static final String PRESET_NAME_LNOISE = "LongNoise";
    public static final String PRESET_NAME_SNOISE = "ShortNoise";
    
    public static final String[] PRESET_NAMES = { 
            PRESET_NAME_SINE, 
            PRESET_NAME_SAW, 
            PRESET_NAME_TRI, 
            PRESET_NAME_SQUARE, 
            PRESET_NAME_PULSE25, 
            PRESET_NAME_PULSE125, 
            PRESET_NAME_LNOISE, 
            PRESET_NAME_SNOISE 
            };

    public static final List<String> getPreset(String name) {
        List<String> lst = new ArrayList<String>();
        lst.clear();
        if (name.equalsIgnoreCase(PRESET_NAME_SAW)) {
            lst.add(OscillatorFactory.OSCILLATOR_NAME_SAW);
        }
        else if (name.equalsIgnoreCase(PRESET_NAME_TRI)) {
            lst.add(OscillatorFactory.OSCILLATOR_NAME_TRIANGLE);
        }
        else if (name.equalsIgnoreCase(PRESET_NAME_SQUARE)) {
            lst.add(OscillatorFactory.OSCILLATOR_NAME_SQUARE);
        }
        else if (name.equalsIgnoreCase(PRESET_NAME_PULSE25)) {
            lst.add(OscillatorFactory.OSCILLATOR_NAME_PULSE25);
        }
        else if (name.equalsIgnoreCase(PRESET_NAME_PULSE125)) {
            lst.add(OscillatorFactory.OSCILLATOR_NAME_PULSE125);
        }
        else if (name.equalsIgnoreCase(PRESET_NAME_LNOISE)) {
            lst.add(OscillatorFactory.OSCILLATOR_NAME_NOISE_L);
        }
        else if (name.equalsIgnoreCase(PRESET_NAME_SNOISE)) {
            lst.add(OscillatorFactory.OSCILLATOR_NAME_NOISE_S);
        }
        else {
            lst.add(OscillatorFactory.OSCILLATOR_NAME_SINE);
        }
        return lst;
    }
    
    public static boolean isNois(String name) {
        if (name.equalsIgnoreCase(PRESET_NAME_LNOISE)) {
            return true;
        }
        else if (name.equalsIgnoreCase(PRESET_NAME_SNOISE)) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public static OscillatorSet getOscillatorSet(OscillatorFactory osc, String name) {
        List<String> pre = getPreset(name);
        ArrayList<IOscillator> oscs = new ArrayList<>();
        for (String sPre : pre) {
            oscs.add(osc.createOscillator(sPre));
        }
        
        IOscillator[] array = new IOscillator[oscs.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = oscs.get(i);
        }
        OscillatorSet oscSet = new OscillatorSet(array);
        return oscSet;
    }
    
    public static OscillatorSet getOscillatorSet(double a, double d, double s, double r, OscillatorFactory osc, String name) {
        OscillatorSet oscSet = getOscillatorSet(osc, name);
        oscSet.setAttackTime(a);
        oscSet.setDecayTime(d);
        oscSet.setSustainLevel(s);
        oscSet.setReleaseTime(r);
        return oscSet;
    }
    
    public static OscillatorSet getOscillatorSet(double a, double d, double s, double r, long ma, long md, long mr, OscillatorFactory osc, String name) {
        OscillatorSet oscSet = getOscillatorSet(osc, name);
        oscSet.setAttackTime(a);
        oscSet.setDecayTime(d);
        oscSet.setSustainLevel(s);
        oscSet.setReleaseTime(r);
        oscSet.setMaxAttackMills(ma);
        oscSet.setMaxDecayMills(md);
        oscSet.setMaxReleaseMills(mr);
        return oscSet;
    }

}
