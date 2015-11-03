package blocks.arpeggiatorStrategy;

import java.util.ArrayList;
import java.util.Collections;

import MIDIBlocks.Note;
import MIDIBlocks.NoteHistory;

/**
 * Moves sequentially by MIDI number through the list of on notes in reverse
 * 
 * @author Aidan
 *
 */
public class ArpeggiatorDescending extends AbstractArpeggiatorStrategy {

    @Override
    public void update(NoteHistory history) {
        list.clear();
        ArrayList<Note> histNotes = history.getNotes();
        Collections.reverse(histNotes);
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
        return "DESCENDING";
    }

}
