package jmsynth.midi;

import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.OscillatorFactory;
import jmsynth.oscillator.OscillatorSet;

public class ProgramChangeTable {

    protected static final int PC_1 = 0;
    protected static final int PC_2 = 1;
    protected static final int PC_3 = 2;
    protected static final int PC_4 = 3;
    protected static final int PC_5 = 4;
    protected static final int PC_6 = 5;
    protected static final int PC_7 = 6;
    protected static final int PC_8 = 7;
    protected static final int PC_9 = 8;
    protected static final int PC_10 = 9;
    protected static final int PC_11 = 10;
    protected static final int PC_12 = 11;
    protected static final int PC_13 = 12;
    protected static final int PC_14 = 13;
    protected static final int PC_15 = 14;
    protected static final int PC_16 = 15;
    protected static final int PC_17 = 16;
    protected static final int PC_18 = 17;
    protected static final int PC_19 = 18;
    protected static final int PC_20 = 19;
    protected static final int PC_21 = 20;
    protected static final int PC_22 = 21;
    protected static final int PC_23 = 22;
    protected static final int PC_24 = 23;
    protected static final int PC_25 = 24;
    protected static final int PC_26 = 25;
    protected static final int PC_27 = 26;
    protected static final int PC_28 = 27;
    protected static final int PC_29 = 28;
    protected static final int PC_30 = 29;
    protected static final int PC_31 = 30;
    protected static final int PC_32 = 31;
    protected static final int PC_33 = 32;
    protected static final int PC_34 = 33;
    protected static final int PC_35 = 34;
    protected static final int PC_36 = 35;
    protected static final int PC_37 = 36;
    protected static final int PC_38 = 37;
    protected static final int PC_39 = 38;
    protected static final int PC_40 = 39;
    protected static final int PC_41 = 40;
    protected static final int PC_42 = 41;
    protected static final int PC_43 = 42;
    protected static final int PC_44 = 43;
    protected static final int PC_45 = 44;
    protected static final int PC_46 = 45;
    protected static final int PC_47 = 46;
    protected static final int PC_48 = 47;
    protected static final int PC_49 = 48;
    protected static final int PC_50 = 49;
    protected static final int PC_51 = 50;
    protected static final int PC_52 = 51;
    protected static final int PC_53 = 52;
    protected static final int PC_54 = 53;
    protected static final int PC_55 = 54;
    protected static final int PC_56 = 55;
    protected static final int PC_57 = 56;
    protected static final int PC_58 = 57;
    protected static final int PC_59 = 58;
    protected static final int PC_60 = 59;
    protected static final int PC_61 = 60;
    protected static final int PC_62 = 61;
    protected static final int PC_63 = 62;
    protected static final int PC_64 = 63;
    protected static final int PC_65 = 64;
    protected static final int PC_66 = 65;
    protected static final int PC_67 = 66;
    protected static final int PC_68 = 67;
    protected static final int PC_69 = 68;
    protected static final int PC_70 = 69;
    protected static final int PC_71 = 70;
    protected static final int PC_72 = 71;
    protected static final int PC_73 = 72;
    protected static final int PC_74 = 73;
    protected static final int PC_75 = 74;
    protected static final int PC_76 = 75;
    protected static final int PC_77 = 76;
    protected static final int PC_78 = 77;
    protected static final int PC_79 = 78;
    protected static final int PC_80 = 79;
    protected static final int PC_81 = 80;
    protected static final int PC_82 = 81;
    protected static final int PC_83 = 82;
    protected static final int PC_84 = 83;
    protected static final int PC_85 = 84;
    protected static final int PC_86 = 85;
    protected static final int PC_87 = 86;
    protected static final int PC_88 = 87;
    protected static final int PC_89 = 88;
    protected static final int PC_90 = 89;
    protected static final int PC_91 = 90;
    protected static final int PC_92 = 91;
    protected static final int PC_93 = 92;
    protected static final int PC_94 = 93;
    protected static final int PC_95 = 94;
    protected static final int PC_96 = 95;
    protected static final int PC_97 = 96;
    protected static final int PC_98 = 97;
    protected static final int PC_99 = 98;
    protected static final int PC_100 = 99;
    protected static final int PC_101 = 100;
    protected static final int PC_102 = 101;
    protected static final int PC_103 = 102;
    protected static final int PC_104 = 103;
    protected static final int PC_105 = 104;
    protected static final int PC_106 = 105;
    protected static final int PC_107 = 106;
    protected static final int PC_108 = 107;
    protected static final int PC_109 = 108;
    protected static final int PC_110 = 109;
    protected static final int PC_111 = 110;
    protected static final int PC_112 = 111;
    protected static final int PC_113 = 112;
    protected static final int PC_114 = 113;
    protected static final int PC_115 = 114;
    protected static final int PC_116 = 115;
    protected static final int PC_117 = 116;
    protected static final int PC_118 = 117;
    protected static final int PC_119 = 118;
    protected static final int PC_120 = 119;
    protected static final int PC_121 = 120;
    protected static final int PC_122 = 121;
    protected static final int PC_123 = 122;
    protected static final int PC_124 = 123;
    protected static final int PC_125 = 124;
    protected static final int PC_126 = 125;
    protected static final int PC_127 = 126;
    protected static final int PC_128 = 127;

    public static final int PC_MAX = 128;

    protected OscillatorSet[] sets = null;

    public ProgramChangeTable() {
        OscillatorFactory ofc = new OscillatorFactory();
        sets = new OscillatorSet[PC_MAX];
        for (int i = 0; i < sets.length; i++) {
            sets[i] = new OscillatorSet(ofc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_SINE));
        }
    }

    public void setOscillatorSet(int pc, OscillatorSet set) {
        if (0 <= pc && pc < sets.length) {
            sets[pc] = set;
        }
    }

    public OscillatorSet getOscillatorSet(int pc) {
        if (pc < 0) {
            return sets[0];
        }
        else if (pc >= sets.length) {
            return sets[sets.length - 1];
        }
        return sets[pc];
    }

    public OscillatorSet getDrumOscillatorSet(int pc) {
        OscillatorFactory ofc = new OscillatorFactory();
        IOscillator osc = ofc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_NOISE_L);
        return new OscillatorSet(0.0, 0.25, 0.0, 0.0, osc);
    }
}