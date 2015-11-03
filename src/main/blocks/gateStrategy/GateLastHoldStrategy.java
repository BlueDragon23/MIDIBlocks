package blocks.gateStrategy;

import MIDIBlocks.Note;

public class GateLastHoldStrategy extends AbstractGateStrategy {
    
    public GateLastHoldStrategy(float notesPerTick) {
        super(notesPerTick);
    }

    @Override
    public void update(Note note) {
        if (note.getStatus() == Note.NOTE_ON) {
            System.out.println("received " + notesReceived + " note " + note.getPitch());
            if (notesReceived < notesPerTick) {
                System.out.println("Adding");
                queue.addNote(note);
                notesReceived++;
            } else {
                System.out.println("Removing 0");
                queue.removeNote(0);
                queue.addNote(note);
            }
        }
    }

    @Override
    public String getName() {
        return "LAST HOLD";
    }

}
