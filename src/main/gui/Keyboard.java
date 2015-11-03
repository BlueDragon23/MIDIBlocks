package gui;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Keyboard extends JPanel {

    private PianoKey[] keys;
    private MusicManager client;
    private int rootPos;
    private ArrayList<Integer> rootNotes;

    /**
     * Create a new keyboard and fill it with keys
     * 
     * @param client
     */
    public Keyboard(MusicManager client) {
        this.client = client;
        GridBagLayout gbl_keyboard = new GridBagLayout();
        this.setLayout(gbl_keyboard);
        keys = new PianoKey[15];
        rootNotes = new ArrayList<>();
        setRootNotes(client.getScale().get(0));
        this.rootPos = client.getScale().indexOf(48);
        GridBagConstraints keyConstraints = new GridBagConstraints();
        keyConstraints.insets = new Insets(0, 0, 0, 0);
        keyConstraints.anchor = GridBagConstraints.WEST;
        keyConstraints.weightx = 1.0;
        keyConstraints.weighty = 1.0;
        keyConstraints.fill = GridBagConstraints.BOTH;
        keyConstraints.gridy = 0;
        for (int i = 0; i < 15; i++) {
            keys[i] = new PianoKey(client, i);
            /* Default to C4 */

            keys[i].setNote(48 + i);
            if ((48 + i) % 12 == 0) {
                keys[i].setRoot(true);
            }
            keyConstraints.gridx = i;
            this.add(keys[i], keyConstraints);
        }
    }

    /**
     * Enable or disable the keyboard
     * 
     * @param disabled
     *            is the keyboard disable
     */
    public void setDisabled(boolean disabled) {
        for (PianoKey key : keys) {
            key.setDisabled(disabled);
        }
    }

    /**
     * The index in the scale for the root note
     * 
     * @return
     */
    public int getRootPos() {
        return rootPos;
    }

    /**
     * Set the first note on the keyboard to be the note at index @pos in the
     * scale
     * 
     * @param pos
     */
    public void setRootPos(int pos) {
        rootPos = pos;
        ArrayList<Integer> scale = client.getScale();
        for (int i = 0; i < 15; i++) {
            if (rootPos + i < scale.size() && rootPos + i >= 0) {
                keys[i].setNote(scale.get(rootPos + i));
                if (rootNotes.contains(scale.get(rootPos + i))) {
                    /* Label it */
                    keys[i].setRoot(true);
                } else {
                    /* Unlabel it */
                    keys[i].setRoot(false);
                }
            } else {
                keys[i].setNote(0);
            }
        }
        client.getClient().hardwareConfig();
    }

    /**
     * Adjust the keyboard to use the new scale. Should display approximately
     * the centre of the playable range
     * 
     * @param rootNote
     *            the new root note of the scale
     */
    public void setScale(int rootNote) {
        setRootNotes(rootNote);
        /* Display around the middle of the keyboard */
        setRootPos(client.getScale().size() / 2 - client.getOctave());
    }

    /**
     * Add new root notes
     * 
     * @param root
     *            The lowest valid root note
     */
    private void setRootNotes(int root) {
        rootNotes.clear();
        for (int i = root; i < Note.LAST_NOTE; i += 12) {
            rootNotes.add(i);
        }
    }

    /**
     * Get the first eight notes being displayed on the keyboard
     * 
     * @return
     */
    public ArrayList<Integer> getFirstEightNotes() {
        ArrayList<Integer> notes = new ArrayList<>();
        int firstNote;
        if (rootPos < 0) {
            firstNote = client.getScale().get(0);
        } else if (rootPos > client.getScale().size()) {
            firstNote = client.getScale().get(client.getScale().size() - 9);
        } else {
            int first = 0;
            for (int i = 0; i < 15; i++) {
                if (rootNotes.contains(keys[i].getNote())) {
                    first = i;
                    break;
                }
            }
            firstNote = keys[first].getNote();
        }
        ArrayList<Integer> scale = client.getScale();
        int noteIndex = scale.indexOf(firstNote);
        for (int i = noteIndex; i < noteIndex + 8; i++) {
            if (i < scale.size()) {
                notes.add(scale.get(i));
            } else {
                notes.add(scale.get(scale.size() - 1));
            }
        }
        return notes;
    }

}
