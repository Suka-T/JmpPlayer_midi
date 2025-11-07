package jlib.midi;

import java.io.IOException;

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
     * 全体BPMの平均値を取得
     * 
     * @return
     */
    abstract double getAverageTempoInBPM();

    /**
     * 全体BPMの中央値を取得
     * 
     * @return
     */
    abstract double getMedianTempoInBPM();
    
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
     * @throws IOException 
     * @throws Exception
     */
    abstract void parseMappedByteBuffer(short trkIndex, MappedParseFunc func) throws IOException;
    
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
    
    // MIDI解析中の進捗取得メソッド 
    abstract boolean isProgressNowAnalyzing();
    abstract long getProgressFinTrackNum();
    abstract long getProgressNotesCount();
    abstract long getProgressReadTick();
    
    abstract void setIgnoreNotesVelocityOfMonitor(int lowest, int highest);
    abstract boolean isValidIgnoreNotesOfMonitor();
    abstract boolean isGhostNotesOfMonitor(int velocity);
    
    abstract void setIgnoreNotesVelocityOfAudio(int lowest, int highest);
    abstract boolean isValidIgnoreNotesOfAudio();
    abstract boolean isGhostNotesOfAudio(int velocity);
    
    /**
     * 音声出力で一時格納するMIDIイベントバッファーのRAM使用率を指定 
     * 
     * @return double 使用率(0.01 ~ 1.0)
     */
    abstract double getUsageRamOfMidiEventBuffer();
    
    /**
     * 音声出力で一時格納するMIDIイベントバッファーのRAM使用率を指定する 
     * 
     * @param usage 0.01 ~ 1.0
     */
    abstract void setUsageRamOfMidiEventBuffer(double usage);
    
    abstract SignatureInfo getSignatureInfo();
    
    /**
     * MIDIファイル解析で使用するスレッド数設定を取得する 
     * 
     * @return スレッド数
     */
    abstract int getUsageAnalyzeThreadCount();

    /**
     * MIDIファイル解析で使用するスレッド数を設定する
     * 
     * @param usageAnalyzeThreadCount スレッド数
     */
    abstract void setUsageAnalyzeThreadCount(int usageAnalyzeThreadCount);
    
    /**
     * MIDIイベント抽出で使用するスレッド数設定を取得する
     * 
     * @return スレッド数
     */
    abstract int getUsageExtractThreadCount();

    /**
     * MIDIイベント抽出で使用するスレッド数を設定する
     * 
     * @param usageExtractThreadCount スレッド数
     */
    abstract void setUsageExtractThreadCount(int usageExtractThreadCount);
    
    /**
     * レンダリング済みのNotesCountを取得する 
     * @param tick 指定tickまでのNotesCountを取得する 
     * @return
     */
    abstract long getRenderedNotesCount(long tick);
}
