package blocks.arpeggiatorStrategy;

import java.util.ArrayList;

import MIDIBlocks.Note;
import MIDIBlocks.NoteHistory;

/**
 * Moves sequentially by MIDI number through the list of on notes
 * 
 * @author Aidan
 *
 */
public class ArpeggiatorAscending extends AbstractArpeggiatorStrategy {

    @Override
    public void update(NoteHistory history) {
        list.clear();
        ArrayList<Note> histNotes = history.getNotes();
        for (Note note : histNotes) {
            list.add(note);
        }
        if (nextIndex > list.size() && list.size() > 0) {
            nextIndex = list.size() - 1;
        }
        return;
    }

    @Override
    public String getName() {
        return "ASCENDING";
    }
    
    
}
