package jmsynth.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jmsynth.oscillator.OscillatorSet;
import jmsynth.sound.ISynthController;

public class MidiInterface implements Receiver {

    private ISynthController controller = null;

    private boolean isAutoSelectOscillator = true;

    private ProgramChangeTable table = null;

    public MidiInterface(ISynthController controller) {
        this.controller = controller;
        this.table = new DefaultProgramChangeTable();
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message == null) {
            return;
        }
        byte[] aMessage = message.getMessage();
        int length = message.getLength();
        if (MidiUtil.isChannelMessage(aMessage, length) == true) {
            sendChannelMessage(aMessage, length);
        }
        else if (MidiUtil.isMetaMessage(aMessage, length) == true) {
            sendMetaMessage(aMessage, length);
        }
        else if (MidiUtil.isSystemMessage(aMessage, length) == true) {
            sendSystemMessage(aMessage, length);
        }
    }

    protected void sendChannelMessage(byte[] aData, int length) {
        int channel = MidiUtil.getChannel(aData, length);
        if (controller.checkChannel(channel) == false) {
            return;
        }

        int command = MidiUtil.getCommand(aData, length);
        int data1 = MidiUtil.getData1(aData, length);
        int data2 = MidiUtil.getData2(aData, length);

        switch (command) {
            case DefineCommand.NOTE_ON: {
                controller.noteOn(channel, data1, data2);
            }
                break;
            case DefineCommand.NOTE_OFF: {
                controller.noteOff(channel, data1);
            }
                break;
            case DefineCommand.PITCH_BEND: {
                // -8192 ～ +8191
                int pitch = (data2 << 7) + (data1 & 0x7f) - 8192;
                controller.pitchBend(channel, pitch);
            }
                break;
            case DefineCommand.CONTROL_CHANGE: {
                sendChannelMessageForControlChange(channel, data1, data2);
            }
                break;
            case DefineCommand.PROGRAM_CHANGE: {
                executeProgramChange(channel, data1);
            }
                break;
            default:
                System.out.println("unknown " + command);
                break;
        }
    }

    protected void sendChannelMessageForControlChange(int channel, int data1, int data2) {
        switch (data1) {
            case DefineControlChange.BANK_SELECT_MSB://
                break;
            case DefineControlChange.MODULATION:// モジュレーション・デプス
                controller.setModulationDepth(channel, data2);
                break;

            case DefineControlChange.PORTAMENTO_TIME:// Portamento Time
                break;
            case DefineControlChange.DATA_ENTRY_MSB:// データエントリ（ＲＰＮ／ＮＲＰＮで指定したパラメータの値を設定）
                // System.out.println("NRPN "+controller.getNRPN(channel) +" " +
                // data2 +" ");
                switch (controller.getNRPN(channel)) {
                    // ピッチベンド・センシティビティ
                    case 0x00: {
                        controller.pitchBendSenc(channel, data2);
                    }
                        break;
                    case 0x08:// ビブラート・レイト（Vibrato Rate）
                    {
                        controller.setVibratoRate(channel, data2);
                    }
                        break;
                    case 0x09:// ビブラート・デプス（Vibrato Depth）
                    {
                        controller.setVibratoDepth(channel, data2);
                    }
                        break;
                    case 0x0A:// ビブラート・ディレイ（Vibrato Delay）
                    {
                        controller.setVibratoDelay(channel, data2);
                    }
                        break;
                }
                break;
            case DefineControlChange.MAIN_VOLUME:// メインボリューム(チャンネルの音量を設定）
            {
                float vol = ((float) data2 / 127.0f);// 127
                controller.setVolume(channel, vol);
            }
                break;
            case DefineControlChange.PAN_POT:// PAN
                controller.setPan(channel, data2);
                break;
            case DefineControlChange.EXPRESSION:// Expression
                controller.setExpression(channel, data2);
                break;
            // case 0x60:// variation
            // controller.setVariation(channel, data2);
            // break;
            // case 0x61:// Portamento
            // break;
            case DefineControlChange.NRPN_LSB:// NRPN
                controller.setNRPN(channel, data2);
                break;
            case DefineControlChange.RPN_MSB:// 101
                controller.setNRPN(channel, 0x00);
                break;
            case DefineControlChange.RESET_ALL_CONTROLLER:// リセット オール コントローラー
                controller.resetAllController(channel);
                break;
            case DefineControlChange.ALL_SOUND_OFF:// All Sound Off
                controller.allSoundOff(channel);
                break;
            case DefineControlChange.ALL_NOTE_OFF:// All Note Off
                controller.allNoteOff(channel);
                break;
            default:
                break;
        }
    }

    protected void sendMetaMessage(byte[] aData, int length) {
        /* 現状何もしない */
    }

    protected void sendSystemMessage(byte[] aData, int length) {
        int status = MidiUtil.getStatus(aData, length);
        switch (status) {
            case DefineCommand.SYSEX_BEGIN:
                if (MidiUtil.isGmSystemOn(aData) == true) {
                    executeSystemReset();
                }
                else if (MidiUtil.isGsReset(aData) == true) {
                    executeSystemReset();
                }
                else if (MidiUtil.isXgSystemOn(aData) == true) {
                    executeSystemReset();
                }
                break;
            default:
                break;
        }
    }

    private void executeProgramChange(int channel, int pc) {
        executeProgramChange(channel, pc, false);
    }

    private void executeProgramChange(int channel, int pc, boolean doForcedChange) {
        if (isAutoSelectOscillator() == true || doForcedChange == true) {
            OscillatorSet osc = getProgramChangeOscillator(channel, pc);
            controller.setOscillator(channel, osc);
        }
        //controller.setVolume(channel, 1.0f);
    }

    private void executeSystemReset() {
        // PCリセット
        for (int i = 0; i < 16; i++) {
            executeProgramChange(i, 0);
        }
        controller.systemReset();
    }

    public ProgramChangeTable getProgramChangeTable() {
        return table;
    }

    private OscillatorSet getProgramChangeOscillator(int ch, int pc) {
        if (ch == 9) {
            // 10chはノイズ固定
            return table.getDrumOscillatorSet(pc);
        }
        return table.getOscillatorSet(pc);
    }

    public void open() {
        controller.openDevice();
    }

    @Override
    public void close() {
        controller.closeDevice();
    }

    public boolean isAutoSelectOscillator() {
        return isAutoSelectOscillator;
    }

    public void setAutoSelectOscillator(boolean isAutoSelectOscillator) {
        this.isAutoSelectOscillator = isAutoSelectOscillator;
    }

    public ISynthController getSynthController() {
        return controller;
    }
}
