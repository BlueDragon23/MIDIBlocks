package MIDIBlocks;

import java.util.ArrayList;

/**
 * Tracks all currently on notes
 * 
 * @author Aidan
 *
 */
public class NoteHistory {

    private ArrayList<Note> history;

    /**
     * Create a new NoteHistory
     */
    public NoteHistory() {
        history = new ArrayList<Note>();
    }

    /**
     * Add a note to the end of the history
     * 
     * @param note
     */
    public void addNote(Note note) {
        if (note.getStatus() == Note.NOTE_ON) {
            history.add(note);
        }
    }

    /**
     * Remove an identical note from the list
     * 
     * @param note
     */
    public void removeNote(Note note) {
        for (Note histNote : history) {
            if (note.getStatus() == Note.NOTE_ON
                            && (histNote.getPitch() == note.getPitch())) {
                history.remove(histNote);
                break;
            }
        }
    }

    /**
     * Get the current number of elements in the history
     * 
     * @return
     */
    public int size() {
        return history.size();
    }

    /**
     * Get the Note at a particular index. Throws IndexOutOfBounds exception if
     * the index isn't in the list
     * 
     * @param index
     * @return
     */
    public Note get(int index) {
        return history.get(index);
    }

    /**
     * Get all of the notes in the history
     * 
     * @return
     */
    public ArrayList<Note> getNotes() {
        ArrayList<Note> retNotes = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            retNotes.add(history.get(i));
        }
        return retNotes;
    }

    /**
     * Set the note at index to be element
     * 
     * @param index
     *            the index to change
     * @param element
     *            the element to be added
     */
    public void set(int index, Note element) {
        history.set(index, element);
    }

    /**
     * Check whether the history contains this note. Always false for off notes
     * 
     * @param note
     * @return
     */
    public boolean contains(Note note) {
        return history.contains(note);
    }

    /**
     * Sort the history, from smallest to largest pitch
     */
    public void sort() {
        history.sort(null);
    }

}
