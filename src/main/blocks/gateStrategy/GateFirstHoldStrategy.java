package blocks.gateStrategy;

import MIDIBlocks.Note;

public class GateFirstHoldStrategy extends AbstractGateStrategy {
    
    public GateFirstHoldStrategy(float notesPerTick) {
        super(notesPerTick);
    }
    
    @Override
    public void update(Note note) {
        if (notesReceived < notesPerTick) {
            currentNote = note;
            queue.addNote(note);
            notesReceived++;
        }
    }

    @Override
    public String getName() {
        return "FIRST HOLD";
    }

}
