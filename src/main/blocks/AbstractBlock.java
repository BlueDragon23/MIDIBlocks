package blocks;

import java.util.ArrayList;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;
import MIDIBlocks.NoteHistory;
import MIDIBlocks.Tickable;

/**
 * Fucking awesome abstraction shit
 * 
 * @author Aidan
 *
 */
public abstract class AbstractBlock implements Tickable {

    protected AbstractBlock nextBlock;
    private ArrayList<Note> queue;
    protected NoteHistory history;
    protected MusicManager musicManager;
    protected boolean needsTimer;

    /** An abstract class to define the general function of blocks */
    protected AbstractBlock(MusicManager musicManager) {
        musicManager.getClient().addTickable(this);
        this.history = new NoteHistory();
        this.musicManager = musicManager;
        this.needsTimer = false;
        this.queue = new ArrayList<>();
    }

    /**
     * Process a given input and return the modified output from the block
     * 
     * @param input
     *            the Note to be processed
     * @return the modified MidiEvent (return
     */
    public void process(Note input) {
        ArrayList<Note> retNotes = modify(input);
        for (Note note : retNotes) {
            if (needsTimer) {
                System.out.println("added");
                queue.add(note);
            } else {
                nextBlock.process(note);
            }
        }
    }

    /**
     * Set the block to succeed this block, i.e. the one that will receive the
     * notes processed by this one
     * 
     * @param block
     */
    public void setSuccessor(AbstractBlock block) {
        this.nextBlock = block;
    }

    /**
     * Modify the input in some way. Default is to just return the input note
     * 
     * @param input
     *            the MidiEvent to be modified
     * @return the modified event
     */
    public ArrayList<Note> modify(Note input) {
        ArrayList<Note> retNotes = new ArrayList<>();
        retNotes.add(input);
        return retNotes;
    }

    /**
     * Set the parameters for a block
     * 
     * @param params
     */
    abstract public void setParams(String[] params);

    /**
     * Get the parameters for a block
     * 
     * @return
     */
    abstract public String[] getParams();

    /**
     * Get the names of parameters for a block
     * 
     * @return
     */
    abstract public String[] getParamNames();

    /**
     * Timer method that triggers note events for processing on blocks that
     * require it
     */
    public void tick() {
        if (needsTimer && queue.size() > 0) {
            Note note = queue.get(0);
            queue.remove(0);
            nextBlock.process(note);
        }
        if (this instanceof ArpeggiatorBlock) {
            ArpeggiatorBlock block = (ArpeggiatorBlock) this;
            /* Turn off the previous note */
            Note oldNote = block.getStrategy().getCurrentNote();
            if (oldNote != null) {
                Note offNote = new Note(Note.NOTE_OFF, oldNote.getPitch(), oldNote.getVelocity(), musicManager.getClient().getTime());
                nextBlock.process(offNote);
                block.getStrategy().removeCurrentNote();
            }
            /* Send out the next Note */
            Note note = block.getStrategy().getNextNote();
            if (note != null) {
                note.setTick(musicManager.getClient().getTime());
                nextBlock.process(note);
            }
        }
    }

    /**
     * Supermethod for use in {@link GateBlock} and {@link ArpeggiatorBlock}
     * 
     * @return
     */
    public String[] getParamValues() {
        return new String[] {};
    }

    public abstract String getName();

    /**
     * Create an AbstractBlock based on the name of the block
     * @param musicManager
     * @param name
     * @return
     */
    public static AbstractBlock makeBlock(MusicManager musicManager, String name) {
        return makeBlock(musicManager, getIndex(name));
    }

    /**
     * Create an AbstractBlock based on index into an alphabetical list of blocks
     * @param musicManager
     * @param index
     * @return
     */
    public static AbstractBlock makeBlock(MusicManager musicManager, int index) {
        switch (index) {
        case 0:
            return new ArpeggiatorBlock(musicManager);
        case 1:
            return new ChordifyBlock(musicManager);
        case 2:
            return new GateBlock(musicManager);
        case 3:
            return new MonophonicBlock(musicManager);
        case 4:
            return new PitchShiftBlock(musicManager);
        default:
            return null;
        }
    }

    /**
     * Find the index of the block alphabetically
     * @param name
     * @return
     */
    public static int getIndex(String name) {
        switch (name) {
        case "arpeggiator":
            return 0;
        case "chordify":
            return 1;
        case "gate":
            return 2;
        case "monophonic":
            return 3;
        case "pitchShift":
            return 4;
        default:
            return -1;
        }
    }

}
