package MIDIBlocks;

import java.util.ArrayList;

/**
 * A list of notes to be played. Mainly implemented for use in the gate block
 * 
 * @author Aidan
 *
 */
public class NoteQueue {

    private ArrayList<Note> queue;

    public NoteQueue() {
        queue = new ArrayList<>();
    }

    /**
     * Add a note to the queue. If it is a note on, adds unimpeded. If it is a
     * note off, it will cancel the matching note on, or be added to the queue
     * 
     * @param note
     */
    public void addNote(Note note) {
        if (note.getStatus() == Note.NOTE_ON) {
            queue.add(note);
        } else if (note.getStatus() == Note.NOTE_OFF) {
            Note noteOn = new Note(Note.NOTE_ON, note.getPitch());
            if (queue.contains(noteOn)) {
                queue.remove(noteOn);
            }
        }
    }

    /**
     * Set the note at index to be this note. If index is greater than the size
     * of the queue, append it to the queue
     * 
     * @param index
     *            Position to set the note
     * @param note
     *            The note to be set
     */
    public void setNote(int index, Note note) {
        if (queue.size() > index) {
            queue.set(index, note);
        } else {
            queue.add(index, note);
        }
    }

    /**
     * Return the note at a given index, or null
     * 
     * @param index
     *            The position to get the note from
     * @return A note or null
     */
    public Note getNote(int index) {
        if (queue.size() > index && index >= 0) {
            return queue.get(index);
        } else {
            return null;
        }
    }

    /**
     * Attempt to remove a note at index from the queue
     * 
     * @param index
     */
    public void removeNote(int index) {
        queue.remove(index);
    }

    /**
     * Get the size of the queue
     * 
     * @return
     */
    public int size() {
        return queue.size();
    }

    /**
     * Returns true iff the queue contains a note with the same pitch, being on
     * or off
     * 
     * @param note
     * @return
     */
    public boolean contains(Note note) {
        if (note.getStatus() == Note.NOTE_OFF) {
            Note note2 = new Note(Note.NOTE_ON, note.getPitch());
            return queue.contains(note2);
        } else {
            return queue.contains(note);
        }
    }

    /**
     * Get all of the notes in the queue
     * 
     * @return A copy of the queue
     */
    public ArrayList<Note> getNotes() {
        ArrayList<Note> newQueue = new ArrayList<>();
        for (Note note : queue) {
            newQueue.add(note);
        }
        return newQueue;
    }
}
