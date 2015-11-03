package blocks;

import blocks.gateStrategy.AbstractGateStrategy;
import blocks.gateStrategy.GateFirstHoldStrategy;
import blocks.gateStrategy.GateLastHoldStrategy;
import blocks.gateStrategy.GateQueueStrategy;

import java.util.ArrayList;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;
import MIDIBlocks.NoteHistory;

/**
 * The gate block acts as a timekeeper for the system, by only outputting a set
 * number of notes each beat. There are three modes for this block, QUEUE,
 * FIRST_HOLD and LAST_HOLD. Queue behaves as expected, putting all notes played
 * in a queue, and then letting them out at a pace corresponding to the value of
 * the other parameter, notes per tick
 * 
 * @author Aidan
 *
 */
public class GateBlock extends AbstractBlock {

    private AbstractGateStrategy strategy;
    private float notesPerTick = (float) 1.0;
    private NoteHistory history;
    private ArrayList<Note> offNotes;
    private int timeToNote = 0;
    private int countdown = 0;

    public GateBlock(MusicManager musicManager) {
        super(musicManager);
        strategy = new GateQueueStrategy(notesPerTick);
        history = new NoteHistory();
        offNotes = new ArrayList<>();
    }

    @Override
    public ArrayList<Note> modify(Note note) {
        if (note.getStatus() == Note.NOTE_ON) {
            strategy.update(note);
        } else if (note.getStatus() == Note.NOTE_OFF) {
            Note note2 = new Note(Note.NOTE_ON, note.getPitch());
            if (strategy.contains(note)) {
                strategy.update(note);
            } else if (history.contains(note2)) {
                offNotes.add(note);
            }
        }
        return new ArrayList<Note>();
    }

    /**
     * Change the parameters of the block. Expect params[0] to be a float,
     * params[1] to be one of QUEUE, FIRST_HOLD, LAST_HOLD
     */
    @Override
    public void setParams(String[] params) {
        try {
            float newNotesPerTick = Float.parseFloat(params[0]);
            notesPerTick = newNotesPerTick;
            if (notesPerTick < 1) {
                timeToNote = (int) (1 / notesPerTick);
                countdown = timeToNote;
            }
        } catch (NumberFormatException e) {
            /* Not a valid float */
            return;
        }
        switch (params[1]) {
        case "QUEUE":
            strategy = new GateQueueStrategy(notesPerTick);
            break;
        case "FIRST HOLD":
            strategy = new GateFirstHoldStrategy(notesPerTick);
            break;
        case "LAST HOLD":
            strategy = new GateLastHoldStrategy(notesPerTick);
            break;
        default:
            /* Not a valid parameter */
            return;
        }
    }

    @Override
    public String[] getParams() {
        return new String[] { String.valueOf(notesPerTick), strategy.getName() };
    }

    @Override
    public String[] getParamNames() {
        return new String[] { "Notes per tick", "Mode" };
    }

    /**
     * Get the possible values for the mode parameter
     */
    public String[] getParamValues() {
        return new String[] { "QUEUE", "FIRST HOLD", "LAST HOLD" };
    }

    public String getName() {
        return "gate";
    }

    @Override
    public void tick() {
        if (notesPerTick >= 1) {
            /* Easy, just release that amount of notes */
            for (int i = 0; i < (int) notesPerTick; i++) {
                if (strategy.isNext()) {
                    /* Get the next note to be played */
                    Note note = strategy.getNext();
                    if (note != null) {
                        /* Add it to the history of played notes */
                        if (note.getStatus() == Note.NOTE_ON) {
                            history.addNote(note);
                        } else {
                            history.removeNote(note);
                        }
                        /* Send it to the next block */
                        nextBlock.process(note);
                    }
                }
            }
            strategy.resetReceived();
        } else {
            /* Keep track of how long we've been waiting to play a note */
            countdown--;
            if (countdown == 0) {
                if (strategy.isNext()) {
                    /* Get the next note to be played */
                    Note note = strategy.getNext();
                    if (note != null) {
                        /* Add it to the history of played notes */
                        if (note.getStatus() == Note.NOTE_ON) {
                            history.addNote(note);
                        } else {
                            history.removeNote(note);
                        }
                        /* Send it to the next block */
                        note.setTick(musicManager.getClient().getTime());
                        nextBlock.process(note);
                    }
                }
                countdown = timeToNote;
                strategy.resetReceived();
            }
        }

        for (Note note : offNotes) {
            note.setTick(musicManager.getClient().getTime());
            nextBlock.process(note);
        }
        offNotes.clear();
    }

}
