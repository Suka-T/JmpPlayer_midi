package jmsynth.app.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import jmsynth.JMSoftSynthesizer;
import jmsynth.JMSynthFile;
import jmsynth.midi.MidiInterface;

public class WaveViewerFrame extends JFrame implements ActionListener {

    private JPanel contentPane;
    protected MultiWaveViewerPanel panel;

    private ChannelSetupDialog setupDialog = null;

    private MidiInterface midiInterface = null;
    private JMSoftSynthesizer softSynth = null;
    private JCheckBox chckbxAutoOscChange;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton rdbtnModeDetail;
    private JRadioButton rdbtnModeSpectrum;
    private JRadioButton rdbtnModeMerge;

    private ProgramChangeTableEditDialog tableEditer;
    private JPanel leftPanel;
    private JCheckBox chckbxWave1;
    private JCheckBox chckbxWave2;
    private JCheckBox chckbxWave3;
    private JCheckBox chckbxWave4;
    private JCheckBox chckbxWave5;
    private JCheckBox chckbxWave6;
    private JCheckBox chckbxWave7;
    private JCheckBox chckbxWave8;
    private JCheckBox chckbxWave9;
    private JCheckBox chckbxWave10;
    private JCheckBox chckbxWave11;
    private JCheckBox chckbxWave12;
    private JCheckBox chckbxWave13;
    private JCheckBox chckbxWave14;
    private JCheckBox chckbxWave15;
    private JCheckBox chckbxWave16;
    private JButton btnAllOff;
    private JButton btnAllOn;
    private JCheckBox chckbxAutoWaveVisible;
    private JPanel topPanel;
    private JButton btnSetup;
    private JPanel bottomPanel;
    private JButton btnWCDefault;
    private JButton btnWCSimple;
    private JRadioButton rdbtnModeInfo;

