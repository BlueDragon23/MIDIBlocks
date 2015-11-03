package blocks;

import java.util.ArrayList;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

public class ChordifyBlock extends AbstractBlock {

    public ChordifyBlock(MusicManager musicManager) {
        super(musicManager);
    }

    @Override
    public ArrayList<Note> modify(Note input) {
        ArrayList<Note> notes = new ArrayList<>();
        int noteIndex = musicManager.getScale().indexOf(input.getPitch());
        if (noteIndex != -1) {
            /* If the note is in the current scale */
            notes.add(new Note(input.getStatus(), input.getPitch(), input
                            .getVelocity(), musicManager.getClient().getTime()));
            notes.add(new Note(input.getStatus(), musicManager.getScale().get(
                            Math.min(noteIndex + 2,
                                            musicManager.getScale().size() - 1)),
                            input.getVelocity(), musicManager.getClient().getTime()));
            notes.add(new Note(input.getStatus(), musicManager.getScale().get(
                            Math.min(noteIndex + 4,
                                            musicManager.getScale().size() - 1)),
                            input.getVelocity(), musicManager.getClient().getTime()));
        }
        return notes;
    }

    /**
     * Does nothing
     */
    @Override
    public void setParams(String[] params) {
        return;
    }

    /**
     * Returns empty array, as this block has no parameters
     * 
     * @return an empty array
     */
    @Override
    public String[] getParams() {
        return new String[] {};
    }

    @Override
    public String[] getParamNames() {
        return new String[] {};
    }

    @Override
    public String getName() {
        return "chordify";
    }

}
