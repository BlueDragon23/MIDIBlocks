package blocks;

import java.util.ArrayList;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;
import blocks.arpeggiatorStrategy.AbstractArpeggiatorStrategy;
import blocks.arpeggiatorStrategy.ArpeggiatorAscending;
import blocks.arpeggiatorStrategy.ArpeggiatorDescending;
import blocks.arpeggiatorStrategy.ArpeggiatorPingPong;
import blocks.arpeggiatorStrategy.ArpeggiatorRandom;

/**
 * This block outputs a sequence of notes based on the notes that are currently
 * on. There are four modes for this block, ASCENDING, DESCENDING, PING_PONG and
 * RANDOM. These are implemented by the {@link ArpeggiatorAscending},
 * {@link ArpeggiatorDescending}, {@link ArpeggiatorPingPong} and
 * {@link ArpeggiatorRandom} strategies respectively.
 * 
 * In general, this block works by creating a list of currently on notes, then
 * the strategy determines how the next note from the list is selected. This
 * next note is then output at the start of the next beat of the music
 * 
 * @author Aidan
 *
 */
public class ArpeggiatorBlock extends AbstractBlock {

    private AbstractArpeggiatorStrategy strategy;

    public ArpeggiatorBlock(MusicManager musicManager) {
        super(musicManager);
        this.needsTimer = true;
        /* Default pattern */
        strategy = new ArpeggiatorAscending();
    }

    @Override
    public ArrayList<Note> modify(Note input) {
        if (input.getStatus() == Note.NOTE_ON) {
            history.addNote(input);
            history.sort();
            strategy.update(history);
            return new ArrayList<Note>();
        } else {
            input.setStatus(Note.NOTE_ON);
            history.removeNote(input);
            strategy.update(history);
            ArrayList<Note> retNotes = new ArrayList<>();
            input.setStatus(Note.NOTE_OFF);
            retNotes.add(input);
            return retNotes;
        }
    }

    @Override
    public void setParams(String[] params) {
        if (!(params.length == 1)) {
            return;
        }
        switch (params[0]) {
        case "ASCENDING":
            strategy = new ArpeggiatorAscending();
            break;
        case "DESCENDING":
            strategy = new ArpeggiatorDescending();
            break;
        case "PING PONG":
            strategy = new ArpeggiatorPingPong();
            break;
        case "RANDOM":
            strategy = new ArpeggiatorRandom();
            break;
        default:
            break;
        }
    }

    @Override
    public String[] getParams() {
        return new String[] { strategy.getName() };
    }

    @Override
    public String[] getParamNames() {
        return new String[] { "Pattern" };
    }

    /**
     * Get the possible values for the pattern
     */
    public String[] getParamValues() {
        return new String[] { "ASCENDING", "DESCENDING", "PING PONG", "RANDOM" };
    }

    public AbstractArpeggiatorStrategy getStrategy() {
        return strategy;
    }

    @Override
    public String getName() {
        return "arpeggiator";
    }

}
