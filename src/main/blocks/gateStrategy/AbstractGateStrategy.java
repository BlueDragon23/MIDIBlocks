package blocks.gateStrategy;

import MIDIBlocks.Note;
import MIDIBlocks.NoteQueue;

public abstract class AbstractGateStrategy {

    protected NoteQueue queue;
    protected Note currentNote;
    protected float notesPerTick;
    protected int notesReceived = 0;

    public AbstractGateStrategy(float notesPerTick) {
        queue = new NoteQueue();
        currentNote = null;
        this.notesPerTick = notesPerTick;
    }

    abstract public void update(Note note);

    public boolean isNext() {
        return queue.size() > 0;
    }

    /**
     * Retrieves the next note and removes it from the queue
     * 
     * @return
     */
    public Note getNext() {
        Note note = queue.getNote(0);
        if (note != null) {
            queue.removeNote(0);
        }
        currentNote = null;
        return note;
    }

    abstract public String getName();

    /**
     * Return true iff the note is in the queue
     * 
     * @param note
     * @return
     */
    public boolean contains(Note note) {
        return queue.contains(note);
    }

    /**
     * Reset the counter for number of notes received. Only useful for
     * first/last hold
     */
    public void resetReceived() {
        notesReceived = 0;
    }
}
