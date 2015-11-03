package blocks;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import MIDIBlocks.MidiBlocksClient;
import MIDIBlocks.Note;

public class ChordifyTest {
    
    private MidiBlocksClient client;
    private Note note;
    private ChordifyBlock block;
    
    @Before
    public void setup() {
        client = new MidiBlocksClient();
        client.getMusicManager().loadScales("src/main/java/MIDIBlocks/MIDIBlocks/scales.csv");
        client.getMusicManager().setScale("chromatic", "C");
        note = new Note(Note.NOTE_ON, 48);
        block = new ChordifyBlock(client.getMusicManager());
    }

	@Test
	public void testModify() {
		ArrayList<Note> result = block.modify(note);
		assertEquals(48, result.get(0).getPitch());
		assertEquals(50, result.get(1).getPitch());
		assertEquals(52, result.get(2).getPitch());
	}
	
	@Test
	public void testModifyScale() {
	    client.getMusicManager().setScale("major", "C");
        ArrayList<Note> result = block.modify(note);
        assertEquals(48, result.get(0).getPitch()); // C
        assertEquals(52, result.get(1).getPitch()); // E
        assertEquals(55, result.get(2).getPitch()); // G
	}
	
	@Test
	public void testModifyWrap() {
	    ArrayList<Note> result = block.modify(new Note(Note.NOTE_ON, 46)); // Bb
        assertEquals(46, result.get(0).getPitch()); // Bb
        assertEquals(48, result.get(1).getPitch()); // C
        assertEquals(50, result.get(2).getPitch()); // D
	}
	
	@Test
	public void testNoteOutOfScale() {
	    client.getMusicManager().setScale("major", "C");
	    ArrayList<Note> result = block.modify(new Note(Note.NOTE_ON, 46)); //Bb
	    assertEquals(0, result.size());
	}

}
