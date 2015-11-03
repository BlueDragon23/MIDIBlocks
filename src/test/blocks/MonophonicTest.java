package blocks;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import MIDIBlocks.MidiBlocksClient;
import MIDIBlocks.Note;

public class MonophonicTest {
    
    private MidiBlocksClient client;
    private MonophonicBlock block;
    private Note noteOn;
    private Note noteOff;
    
    @Before
    public void setup() {
        client = new MidiBlocksClient();
        client.getMusicManager().loadScales("src/main/java/MIDIBlocks/MIDIBlocks/scales.csv");
        client.getMusicManager().setScale("chromatic", "C");
        block = new MonophonicBlock(client.getMusicManager());
        noteOn = new Note(Note.NOTE_ON, 48);
        noteOff = new Note(Note.NOTE_OFF, 48);
    }

    @Test
    public void testModifyPassThrough() {
        ArrayList<Note> result = block.modify(noteOn);
        assertEquals(48, result.get(0).getPitch());
        assertEquals(Note.NOTE_ON, result.get(0).getStatus());
        result = block.modify(noteOff);
        assertEquals(48, result.get(0).getPitch());
        assertEquals(Note.NOTE_OFF, result.get(0).getStatus());        
    }
    
    @Test
    public void testMultipleOn() {
        ArrayList<Note> result = block.modify(noteOn);
        assertEquals(48, result.get(0).getPitch());
        assertEquals(Note.NOTE_ON, result.get(0).getStatus());
        result = block.modify(new Note(Note.NOTE_ON, 50));
        assertEquals(48, result.get(0).getPitch());
        assertEquals(Note.NOTE_OFF, result.get(0).getStatus());
        assertEquals(50, result.get(1).getPitch());
        assertEquals(Note.NOTE_ON, result.get(1).getStatus());
    }
    
    @Test
    public void testIrrelevantOff() {
        ArrayList<Note> result = block.modify(noteOff);
        assertEquals(0, result.size());
    }

}
