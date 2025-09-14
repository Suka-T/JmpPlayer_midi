package jlib.midi;

public interface INotesMonitor {
    /***
     * 現在のモニター情報をリセットする
     */
    abstract void reset();
    
    /**
     * 現在のモニター情報をリセットする
     */
    abstract void resetNoteMonitor();
    
    /**
     * ノート総数解析データをクリアする
     */
    abstract void clearNumOfNotes();
    
    /**
     * 現在のMIDIシーケンスを解析する
     */
    abstract void analyzeMidiSequence();
    
    /**
     * ノーツカウントを取得する
     * 
     * @return ノーツカウント
     */
    abstract long getNotesCount();
    
    /**
     * ノーツ総数を取得する
     * 
     * @return ノーツ総数
     */
    abstract long getNumOfNotes();
    
    /***
     * NPSを取得する
     * 
     * @return NPS
     */
    abstract double getNps();
    
    /***
     * 最大NPSを取得する
     * 
     * @return 最大NPS
     */
    abstract double getMaxNps();
    
    /**
     * 同時発声数を取得する
     * 
     * @return 同時発声数
     */
    abstract int getPolyphony();
    
    /**
     * 最大同時発声数を取得する
     * 
     * @return 同時発声数
     */
    abstract int getMaxPolyphony();
    
    /**
     * 指定CHとMIDI番号のNoteONステータスを取得する
     * 
     * @param channel CH番号 0~15
     * @param midiNo MIDI番号 0~127
     * @return ON:true, OFF:false
     */
    abstract boolean isNoteOn(int channel, int midiNo);
    
    /***
     * 各MIDI番号のトップレイヤー鍵盤のCH番号を取得する
     * 
     * @param midiNo
     * @return
     */
    abstract int getTopNoteOnChannel(int midiNo);
    
    /***
     * 各MIDI番号のトップレイヤー鍵盤のCH番号を取得する
     * 
     * @param midiNo
     * @param orderAsc
     * @return
     */
    abstract int getTopNoteOnChannel(int midiNo, boolean orderAsc);
    
    /***
     * 各MIDI番号のトップレイヤー鍵盤のTrack番号を取得する
     * 
     * @param midiNo
     * @return
     */
    abstract int getTopNoteOnTrack(int midiNo);
    
    /***
     * 各MIDI番号のトップレイヤー鍵盤のCH番号を取得する
     * 
     * @param midiNo
     * @param orderAsc
     * @return
     */
    abstract int getTopNoteOnTrack(int midiNo, boolean orderAsc);
    
    /***
     * トラック数を取得する
     * 
     * @return トラック数
     */
    abstract int getNumOfTrack();
    
    /**
     * ピッチベンドステータスを取得する 
     * 
     * @param channel
     * @return
     */
    abstract int getPitchBend(int channel);
    
    /***
     * エクスプレッションステータスを取得する 
     * 
     * @param channel
     * @return
     */
    abstract int getExpression(int channel);
}
