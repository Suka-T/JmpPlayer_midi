package jmsynth.app.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import jmsynth.JMSoftSynthesizer;
import jmsynth.JMSynthFile;
import jmsynth.midi.ProgramChangeTable;
import jmsynth.oscillator.OscillatorSet;

public class ProgramChangeTableEditDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JList<String> list;
    private DefaultListModel<String> model;

    private ProgramChangeTable pcTable = null;
    private JMSoftSynthesizer synth;
    private JSpinner spinner;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ProgramChangeTableEditDialog dialog = new ProgramChangeTableEditDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public ProgramChangeTableEditDialog() {
        setResizable(false);
        setBounds(100, 100, 450, 512);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        model = new DefaultListModel<String>();

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 269, 432);
        contentPanel.add(scrollPane);
        list = new JList<String>(model);
        scrollPane.setViewportView(list);

        spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(1, 1, 16, 1));
        spinner.setBounds(366, 9, 55, 20);
        contentPanel.add(spinner);

        JButton btnToSynth = new JButton("Select to synth→");
        btnToSynth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int ch = (int) spinner.getValue() - 1;
                int no = list.getSelectedIndex();
                if (no < 0) {
                    return;
                }
                tableToSynth(ch, no);
            }
        });
        btnToSynth.setBounds(293, 39, 139, 21);
        contentPanel.add(btnToSynth);

        JButton btnSynthToTable = new JButton("←Synth to select");
        btnSynthToTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int ch = (int) spinner.getValue() - 1;
                int no = list.getSelectedIndex();
                if (no < 0) {
                    return;
                }
                synthToTable(ch, no);
            }
        });
        btnSynthToTable.setBounds(293, 70, 139, 21);
        contentPanel.add(btnSynthToTable);

        JLabel lblNewLabel = new JLabel("ch");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setBounds(304, 12, 50, 13);
        contentPanel.add(lblNewLabel);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File ret = null;

                JFileChooser filechooser = new JFileChooser();
                File f = new File(new File("config." + "pctbl").getAbsolutePath());
                filechooser.setSelectedFile(f);

                int selected = filechooser.showSaveDialog(ProgramChangeTableEditDialog.this);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    ret = filechooser.getSelectedFile();
                }
                if (ret != null) {
                    output(ret);
                }
            }
        });
        btnSave.setBounds(293, 421, 67, 21);
        contentPanel.add(btnSave);

        JButton buttonLoad = new JButton("Load");
        buttonLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File ret = null;

                // 確認ダイアログ
                JFileChooser filechooser = new JFileChooser(new File(""));
                int selected = filechooser.showOpenDialog(ProgramChangeTableEditDialog.this);
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        ret = filechooser.getSelectedFile();
                        break;
                    default:
                        break;
                }
                if (ret != null) {
                    input(ret);
                }
            }
        });
        buttonLoad.setBounds(366, 421, 67, 21);
        contentPanel.add(buttonLoad);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    public void openDlg(ProgramChangeTable tbl, JMSoftSynthesizer synth) {
        pcTable = tbl;
        this.synth = synth;
        resetList();
        setVisible(true);
    }

    public void resetList() {
        model.clear();

        for (int i = 0; i < ProgramChangeTable.PC_MAX; i++) {
            String row = toOscStr(i, pcTable.getOscillatorSet(i));
            model.addElement(row);
        }
    }

    private void synthToTable(int ch, int no) {
//        OscillatorSet set = new OscillatorSet(synth.getEnvelope(ch).getAttackTime(), synth.getEnvelope(ch).getDecayTime(),
//                synth.getEnvelope(ch).getSustainLevel(), synth.getEnvelope(ch).getReleaseTime(), synth.getEnvelope(ch).getMaxAttackMills(),
//                synth.getEnvelope(ch).getMaxDecayMills(), synth.getEnvelope(ch).getMaxReleaseMills(), synth.getWaveType(ch, 0));
//        pcTable.setOscillatorSet(no, set);
        resetList();
    }

    private void tableToSynth(int ch, int no) {
        OscillatorSet set = pcTable.getOscillatorSet(no);
        synth.getEnvelope(ch).setAttackTime(set.getAttackTime());
        synth.getEnvelope(ch).setDecayTime(set.getDecayTime());
        synth.getEnvelope(ch).setSustainLevel(set.getSustainLevel());
        synth.getEnvelope(ch).setReleaseTime(set.getReleaseTime());
        synth.getEnvelope(ch).setMaxAttackMills(set.getMaxAttackMills());
        synth.getEnvelope(ch).setMaxDecayMills(set.getMaxDecayMills());
        synth.getEnvelope(ch).setMaxReleaseMills(set.getMaxReleaseMills());
        synth.setOscillator(ch, set);
    }

    private void setTableValue(int no) {
        String row = model.elementAt(no);
        String[] values = row.split(",");

        int i = 0;
        OscillatorSet set = pcTable.getOscillatorSet(no);
        i++;
        //set.setOscillators(JMSynthFile.toWaveType(values[i]));
        i++;
        set.setAttackTime(Double.parseDouble(values[i]));
        i++;
        set.setDecayTime(Double.parseDouble(values[i]));
        i++;
        set.setSustainLevel(Double.parseDouble(values[i]));
        i++;
        set.setReleaseTime(Double.parseDouble(values[i]));
        i++;
        set.setMaxAttackMills(Long.parseLong(values[i]));
        i++;
        set.setMaxDecayMills(Long.parseLong(values[i]));
        i++;
        set.setMaxReleaseMills(Long.parseLong(values[i]));
    }

    private OscillatorSet toStrOsc(String str) {
        int i = 0;
        String[] values = str.split(",");
        OscillatorSet set = new OscillatorSet();
        i++;
        //set.setOscillators(JMSynthFile.toWaveType(values[i]));
        i++;
        set.setAttackTime(Double.parseDouble(values[i]));
        i++;
        set.setDecayTime(Double.parseDouble(values[i]));
        i++;
        set.setSustainLevel(Double.parseDouble(values[i]));
        i++;
        set.setReleaseTime(Double.parseDouble(values[i]));
        i++;
        set.setMaxAttackMills(Long.parseLong(values[i]));
        i++;
        set.setMaxDecayMills(Long.parseLong(values[i]));
        i++;
        set.setMaxReleaseMills(Long.parseLong(values[i]));
        return set;
    }

    private String toOscStr(int no, OscillatorSet set) {
        String str = "";
        str += no;
        str += ",";
        str += JMSynthFile.toWaveStr(set.getOscillator());
        str += ",";
        str += set.getAttackTime();
        str += ",";
        str += set.getDecayTime();
        str += ",";
        str += set.getSustainLevel();
        str += ",";
        str += set.getReleaseTime();
        str += ",";
        str += set.getMaxAttackMills();
        str += ",";
        str += set.getMaxDecayMills();
        str += ",";
        str += set.getMaxReleaseMills();
        return str;
    }

    private void output(File f) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(f.getPath(), "UTF-8");
            for (int i = 0; i < model.getSize(); i++) {
                pw.println(model.getElementAt(i));
            }
            pw.close();
        }
        catch (Exception fnfe) {
        }
        finally {
            pw = null;
        }
    }

    private void input(File f) {

        BufferedReader reader;

        try {
            FileInputStream fs = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
            reader = new BufferedReader(isr);

            // ファイルを読み込む
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] sLine = line.split(",");
                int no = Integer.parseInt(sLine[0]);
                OscillatorSet set = toStrOsc(line);
                pcTable.setOscillatorSet(no, set);
            }
            reader.close();
        }
        catch (IOException ioe) {
        }
        finally {
            reader = null;
        }

        resetList();
    }
}
