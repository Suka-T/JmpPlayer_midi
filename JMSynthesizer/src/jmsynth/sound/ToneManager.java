package jmsynth.sound;

import java.util.Stack;
import java.util.Vector;

public class ToneManager {
    /** 音声オブジェクトのインスタンス管理 */
    protected Tone[] toneInstances = null;

    /** アクティブな音声を管理 */
    protected Vector<Tone> activeTones = null;

    /** 発声中の音色を管理するためのテーブル */
    protected Tone[] playingTones = null;

    /** 利用可能な音色をプールするためのクラス */
    protected Stack<Tone> tonePool = null;
    
    Object mutex = new Object();
    
    public ToneManager(int polyphony) {
        activeTones = new Vector<Tone>();
        tonePool = new Stack<Tone>();
        toneInstances = new Tone[polyphony];
        playingTones = new Tone[256];
        
        // Toneインスタンス作成
        for (int i = 0; i < polyphony; i++) {
            toneInstances[i] = new Tone();
        }
        
        /* 音階データ生成 */
        for (int i = 0; i < polyphony; i++) {
            try {
                Tone rw = toneInstances[i];
                rw.setFrequency(523.3);// 523.3
                rw.setVelocity(0);
                tonePool.add(rw);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public Tone[] getToneInstances() {
        return toneInstances;
    }
    
    public int getActiveToneCount() {
        synchronized (mutex) {
            return activeTones.size();
        }
    }
    
    public int getDeactiveToneCount() {
        synchronized (mutex) {
            return tonePool.size();
        }
    }
    
    public Tone getActiveTone(int index) {
        synchronized (mutex) {
            return activeTones.get(index);
        }
    }
    
    public Tone getPlayingTone(int noteNo) {
        synchronized (mutex) {
            return playingTones[noteNo];
        }
    }
    
    public boolean isPlayingTone(int noteNo) {
        if (playingTones[noteNo] == null) {
            return false;
        }
        return true;
    }
    
    public int getPlayingToneLength() {
        return playingTones.length;
    }
    
    public int getPlayingToneCount() {
        synchronized (mutex) {
            int cnt = 0;
            for (int i=0; i<playingTones.length; i++) {
                if (playingTones[i] != null) {
                    cnt++;
                }
            }
            return cnt;
        }
    }
    
    public void activateTone(int noteNo, int velocity) {
        synchronized (mutex) {
            if (!tonePool.empty() && playingTones[noteNo] == null) {
                Tone t = tonePool.pop();
                if (t != null) {
                    t.setNote(noteNo);
                    t.setReleaseFlag(false);
                    t.resetEnvelopeOffset();
                    t.setVelocity(velocity);
                    t.setStartMills();
                    t.setTablePointer(0);
                    activeTones.add(t);
                    playingTones[noteNo] = t;
                }
            }
        }
    }
    
    public void deactivateTone(int noteNo) {
        synchronized (mutex) {
            Tone tone = playingTones[noteNo];
            if (tone != null) {
                activeTones.remove(tone);
                tonePool.push(tone);
                playingTones[noteNo] = null;
            }
        }
    }
    
    public void allDeactivateTone() {
        synchronized (mutex) {
            for (int i = 0; i < playingTones.length; i++) {
                Tone tone = playingTones[i];
                if (tone != null) {
                    activeTones.remove(tone);
                    tonePool.push(tone);
                    playingTones[i] = null;
                }
            }
        }
    }
    
    public double getCurrentPitch() {
        double val = 0.0;
        try {
            Tone t = toneInstances[0];
            val = t.getPitch();
        }
        catch(Exception e) {
        }
        return val;
    }
    
    public int getCurrentExpression() {
        int val = 0;
        try {
            Tone t = toneInstances[0];
            val = t.getExpression();
        }
        catch(Exception e) {
        }
        return val;
    }
    
    public int getCurrentVelocity() {
        int val = 0;
        try {
            for (int i=0; i<playingTones.length; i++) {
                Tone t = playingTones[i];
                if (t != null) {
                    val = t.getVelocity();
                    break;
                }
            }
        }
        catch(Exception e) {
        }
        return val;
    }
    
    public double getCurrentEnvelopeOffset() {
        double val = 0;
        try {
            for (int i=0; i<playingTones.length; i++) {
                Tone t = playingTones[i];
                if (t != null) {
                    val = t.getEnvelopeOffset();
                    break;
                }
            }
        }
        catch(Exception e) {
        }
        return val;
    }
    
    public int getCurrentOverallLevel() {
        int val = 0;
        try {
            for (int i=0; i<playingTones.length; i++) {
                Tone t = playingTones[i];
                if (t != null) {
                    val = t.getOverallLevel();
                    break;
                }
            }
        }
        catch(Exception e) {
        }
        return val;
    }
}