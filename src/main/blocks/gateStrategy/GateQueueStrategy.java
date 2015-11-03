package blocks.gateStrategy;

import MIDIBlocks.Note;

public class GateQueueStrategy extends AbstractGateStrategy {
    
    public GateQueueStrategy(float notesPerTick) {
        super(notesPerTick);
    }

    @Override
    public void update(Note note) {
        /* Appends note to the end of the queue */
        queue.addNote(note);
    }

    @Override
    public String getName() {
        return "QUEUE";
    }

}
