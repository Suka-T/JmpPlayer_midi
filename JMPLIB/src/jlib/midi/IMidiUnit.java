package jlib.midi;

/**
 * 高度なMidi関係の設定にアクセスするためのクラス
 *
 * @author akkut
 *
 */
public interface IMidiUnit {

    /**
     * Midiシーケンサーの再生状態取得
     *
     * @return
     */
    abstract boolean isRunning();

    /**
     * BPMベースのテンポを取得
     *
     * @return
     */
    abstract double getTempoInBPM();
    
    /**
     * Sequence最初のBPMを取得 
     * 
     * @return
     */
    abstract double getFirstTempoInBPM();

    /**
     * 現在のティック取得
     *
     * @return
     */
    abstract long getTickPosition();

    /**
     * 総ティック数取得
     *
     * @return
     */
    abstract long getTickLength();

    /**
     * 現在秒数取得(usec)
     *
     * @return
     */
    abstract long getMicrosecondPosition();

    /**
     * 総秒数取得(usec)
     * 
     * @return
     */
    abstract long getMicrosecondLength();
    
    /**
     * レンダリングモードか問い合わせ。(Midiメッセージを送らずTickの進行のみ行うモード) 
     * 
     * @return
     */
    abstract boolean isRenderingOnlyMode();
    
    /**
     * 分解能取得 
     * 
     * @return
     */
    abstract int getResolution();
    
    /**
     * MIDIファイルアクセス処理 
     * 
     * @param trkIndex
     * @param func
     * @throws Exception
     */
    abstract void parseMappedByteBuffer(short trkIndex, MappedParseFunc func) throws Exception ;
    
    /**
     * 有効なSequenceがあるかチェック 
     * 
     * @return
     */
    abstract boolean isValidSequence();
    
    /**
     * 最大ノーツ数 
     * 
     * @return
     */
    abstract long getNumOfNote();
    
    /**
     * トラック数取得 
     * 
     * @return
     */
    abstract int getNumOfTrack();
}