    /**
     * Create the frame.
     * 
     * @wbp.parser.constructor
     */
    public WaveViewerFrame(JMSoftSynthesizer synth) {
        setTitle(JMSoftSynthesizer.INFO_NAME + " Setting Dialog");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 853, 667);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 240));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        softSynth = synth;
        contentPane.setLayout(new BorderLayout(0, 0));

        panel = new MultiWaveViewerPanel(synth);
        contentPane.add(panel);

        bottomPanel = new JPanel();
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        setupDialog = new ChannelSetupDialog(synth);

        JButton btnSaveButton = new JButton("Save");
        btnSaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File ret = null;

                JFileChooser filechooser = new JFileChooser();
                File f = new File(new File("config." + JMSynthFile.EXTENSION_CONFIG).getAbsolutePath());
                filechooser.setSelectedFile(f);

                int selected = filechooser.showSaveDialog(WaveViewerFrame.this);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    ret = filechooser.getSelectedFile();
                }
                if (ret != null) {
                    JMSynthFile.saveSynthConfig(ret, softSynth);
                }
            }
        });
        bottomPanel.add(btnSaveButton);

        JButton btnLoad = new JButton("Load");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File ret = null;

                // 確認ダイアログ
                JFileChooser filechooser = new JFileChooser();
                int selected = filechooser.showOpenDialog(WaveViewerFrame.this);
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        ret = filechooser.getSelectedFile();
                        break;
                    default:
                        break;
                }
                if (ret != null) {
                    JMSynthFile.loadSynthConfig(ret, softSynth);
                }
            }
        });
        bottomPanel.add(btnLoad);

        JButton btnTable = new JButton("Table asign");
        btnTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableEditer.openDlg(midiInterface.getProgramChangeTable(), softSynth);
            }
        });
        bottomPanel.add(btnTable);

        leftPanel = new JPanel();
        contentPane.add(leftPanel, BorderLayout.WEST);
        leftPanel.setLayout(new GridLayout(22, 0, 0, 0));

        btnWCDefault = new JButton("Full color");
        btnWCDefault.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.setRestoreColorTable();
            }
        });
        leftPanel.add(btnWCDefault);

        btnWCSimple = new JButton("Mono color");
        btnWCSimple.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.setDefaultColorTable();
            }
        });
        leftPanel.add(btnWCSimple);

        chckbxAutoWaveVisible = new JCheckBox("Auto");
        chckbxAutoWaveVisible.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.visibleAuto = chckbxAutoWaveVisible.isSelected();
                boolean cbEnable = true;
                if (chckbxAutoWaveVisible.isSelected() == true) {
                    cbEnable = false;
                }
                else {
                    cbEnable = true;
                }
                chckbxWave1.setEnabled(cbEnable);
                chckbxWave2.setEnabled(cbEnable);
                chckbxWave3.setEnabled(cbEnable);
                chckbxWave4.setEnabled(cbEnable);
                chckbxWave5.setEnabled(cbEnable);
                chckbxWave6.setEnabled(cbEnable);
                chckbxWave7.setEnabled(cbEnable);
                chckbxWave8.setEnabled(cbEnable);
                chckbxWave9.setEnabled(cbEnable);
                chckbxWave10.setEnabled(cbEnable);
                chckbxWave11.setEnabled(cbEnable);
                chckbxWave12.setEnabled(cbEnable);
                chckbxWave13.setEnabled(cbEnable);
                chckbxWave14.setEnabled(cbEnable);
                chckbxWave15.setEnabled(cbEnable);
                chckbxWave16.setEnabled(cbEnable);
                updateMenu();
            }
        });

        btnAllOff = new JButton("Hide all");
        btnAllOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean bb = false;
                chckbxWave1.setSelected(bb);
                chckbxWave2.setSelected(bb);
                chckbxWave3.setSelected(bb);
                chckbxWave4.setSelected(bb);
                chckbxWave5.setSelected(bb);
                chckbxWave6.setSelected(bb);
                chckbxWave7.setSelected(bb);
                chckbxWave8.setSelected(bb);
                chckbxWave9.setSelected(bb);
                chckbxWave10.setSelected(bb);
                chckbxWave11.setSelected(bb);
                chckbxWave12.setSelected(bb);
                chckbxWave13.setSelected(bb);
                chckbxWave14.setSelected(bb);
                chckbxWave15.setSelected(bb);
                chckbxWave16.setSelected(bb);
                updateLabel();
            }
        });

        btnAllOn = new JButton("Show all");
        btnAllOn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean bb = true;
                chckbxWave1.setSelected(bb);
                chckbxWave2.setSelected(bb);
                chckbxWave3.setSelected(bb);
                chckbxWave4.setSelected(bb);
                chckbxWave5.setSelected(bb);
                chckbxWave6.setSelected(bb);
                chckbxWave7.setSelected(bb);
                chckbxWave8.setSelected(bb);
                chckbxWave9.setSelected(bb);
                chckbxWave10.setSelected(bb);
                chckbxWave11.setSelected(bb);
                chckbxWave12.setSelected(bb);
                chckbxWave13.setSelected(bb);
                chckbxWave14.setSelected(bb);
                chckbxWave15.setSelected(bb);
                chckbxWave16.setSelected(bb);
                updateLabel();
            }
        });
        leftPanel.add(btnAllOn, BorderLayout.SOUTH);
        leftPanel.add(btnAllOff, BorderLayout.NORTH);
        chckbxAutoWaveVisible.setSelected(true);
        leftPanel.add(chckbxAutoWaveVisible);

        chckbxWave1 = new JCheckBox("wave1");
        chckbxWave1.setSelected(false);
        chckbxWave1.setEnabled(true);
        leftPanel.add(chckbxWave1);

        chckbxWave2 = new JCheckBox("wave2");
        chckbxWave2.setSelected(false);
        chckbxWave2.setEnabled(true);
        leftPanel.add(chckbxWave2);

        chckbxWave3 = new JCheckBox("wave3");
        chckbxWave3.setSelected(false);
        chckbxWave3.setEnabled(true);
        leftPanel.add(chckbxWave3);

        chckbxWave4 = new JCheckBox("wave4");
        chckbxWave4.setSelected(false);
        chckbxWave4.setEnabled(true);
        leftPanel.add(chckbxWave4);

        chckbxWave5 = new JCheckBox("wave5");
        chckbxWave5.setSelected(false);
        chckbxWave5.setEnabled(true);
        leftPanel.add(chckbxWave5);

        chckbxWave6 = new JCheckBox("wave6");
        chckbxWave6.setSelected(false);
        chckbxWave6.setEnabled(true);
        leftPanel.add(chckbxWave6);

        chckbxWave7 = new JCheckBox("wave7");
        chckbxWave7.setSelected(false);
        chckbxWave7.setEnabled(true);
        leftPanel.add(chckbxWave7);

        chckbxWave8 = new JCheckBox("wave8");
        chckbxWave8.setSelected(false);
        chckbxWave8.setEnabled(true);
        leftPanel.add(chckbxWave8);

        chckbxWave9 = new JCheckBox("wave9");
        chckbxWave9.setSelected(false);
        chckbxWave9.setEnabled(true);
        leftPanel.add(chckbxWave9);

        chckbxWave10 = new JCheckBox("wave10");
        chckbxWave10.setSelected(false);
        chckbxWave10.setEnabled(true);
        leftPanel.add(chckbxWave10);

        chckbxWave11 = new JCheckBox("wave11");
        chckbxWave11.setSelected(false);
        chckbxWave11.setEnabled(true);
        leftPanel.add(chckbxWave11);

        chckbxWave12 = new JCheckBox("wave12");
        chckbxWave12.setSelected(false);
        chckbxWave12.setEnabled(true);
        leftPanel.add(chckbxWave12);

        chckbxWave13 = new JCheckBox("wave13");
        chckbxWave13.setSelected(false);
        chckbxWave13.setEnabled(true);
        leftPanel.add(chckbxWave13);

        chckbxWave14 = new JCheckBox("wave14");
        chckbxWave14.setSelected(false);
        chckbxWave14.setEnabled(true);
        leftPanel.add(chckbxWave14);

        chckbxWave15 = new JCheckBox("wave15");
        chckbxWave15.setSelected(false);
        chckbxWave15.setEnabled(true);
        leftPanel.add(chckbxWave15);

        chckbxWave16 = new JCheckBox("wave16");
        chckbxWave16.setSelected(false);
        chckbxWave16.setEnabled(true);
        leftPanel.add(chckbxWave16);

        topPanel = new JPanel();
        topPanel.setBackground(new Color(240, 240, 240));
        contentPane.add(topPanel, BorderLayout.NORTH);

        rdbtnModeDetail = new JRadioButton("Waves");
        rdbtnModeDetail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLabel();
            }
        });

        rdbtnModeMerge = new JRadioButton("Top");
        rdbtnModeMerge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLabel();
            }
        });
        rdbtnModeMerge.setSelected(true);
        buttonGroup.add(rdbtnModeMerge);
        topPanel.add(rdbtnModeMerge);
        buttonGroup.add(rdbtnModeDetail);
        rdbtnModeDetail.setSelected(false);
        topPanel.add(rdbtnModeDetail);

        btnSetup = new JButton("Wave setup");
        btnSetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupDialog.setLocation(WaveViewerFrame.this.getX() + 20, WaveViewerFrame.this.getY() + 20);
                setupDialog.setVisible(true);
            }
        });

        rdbtnModeInfo = new JRadioButton("Parameters");
        rdbtnModeInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLabel();
            }
        });
        buttonGroup.add(rdbtnModeInfo);
        topPanel.add(rdbtnModeInfo);

        rdbtnModeSpectrum = new JRadioButton("Spectrum");
        rdbtnModeSpectrum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLabel();
            }
        });
        buttonGroup.add(rdbtnModeSpectrum);
        topPanel.add(rdbtnModeSpectrum);
        topPanel.add(btnSetup);

        JButton btnResetAllChannel = new JButton("Init all channel");
        topPanel.add(btnResetAllChannel);

        chckbxAutoOscChange = new JCheckBox("Enable ProgramChange");
        topPanel.add(chckbxAutoOscChange);
        chckbxAutoOscChange.setVisible(false);
        chckbxAutoOscChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (midiInterface != null) {
                    midiInterface.setAutoSelectOscillator(chckbxAutoOscChange.isSelected());
                }
            }
        });
        btnResetAllChannel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                softSynth.initializeAllChannel();

                if (setupDialog.isVisible() == true) {
                    setupDialog.reset();
                    setupDialog.repaint();
                }
            }
        });
        
        // 非公開機能 
        btnSaveButton.setVisible(false);
        btnLoad.setVisible(false);
        btnTable.setVisible(false);

        _init();
    }

    public WaveViewerFrame(MidiInterface iface) {
        this((JMSoftSynthesizer) iface.getSynthController());
        midiInterface = iface;
        chckbxAutoOscChange.setVisible(true);

        _init();
    }

    private void _init() {

        chckbxWave1.addActionListener(this);
        chckbxWave2.addActionListener(this);
        chckbxWave3.addActionListener(this);
        chckbxWave4.addActionListener(this);
        chckbxWave5.addActionListener(this);
        chckbxWave6.addActionListener(this);
        chckbxWave7.addActionListener(this);
        chckbxWave8.addActionListener(this);
        chckbxWave9.addActionListener(this);
        chckbxWave10.addActionListener(this);
        chckbxWave11.addActionListener(this);
        chckbxWave12.addActionListener(this);
        chckbxWave13.addActionListener(this);
        chckbxWave14.addActionListener(this);
        chckbxWave15.addActionListener(this);
        chckbxWave16.addActionListener(this);

        boolean bb = false;
        chckbxWave1.setSelected(bb);
        chckbxWave2.setSelected(bb);
        chckbxWave3.setSelected(bb);
        chckbxWave4.setSelected(bb);
        chckbxWave5.setSelected(bb);
        chckbxWave6.setSelected(bb);
        chckbxWave7.setSelected(bb);
        chckbxWave8.setSelected(bb);
        chckbxWave9.setSelected(bb);
        chckbxWave10.setSelected(bb);
        chckbxWave11.setSelected(bb);
        chckbxWave12.setSelected(bb);
        chckbxWave13.setSelected(bb);
        chckbxWave14.setSelected(bb);
        chckbxWave15.setSelected(bb);
        chckbxWave16.setSelected(bb);
        chckbxAutoWaveVisible.setSelected(true);

        tableEditer = new ProgramChangeTableEditDialog();
        rdbtnModeDetail.setVisible(true);
        rdbtnModeSpectrum.setVisible(false);
        rdbtnModeMerge.setVisible(true);
        rdbtnModeInfo.setVisible(true);

        updateLabel();
    }

    private void updateMenu() {
        chckbxWave1.setSelected(panel.visibleWave[0]);
        chckbxWave2.setSelected(panel.visibleWave[1]);
        chckbxWave3.setSelected(panel.visibleWave[2]);
        chckbxWave4.setSelected(panel.visibleWave[3]);
        chckbxWave5.setSelected(panel.visibleWave[4]);
        chckbxWave6.setSelected(panel.visibleWave[5]);
        chckbxWave7.setSelected(panel.visibleWave[6]);
        chckbxWave8.setSelected(panel.visibleWave[7]);
        chckbxWave9.setSelected(panel.visibleWave[8]);
        chckbxWave10.setSelected(panel.visibleWave[9]);
        chckbxWave11.setSelected(panel.visibleWave[10]);
        chckbxWave12.setSelected(panel.visibleWave[11]);
        chckbxWave13.setSelected(panel.visibleWave[12]);
        chckbxWave14.setSelected(panel.visibleWave[13]);
        chckbxWave15.setSelected(panel.visibleWave[14]);
        chckbxWave16.setSelected(panel.visibleWave[15]);
    }

    private void updateLabel() {
        if (rdbtnModeDetail.isSelected() == true) {
            panel.traceViewMode = MultiWaveViewerPanel.TRACE_VIEW_MODE_DETAIL;
        }
        else if (rdbtnModeMerge.isSelected() == true) {
            panel.traceViewMode = MultiWaveViewerPanel.TRACE_VIEW_MODE_MERGE;
        }
        else if (rdbtnModeSpectrum.isSelected() == true) {
            panel.traceViewMode = MultiWaveViewerPanel.TRACE_VIEW_MODE_SPECT;
        }
        else if (rdbtnModeInfo.isSelected() == true) {
            panel.traceViewMode = MultiWaveViewerPanel.TRACE_VIEW_MODE_INFO;
        }
        panel.visibleAuto = chckbxAutoWaveVisible.isSelected();
        boolean cbEnable = true;
        if (chckbxAutoWaveVisible.isSelected() == true) {
            cbEnable = false;
        }
        else {
            cbEnable = true;
        }
        chckbxWave1.setEnabled(cbEnable);
        chckbxWave2.setEnabled(cbEnable);
        chckbxWave3.setEnabled(cbEnable);
        chckbxWave4.setEnabled(cbEnable);
        chckbxWave5.setEnabled(cbEnable);
        chckbxWave6.setEnabled(cbEnable);
        chckbxWave7.setEnabled(cbEnable);
        chckbxWave8.setEnabled(cbEnable);
        chckbxWave9.setEnabled(cbEnable);
        chckbxWave10.setEnabled(cbEnable);
        chckbxWave11.setEnabled(cbEnable);
        chckbxWave12.setEnabled(cbEnable);
        chckbxWave13.setEnabled(cbEnable);
        chckbxWave14.setEnabled(cbEnable);
        chckbxWave15.setEnabled(cbEnable);
        chckbxWave16.setEnabled(cbEnable);

        panel.visibleWave[0] = chckbxWave1.isSelected();
        panel.visibleWave[1] = chckbxWave2.isSelected();
        panel.visibleWave[2] = chckbxWave3.isSelected();
        panel.visibleWave[3] = chckbxWave4.isSelected();
        panel.visibleWave[4] = chckbxWave5.isSelected();
        panel.visibleWave[5] = chckbxWave6.isSelected();
        panel.visibleWave[6] = chckbxWave7.isSelected();
        panel.visibleWave[7] = chckbxWave8.isSelected();
        panel.visibleWave[8] = chckbxWave9.isSelected();
        panel.visibleWave[9] = chckbxWave10.isSelected();
        panel.visibleWave[10] = chckbxWave11.isSelected();
        panel.visibleWave[11] = chckbxWave12.isSelected();
        panel.visibleWave[12] = chckbxWave13.isSelected();
        panel.visibleWave[13] = chckbxWave14.isSelected();
        panel.visibleWave[14] = chckbxWave15.isSelected();
        panel.visibleWave[15] = chckbxWave16.isSelected();

        boolean isVisibleChckbxWave = true;
        if (rdbtnModeDetail.isSelected() == false) {
            isVisibleChckbxWave = false;
        }
        btnAllOn.setVisible(isVisibleChckbxWave);
        btnAllOff.setVisible(isVisibleChckbxWave);
        chckbxAutoWaveVisible.setVisible(isVisibleChckbxWave);
        chckbxWave1.setVisible(isVisibleChckbxWave);
        chckbxWave2.setVisible(isVisibleChckbxWave);
        chckbxWave3.setVisible(isVisibleChckbxWave);
        chckbxWave4.setVisible(isVisibleChckbxWave);
        chckbxWave5.setVisible(isVisibleChckbxWave);
        chckbxWave6.setVisible(isVisibleChckbxWave);
        chckbxWave7.setVisible(isVisibleChckbxWave);
        chckbxWave8.setVisible(isVisibleChckbxWave);
        chckbxWave9.setVisible(isVisibleChckbxWave);
        chckbxWave10.setVisible(isVisibleChckbxWave);
        chckbxWave11.setVisible(isVisibleChckbxWave);
        chckbxWave12.setVisible(isVisibleChckbxWave);
        chckbxWave13.setVisible(isVisibleChckbxWave);
        chckbxWave14.setVisible(isVisibleChckbxWave);
        chckbxWave15.setVisible(isVisibleChckbxWave);
        chckbxWave16.setVisible(isVisibleChckbxWave);

        if (midiInterface != null) {
            chckbxAutoOscChange.setSelected(midiInterface.isAutoSelectOscillator());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateLabel();
    }

    public void setWaveColorTable(Color[] waveColorTable) {
        panel.setWaveColorTable(waveColorTable);
    }

    @Override
    public void setVisible(boolean b) {
        if (b == false) {
            setupDialog.setVisible(false);
            tableEditer.setVisible(false);
        }
        super.setVisible(b);
    }

    public void repaintWavePane() {
        if (isVisible() == true) {
            panel.repaintWavePane();
        }
    }

    public void setAutoRepaintTask(boolean autoRepaint) {
        panel.autoRepaintTask = autoRepaint;
    }
}
