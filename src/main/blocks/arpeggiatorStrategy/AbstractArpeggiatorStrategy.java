package blocks.arpeggiatorStrategy;

import java.util.ArrayList;

import MIDIBlocks.Note;
import MIDIBlocks.NoteHistory;

public abstract class AbstractArpeggiatorStrategy {
    
    protected ArrayList<Note> list;
    
    protected Note currentNote;
    protected int nextIndex = 0;
    
    public AbstractArpeggiatorStrategy() {
        list = new ArrayList<>();
    }
    
    abstract public void update(NoteHistory history);
    
    public Note getNextNote() {
        if (list.size() > 0) {
            if (nextIndex > list.size() - 1) {
                nextIndex = 0;
            }
            Note retNote = list.get(nextIndex);
            nextIndex = (nextIndex + 1) % list.size();
            currentNote = new Note(Note.NOTE_ON, retNote.getPitch());
            retNote = new Note(retNote.getStatus(), retNote.getPitch(), retNote.getVelocity());
            return retNote;
        }
        return null;
    }
    
    public Note getCurrentNote() {
        return currentNote;
    }
    
    public void removeCurrentNote() {
        currentNote = null;
    }
    
    abstract public String getName();
}
