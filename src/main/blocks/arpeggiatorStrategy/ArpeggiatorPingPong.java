package blocks.arpeggiatorStrategy;

import java.util.ArrayList;
import java.util.Collections;

import MIDIBlocks.Note;
import MIDIBlocks.NoteHistory;

public class ArpeggiatorPingPong extends AbstractArpeggiatorStrategy {

    @Override
    public void update(NoteHistory history) {
        list.clear();
        ArrayList<Note> histNotes = history.getNotes();
        for (Note note : histNotes) {
            list.add(note);
        }
        Collections.reverse(histNotes);
        for (Note note : histNotes) {
            list.add(note);
        }
        if (list.size() > 0) {
            list.remove(list.size() - 1);
            if (list.size() > 2) {
                list.remove(list.size() / 2);
            }
        }
        if (nextIndex > list.size() && list.size() > 0) {
            nextIndex = list.size() - 1;
        }
        return;
    }

    @Override
    public String getName() {
        return "PING PONG";
    }

}
