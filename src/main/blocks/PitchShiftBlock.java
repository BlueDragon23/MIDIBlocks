package blocks;

import java.util.ArrayList;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

/**
 * Pitch Shift block shifts the pitch of every note that comes through by a
 * certain amount. It also locks these notes to the currently set scale. 
 * 
 * @author Aidan
 *
 */
public class PitchShiftBlock extends AbstractBlock {

    private int shift;

    /**
     * Create a new PitchShiftBlock with a default shift of 0
     * 
     * @param musicManager
     */
    public PitchShiftBlock(MusicManager musicManager) {
        super(musicManager);
        /* Default shift */
        this.shift = 0;
    }

    /**
     * Create a new PitchShiftBlock with an initial shift shift
     * 
     * @param musicManager
     * @param shift
     *            the initial shift setting
     */
    PitchShiftBlock(MusicManager musicManager, int shift) {
        super(musicManager);
        this.shift = shift;
    }

    /**
     * Change the note pitch so that the new pitch is /old + shift rounded up to
     * the nearest pitch
     * 
     * @param input
     *            the input note to be shifted
     */
    @Override
    public ArrayList<Note> modify(Note input) {
        ArrayList<Integer> scale = musicManager.getScale();
        int pitch = input.getPitch();
        pitch += shift;
        if (!scale.contains(pitch)) {
            /* Round */
            int newPitch = 0;
            for (int i = 0; i < scale.size() - 1; i++) {
                if (pitch > scale.get(i)) {
                    newPitch = (pitch - scale.get(i)) < (pitch - scale
                                    .get(i + 1)) ? scale.get(i) : scale
                                    .get(i + 1);
                }
            }
            if (newPitch == 0) {
                scale.get(scale.size() - 1);
            } else {
                pitch = newPitch;
            }
        }
        /*
         * Works for adjusting by scaled pitches int index =
         * scale.indexOf(pitch); index += shift;
         */
        ArrayList<Note> retNotes = new ArrayList<>();
        pitch = Math.max(Math.min(pitch, scale.get(scale.size() - 1)),
                        scale.get(0));
        Note retNote = new Note(input.getStatus(), pitch, input.getVelocity(), musicManager.getClient().getTime());
        retNotes.add(retNote);
        return retNotes;
    }

    /**
     * Set the shift of the block. Only accepts arrays of length 1, and the
     * String must be a valid number
     * 
     * @param params
     *            array containing the desired shift as the only element
     */
    public void setParams(String[] params) {
        if (params.length == 1) {
            this.shift = Integer.parseInt(params[0]);
        }
    }

    /**
     * Retrieve the shift setting for the block
     * 
     * @return an array, with the shift of this block as the only element
     */
    public String[] getParams() {
        return new String[] { Integer.toString(shift) };
    }

    /**
     * Retrieve the name of the setting for this block
     */
    public String[] getParamNames() {
        return new String[] { "Shift" };
    }

    public String getName() {
        return "pitchShift";
    }
}
