package jmsynth.midi;

import jmsynth.app.component.WavePreset;
import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.OscillatorFactory;
import jmsynth.oscillator.OscillatorSet;

public class DefaultProgramChangeTable extends ProgramChangeTable {
    
    private static IOscillator createOsc(OscillatorFactory ofc, String name) {
        return ofc.createOscillator(name);
    }

    public DefaultProgramChangeTable() {
        super();
        
        OscillatorFactory ofc = new OscillatorFactory();

        // GS準拠

        /*
         * Piano
         */
        this.sets[PC_1] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_2] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_3] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_4] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_5] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_6] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_7] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_8] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SINE);

        /*
         * Chromatic Percussion
         */
        this.sets[PC_9] = WavePreset.getOscillatorSet(0.04, 0.15, 0.5, 0.2, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_10] = WavePreset.getOscillatorSet(0.04, 0.15, 0.5, 0.2, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_11] = WavePreset.getOscillatorSet(0.04, 0.15, 0.5, 0.2, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_12] = WavePreset.getOscillatorSet(0.04, 0.15, 0.5, 0.2, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_13] = WavePreset.getOscillatorSet(0.04, 0.15, 0.5, 0.2, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_14] = WavePreset.getOscillatorSet(0.04, 0.15, 0.5, 0.2, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_15] = WavePreset.getOscillatorSet(0.04, 0.15, 0.5, 0.2, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_16] = WavePreset.getOscillatorSet(0.04, 0.15, 0.5, 0.2, ofc, WavePreset.PRESET_NAME_SINE);

        /*
         * Organ
         */
        this.sets[PC_17] = WavePreset.getOscillatorSet(0.05, 0.0, 1.0, 0.05, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_18] = WavePreset.getOscillatorSet(0.05, 0.0, 1.0, 0.05, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_19] = WavePreset.getOscillatorSet(0.05, 0.0, 1.0, 0.05, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_20] = WavePreset.getOscillatorSet(0.05, 0.0, 1.0, 0.05, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_21] = WavePreset.getOscillatorSet(0.05, 0.0, 1.0, 0.05, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_22] = WavePreset.getOscillatorSet(0.05, 0.0, 1.0, 0.05, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_23] = WavePreset.getOscillatorSet(0.05, 0.0, 1.0, 0.05, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_24] = WavePreset.getOscillatorSet(0.05, 0.0, 1.0, 0.05, ofc, WavePreset.PRESET_NAME_SINE);

        /*
         * Guitar
         */
        this.sets[PC_25] = WavePreset.getOscillatorSet(0.05, 0.2, 0.65, 0.3, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_26] = WavePreset.getOscillatorSet(0.05, 0.2, 0.65, 0.3, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_27] = WavePreset.getOscillatorSet(0.05, 0.2, 0.65, 0.3, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_28] = WavePreset.getOscillatorSet(0.05, 0.2, 0.65, 0.3, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_29] = WavePreset.getOscillatorSet(0.05, 0.2, 0.65, 0.3, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_30] = WavePreset.getOscillatorSet(0.05, 0.2, 0.65, 0.3, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_31] = WavePreset.getOscillatorSet(0.05, 0.2, 0.65, 0.3, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_32] = WavePreset.getOscillatorSet(0.05, 0.2, 0.65, 0.3, ofc, WavePreset.PRESET_NAME_SAW);

        /*
         * Bass
         */
        this.sets[PC_33] = WavePreset.getOscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, ofc, WavePreset.PRESET_NAME_TRI);
        this.sets[PC_34] = WavePreset.getOscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, ofc, WavePreset.PRESET_NAME_TRI);
        this.sets[PC_35] = WavePreset.getOscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, ofc, WavePreset.PRESET_NAME_TRI);
        this.sets[PC_36] = WavePreset.getOscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, ofc, WavePreset.PRESET_NAME_TRI);
        this.sets[PC_37] = WavePreset.getOscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, ofc, WavePreset.PRESET_NAME_TRI);
        this.sets[PC_38] = WavePreset.getOscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, ofc, WavePreset.PRESET_NAME_TRI);
        this.sets[PC_39] = WavePreset.getOscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, ofc, WavePreset.PRESET_NAME_TRI);
        this.sets[PC_40] = WavePreset.getOscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, ofc, WavePreset.PRESET_NAME_TRI);

        /*
         * Strings
         */
        this.sets[PC_41] = WavePreset.getOscillatorSet(0.04, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_42] = WavePreset.getOscillatorSet(0.04, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_43] = WavePreset.getOscillatorSet(0.04, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_44] = WavePreset.getOscillatorSet(0.04, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_45] = WavePreset.getOscillatorSet(0.04, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_46] = WavePreset.getOscillatorSet(0.04, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_47] = WavePreset.getOscillatorSet(0.04, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_48] = WavePreset.getOscillatorSet(0.04, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_SAW);

        /*
         * Ensemble
         */
        this.sets[PC_49] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.25, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_50] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.25, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_51] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.25, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_52] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.25, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_53] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.25, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_54] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.25, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_55] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.25, ofc, WavePreset.PRESET_NAME_SAW);
        this.sets[PC_56] = WavePreset.getOscillatorSet(0.0, 0.4, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);

        /*
         * Brass
         */
        this.sets[PC_57] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_58] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_59] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_60] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_61] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_62] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_63] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_64] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);

        /*
         * Reed
         */
        this.sets[PC_65] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_66] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_67] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_68] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_69] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_70] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_71] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_72] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);

        /*
         * Pipe
         */
        this.sets[PC_73] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE125);
        this.sets[PC_74] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE125);
        this.sets[PC_75] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE125);
        this.sets[PC_76] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE125);
        this.sets[PC_77] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE125);
        this.sets[PC_78] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE125);
        this.sets[PC_79] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE125);
        this.sets[PC_80] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE125);

        /*
         * Synth Lead
         */
        // Square Wave
        this.sets[PC_81] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SQUARE);
        // Saw Wave
        this.sets[PC_82] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_SAW);
        // Syn Calliope
        this.sets[PC_83] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.1, ofc, WavePreset.PRESET_NAME_TRI);
        // Chiffer Lead
        this.sets[PC_84] = WavePreset.getOscillatorSet(0.25, 0.0, 1.0, 0.25, ofc, WavePreset.PRESET_NAME_TRI);
        // Charang
        this.sets[PC_85] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);
        // Solo Vox
        this.sets[PC_86] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.0, ofc, WavePreset.PRESET_NAME_SAW);
        // 7th Saw
        this.sets[PC_87] = WavePreset.getOscillatorSet(0.1, 0.0, 1.0, 0.0, ofc, WavePreset.PRESET_NAME_SAW);
        // Bass & Lead
        this.sets[PC_88] = WavePreset.getOscillatorSet(0.0, 0.0, 1.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);

        /*
         * Pipe
         */
        // Fantasia
        this.sets[PC_89] = WavePreset.getOscillatorSet(0.0, 1.0, 0.25, 0.5, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_90] = WavePreset.getOscillatorSet(0.15, 0.75, 0.25, 0.5, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_91] = WavePreset.getOscillatorSet(0.15, 0.75, 0.25, 0.5, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_92] = WavePreset.getOscillatorSet(0.15, 0.75, 0.25, 0.5, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_93] = WavePreset.getOscillatorSet(0.15, 0.75, 0.25, 0.5, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_94] = WavePreset.getOscillatorSet(0.15, 0.75, 0.25, 0.5, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_95] = WavePreset.getOscillatorSet(0.15, 0.75, 0.25, 0.5, ofc, WavePreset.PRESET_NAME_SINE);
        this.sets[PC_96] = WavePreset.getOscillatorSet(0.15, 0.75, 0.25, 0.5, ofc, WavePreset.PRESET_NAME_SINE);

        /*
         * Synth Effects
         */
        this.sets[PC_97] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_98] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_99] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_100] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_101] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_102] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_103] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_104] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));

        /*
         * ethnic
         */
        this.sets[PC_105] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_106] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_107] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_108] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_109] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_110] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_111] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_112] = WavePreset.getOscillatorSet(ofc, WavePreset.PRESET_NAME_PULSE25);

        /*
         * Percussive
         */
        this.sets[PC_113] = WavePreset.getOscillatorSet(0.0, 0.25, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_114] = WavePreset.getOscillatorSet(0.0, 0.25, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_115] = WavePreset.getOscillatorSet(0.0, 0.25, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_116] = WavePreset.getOscillatorSet(0.0, 0.25, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_117] = WavePreset.getOscillatorSet(0.0, 0.25, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_118] = WavePreset.getOscillatorSet(0.0, 0.25, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_119] = WavePreset.getOscillatorSet(0.0, 0.25, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);
        this.sets[PC_120] = WavePreset.getOscillatorSet(0.0, 0.25, 0.0, 0.0, ofc, WavePreset.PRESET_NAME_PULSE25);

        /*
         * Sound effects
         */
        this.sets[PC_121] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_122] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_123] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_124] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_125] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_126] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_127] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_128] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
    }

}
