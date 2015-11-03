package MIDIBlocks;

import gui.Keyboard;
import io.CSVReader;

import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 * MusicManager controls the musical elements for the client. This includes
 * scales, playing notes and the connection to the keyboard
 * 
 * @author Aidan
 *
 */
public class MusicManager {

    private CSVReader csvReader;
    private ArrayList<ArrayList<String>> scales;
    private ArrayList<String> currentScale;
    private ArrayList<Integer> currentScaleNums;
    private String mode;
    private String rootNote;
    private DefaultComboBoxModel<String> modes;
    private Keyboard keyboard;
    private boolean notified = false;
    private MidiBlocksClient client;
    private BlockManager blockManager;

    public MusicManager(MidiBlocksClient client, BlockManager blockManager) {
        csvReader = CSVReader.getInstance();
        currentScaleNums = new ArrayList<>();
        rootNote = "C";
        modes = new DefaultComboBoxModel<String>();
        this.client = client;
        this.blockManager = blockManager;
    }

    /**
     * Load scales to be used in the system
     * 
     * @param filename
     *            the scales csv
     */
    public boolean loadScales(String filename) {
        try {
            ArrayList<ArrayList<String>> temp = csvReader.readFile(filename);
            if (temp.size() > 0) {
                this.scales = temp;
                modes.removeAllElements();
                for (String mode : getModeNames()) {
                    modes.addElement(mode);
                }
                setScale(scales.get(0).get(0), scales.get(0).get(1));
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the name of possible modes from the loaded scales
     * 
     * @return
     */
    public ArrayList<String> getModeNames() {
        ArrayList<String> modeNames = new ArrayList<>();
        for (ArrayList<String> scale : scales) {
            if (!modeNames.contains(scale.get(0))) {
                modeNames.add(scale.get(0));
            }
        }
        return modeNames;
    }

    /**
     * Change the scale for the keyboard to a new scale, in the mode @mode and
     * the root note @root.
     * 
     * @param mode
     *            the mode for the new scale
     * @param root
     *            the root for the new scale
     */
    public void setScale(String mode, String root) {
        /* Loop over all of the scales and try to find one that matches inputs */
        for (ArrayList<String> scale : scales) {
            if (scale.get(0).equals(mode) && scale.get(1).equals(root)) {
                this.currentScale = new ArrayList<String>(scale.subList(1,
                                scale.size()));
                currentScaleNums.clear();
                int scaling = Note.FIRST_NOTE;
                while (scaling < Note.LAST_NOTE - 12) {
                    for (int i = 0; i < currentScale.size() - 1; i++) {
                        if (currentScaleNums.contains(Note
                                        .getNoteNum(currentScale.get(i))
                                        + scaling)) {
                            scaling += 12;
                        }
                        currentScaleNums.add(Note.getNoteNum(currentScale
                                        .get(i)) + scaling);
                    }
                }
                currentScaleNums.sort(null);
                if (keyboard != null) {
                    keyboard.setScale(Note.getNoteNum(currentScale.get(0))
                                    + Note.FIRST_NOTE);
                }
                this.mode = mode;
                this.rootNote = root;
            }
        }
    }

    /**
     * 
     * @return the keyboard associated with the client
     */
    public Keyboard getKeyboard() {
        return this.keyboard;
    }

    /**
     * Add a keyboard to the client
     * 
     * @param keyboard
     */
    public void setKeyboard(Keyboard keyboard) {
        if (this.keyboard == null) {
            this.keyboard = keyboard;
        }
    }

    /**
     * Get the currently set scale
     * 
     * @return
     */
    public ArrayList<Integer> getScale() {
        return this.currentScaleNums;
    }

    /**
     * Get the amount of notes for an octave in the current scale
     * 
     * @return the number of notes in one octave
     */
    public int getOctave() {
        return this.currentScale.size() - 1;
    }

    /**
     * Get the currently set mode
     * 
     * @return
     */
    public String getMode() {
        return mode;
    }

    /**
     * Get the currently set root note
     * 
     * @return
     */
    public String getRootNote() {
        return rootNote;
    }

    public ComboBoxModel<String> getModeModel() {
        return modes;
    }

    /**
     * Whether the user has been notified of input with no output
     * 
     * @return
     */
    public boolean isNotified() {
        return notified;
    }

    /**
     * Set whether the user has been notified about no output
     * 
     * @param b
     */
    public void setNotified(boolean b) {
        notified = b;
    }

    /**
     * Passes a note into the processing line to be played
     * 
     * @param note
     */
    public void playNote(Note note) {
        playNote(note, 0);
    }

    /**
     * Passes a note into the processing line to be played at a given index
     * 
     * @param note
     * 
     * @param index
     *            the index of the block to start processing at
     */
    public void playNote(Note note, int index) {
        blockManager.get(index).process(
                        new Note(note.getStatus(), note.getPitch(), note
                                        .getVelocity(), note.getTick()));
    }

    /**
     * Moves a note directly to output
     * 
     * @param note
     */
    public void quickNote(Note note) {
        playNote(note, blockManager.size() - 1);
    }

    public MidiBlocksClient getClient() {
        return client;
    }
}
