package blocks;

import java.util.ArrayList;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

/**
 * This block only allows one note to be on at any given time. If a note is
 * played while no other notes are on, this block should just pass it on. If a
 * note is played and another note is already on, it should output a note off
 * for the previous note and then pass the new note through. If it receives a
 * note off for a note that is on, that also passes through. If it receives a
 * different note off, that is discarded.
 * 
 * @author Aidan
 *
 */
public class MonophonicBlock extends AbstractBlock {

    public MonophonicBlock(MusicManager musicManager) {
        super(musicManager);
    }

    @Override
    public ArrayList<Note> modify(Note input) {
        ArrayList<Note> retNotes = new ArrayList<Note>();
        if (input.getStatus() == Note.NOTE_ON) {
            if (history.size() == 0) {
                retNotes.add(input);
                history.addNote(input);
            } else {
                retNotes.add(new Note(Note.NOTE_OFF, history.get(0).getPitch(),
                                history.get(0).getVelocity()));
                retNotes.add(input);
                history.set(0, input);
            }
        } else if (input.getStatus() == Note.NOTE_OFF && history.size() == 1) {
            if (history.get(0).getPitch() == input.getPitch()) {
                retNotes.add(input);
                history.removeNote(history.get(0));
            }
        }
        return retNotes;
    }

    @Override
    public void setParams(String[] params) {
        return;
    }

    @Override
    public String[] getParams() {
        return new String[] {};
    }

    @Override
    public String[] getParamNames() {
        return new String[] {};
    }

    public String getName() {
        return "monophonic";
    }

}
