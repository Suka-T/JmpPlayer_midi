package jmp.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import jlib.gui.IJmpWindow;
import jlib.midi.IMidiUnit;
import jlib.midi.INotesMonitor;
import jmp.core.JMPCore;

public class NotesMonitorDialog extends JDialog implements IJmpWindow {

    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private JButton btnResetCount;

    /**
     * Create the dialog.
     */
    public NotesMonitorDialog() {
        setTitle("Notes Monitor");
        setBounds(100, 100, 450, 207);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        {
            table = new JTable();
            table.setEnabled(false);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setModel(new DefaultTableModel(
                new Object[][] {
                    {"BPM", ""},
                    {"PPQ", ""},
                    {"Number of Notes", ""},
                    {"Notes Count", ""},
                    {"NPS", ""},
                    {"Poryphony", null},
                },
                new String[] {
                    "Name", "Value"
                }
            ) {
                Class[] columnTypes = new Class[] {
                    String.class, String.class
                };
                public Class getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }
                boolean[] columnEditables = new boolean[] {
                    false, true
                };
                public boolean isCellEditable(int row, int column) {
                    return columnEditables[column];
                }
            });
            table.getColumnModel().getColumn(0).setPreferredWidth(143);
            table.getColumnModel().getColumn(1).setPreferredWidth(251);
            contentPanel.setLayout(new BorderLayout(0, 0));
            contentPanel.add(table);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                btnResetCount = new JButton("Reset Count");
                btnResetCount.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JMPCore.getSoundManager().getNotesMonitor().reset();
                    }
                });
                buttonPane.add(btnResetCount);
            }
        }
    }
    

    @Override
    public void showWindow() {
        setVisible(true);
    }

    @Override
    public void hideWindow() {
        setVisible(false);
    }

    @Override
    public boolean isWindowVisible() {
        return isVisible();
    }

    @Override
    public void setDefaultWindowLocation() {
    }

    @Override
    public void repaintWindow() {
        repaint();
        
        int ppq = 0;
        IMidiUnit midiUnit = JMPCore.getSoundManager().getMidiUnit();
        if (midiUnit.isValidSequence() == true) {
            ppq = midiUnit.getResolution();
        }
        
        INotesMonitor monitor = JMPCore.getSoundManager().getNotesMonitor();
        table.setValueAt(String.format("%.2f", midiUnit.getTempoInBPM()), 0, 1);
        table.setValueAt(String.format("%d", ppq), 1, 1);
        table.setValueAt(String.format("%d", monitor.getNumOfNotes()), 2, 1);
        table.setValueAt(String.format("%d", monitor.getNotesCount()), 3, 1);
        table.setValueAt(String.format("%.2f", monitor.getNps()), 4, 1);
        table.setValueAt(String.format("%d", monitor.getPolyphony()), 5, 1);
    }
    
    @Override
    public void updateLanguage() {
    }
}
