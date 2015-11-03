package blocks.arpeggiatorStrategy;

import java.util.Random;

import MIDIBlocks.Note;
import MIDIBlocks.NoteHistory;

public class ArpeggiatorRandom extends AbstractArpeggiatorStrategy {
    
    private int previous = -1;

    @Override
    public void update(NoteHistory history) {
        list = history.getNotes();
    }

    @Override
    public String getName() {
        return "RANDOM";
    }
    
    @Override
    public Note getNextNote() {
        if (list.size() > 0) {
            Random random = new Random();
            int randomInt = 0;
            do {
                randomInt = random.nextInt(list.size());
            } while (randomInt == previous && list.size() > 1);
            previous = randomInt;
            currentNote = new Note(Note.NOTE_ON, list.get(randomInt).getPitch());
            Note retNote = list.get(randomInt);
            return new Note(retNote.getStatus(), retNote.getPitch(), retNote.getVelocity(), System.currentTimeMillis());
        }
        return null;
    }

}
