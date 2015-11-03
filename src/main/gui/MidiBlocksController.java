package gui;

import io.InvalidFileException;
import io.MidiReceiver;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.Enumeration;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import MIDIBlocks.MidiBlocksClient;

public class MidiBlocksController {

    private MidiBlocksClient client;
    public SourceActionListener sourceListener;
    public OutputActionListener outListener;
    public DisabledSelectionModel disabledModel;
    public BlockTransferHandler transferHandler;
    public ScaleSelection scaleListener;

    /**
     * Controller for the MIDIBlocks system
     * 
     * @param client
     *            the model of the program
     */
    public MidiBlocksController(MidiBlocksClient client) {
        this.client = client;
        sourceListener = new SourceActionListener();
        outListener = new OutputActionListener();
        disabledModel = new DisabledSelectionModel();
        transferHandler = new BlockTransferHandler();
        scaleListener = new ScaleSelection();
    }

    @SuppressWarnings("serial")
    private class SourceActionListener extends AbstractAction {

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Only accept MIDI files", "mid");

        public SourceActionListener() {
            fileChooser.setFileFilter(filter);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand() == null) {
                return;
            } else if (e.getActionCommand().equals("File")) {
                /* Create a prompt for a filename */
                int retVal = fileChooser.showOpenDialog(null);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        client.setSource("FILE",
                                        fileChooser.getSelectedFile()
                                                        .getAbsolutePath());
                    } catch (InvalidFileException error) {
                        JOptionPane.showMessageDialog(null, "Invalid file");
                        resetSelected((JToggleButton) e.getSource());
                    }
                } else {
                    resetSelected((JToggleButton) e.getSource());
                }
            } else if (e.getActionCommand().equals("Device")) {
                JToggleButton button = (JToggleButton) e.getSource();
                if (button.isSelected()) {
                    /* Try and find a valid MIDI input */
                    Info[] infos = MidiSystem.getMidiDeviceInfo();
                    Info info = (Info) JOptionPane.showInputDialog(null, "Please choose a MIDI device as input", "Device Selection", JOptionPane.QUESTION_MESSAGE, UIManager.getIcon("OptionPane.questionIcon"), infos, null);
                    if (info == null) {
                        resetSelected(button);
                        return;
                    }
                    try {
                        MidiDevice device = MidiSystem.getMidiDevice(info);
                        device.getTransmitter().setReceiver(new MidiReceiver(client.getMusicManager()));
                        client.setSource("KEYBOARD");
                        device.open();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Device is unavailable", "Unavailable Device", JOptionPane.ERROR_MESSAGE);
                        resetSelected(button);
                    }
                }
            } else if (e.getActionCommand().equals("Virtual Keyboard")) {
                client.setSource("DRIVER");
            }
        }

        private void resetSelected(JToggleButton button) {
            ButtonGroup group = ((DefaultButtonModel) button.getModel()).getGroup();
            group.clearSelection();
            Enumeration<AbstractButton> buttons = group.getElements();
            AbstractButton abButton;
            String source = client.getSource().split(" ")[0];
            while (buttons.hasMoreElements()) {
                abButton = buttons.nextElement();
                if (abButton.getText().equals("File") && source.equals("FILE")) {
                    group.setSelected(abButton.getModel(), true);
                    break;
                } else if (abButton.getText().equals("Device") && source.equals("DEVICE")) {
                    group.setSelected(abButton.getModel(), true);
                    break;
                } else if (abButton.getText().equals("Virtual Keyboard") && source.equals("KEYBOARD")) {
                    group.setSelected(abButton.getModel(), true);
                    break;
                }
            }
            return;
        }
    }

    @SuppressWarnings("serial")
    private class ScaleSelection extends AbstractAction {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Please select a csv document", "csv");

        public ScaleSelection() {
            fileChooser.setFileFilter(filter);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int retVal = fileChooser.showOpenDialog(null);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                boolean result = client.getMusicManager().loadScales(fileChooser.getSelectedFile()
                                .getAbsolutePath());
                if (!result) {
                    JOptionPane.showMessageDialog(null, "Invalid scales file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @SuppressWarnings("serial")
    private class OutputActionListener extends AbstractAction {

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Only accept MIDI files", "mid");

        public OutputActionListener() {
            fileChooser.setFileFilter(filter);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand() == null) {
                return;
            } else if (e.getActionCommand() == "File") {
                boolean request = ((JToggleButton) e.getSource()).isSelected();
                if (request) {
                    int retVal = fileChooser.showOpenDialog(null);
                    if (retVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            boolean result = client.setOutput("FILE", fileChooser.getSelectedFile()
                                            .getAbsolutePath());
                            if (result != request) {
                                JOptionPane.showMessageDialog(null, "Invalid file",
                                                "Invalid file",
                                                JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (InvalidFileException error) {
                            JOptionPane.showMessageDialog(null, "Invalid file",
                                            "Invalid file",
                                            JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    client.setOutput("FILE", false);
                    if (client.getOutput().isEmpty()) {
                        client.getMusicManager().setNotified(false);
                    }
                }
            } else if (e.getActionCommand() == "Synth") {
                boolean request = ((JToggleButton) e.getSource()).isSelected();
                boolean success = client.setOutput("SYNTH", request);
                if (client.getOutput().isEmpty()) {
                    client.getMusicManager().setNotified(false);
                }
                ((JToggleButton) e.getSource()).setSelected(success);
                if (request && !success) {
                    JOptionPane.showMessageDialog(null,
                                    "No serial ports found",
                                    "Error Connecting",
                                    JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class BlockTransferable implements Transferable {

        private JLabel label;

        public BlockTransferable(JLabel label) {
            this.label = label;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                        throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor)) {
                return label.getIcon();
            } else if (flavor.equals(DataFlavor.stringFlavor)) {
                return label.getText();
            } else {
                return null;
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = { DataFlavor.imageFlavor,
                    DataFlavor.stringFlavor };
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = this.getTransferDataFlavors();
            for (DataFlavor flav : flavors) {
                if (flav.equals(flavor)) {
                    return true;
                }
            }
            return false;
        }

    }

    @SuppressWarnings("serial")
    private class BlockTransferHandler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent cmp) {
            if (cmp instanceof JLabel) {
                return COPY;
            } else if (cmp instanceof JList) {
                return MOVE;
            } else {
                return NONE;
            }
        }

        @Override
        public Transferable createTransferable(JComponent cmp) {
            return new BlockTransferable((JLabel) cmp);
        }

        @Override
        public void exportAsDrag(JComponent comp, InputEvent e, int action) {
            super.exportAsDrag(comp, e, action);
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport supp) {
            if (supp.getComponent() instanceof JList && supp.isDrop()
                            && supp.getSourceDropActions() == COPY) {
                return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean importData(TransferHandler.TransferSupport supp) {
            if (!this.canImport(supp)) {
                return false;
            }
            JList<JLabel> list = (JList<JLabel>) supp.getComponent();
            DefaultListModel<JLabel> listModel = (DefaultListModel<JLabel>) list
                            .getModel();
            JList.DropLocation dl = (JList.DropLocation) supp.getDropLocation();
            int index = dl.getIndex();
            boolean insert = dl.isInsert();

            Icon icon;
            try {
                icon = (Icon) supp.getTransferable().getTransferData(
                                DataFlavor.imageFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
                return false;
            }
            if (insert) {
                listModel.add(index, new JLabel(icon));
            } else {
                listModel.add(index, new JLabel(icon));
            }
            return true;
        }
    }

    @SuppressWarnings("serial")
    private class DisabledSelectionModel extends DefaultListSelectionModel {

        @Override
        public void addSelectionInterval(int index0, int index1) {
            /* Do nothing */
            ;
        }

        @Override
        public boolean isSelectionEmpty() {
            return true;
        }

        @Override
        public boolean isSelectedIndex(int index) {
            return false;
        }

    }
}
