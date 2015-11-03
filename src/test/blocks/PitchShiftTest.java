package blocks;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import MIDIBlocks.MidiBlocksClient;
import MIDIBlocks.Note;

public class PitchShiftTest {

    private MidiBlocksClient client;
    private Note note;
    private PitchShiftBlock block;

    @Before
    public void setup() {
        client = new MidiBlocksClient();
        client.getMusicManager().loadScales("src/main/java/MIDIBlocks/MIDIBlocks/scales.csv");
        client.getMusicManager().setScale("chromatic", "C");
        note = new Note(Note.NOTE_ON, 48, 100);
        block = new PitchShiftBlock(client.getMusicManager());
    }
    
    @Test
    public void testConstructor2() {
        PitchShiftBlock block2 = new PitchShiftBlock(client.getMusicManager(), 3);
        assertEquals("3", block2.getParams()[0]);
    }

    @Test
    public void testSetParams() {
        block.setParams(new String[] { "2" });
        assertEquals("2", block.getParams()[0]);
    }

    @Test
    public void testGetParams() {
        assertEquals("0", block.getParams()[0]);
    }

    @Test
    public void testGetParamNames() {
        assertEquals("Shift", block.getParamNames()[0]);
    }

    @Test
    public void testModify() {
        ArrayList<Note> result = block.modify(note);
        assertEquals(note.getPitch(), result.get(0).getPitch());
    }

    @Test
    public void testModify2() {
        String[] params = { "2" };
        block.setParams(params);
        ArrayList<Note> result = block.modify(note);
        assertEquals(50, result.get(0).getPitch());
    }

    @Test
    public void testModifyNeg() {
        String[] params = { "-2" };
        block.setParams(params);
        ArrayList<Note> result = block.modify(note);
        assertEquals(46, result.get(0).getPitch());
    }

    @Test
    public void testModifyScale() {
        client.getMusicManager().setScale("major", "C");
        block.setParams(new String[] { "1" });
        ArrayList<Note> result = block.modify(note);
        assertEquals(50, result.get(0).getPitch());
    }

    @Test
    public void testModifyScaleNoteOutOfScale() {
        client.getMusicManager().setScale("major", "C");
        ArrayList<Note> result = block.modify(new Note(Note.NOTE_ON, 49)); // C#
        assertEquals(50, result.get(0).getPitch()); // Rounds up to D
    }

    @Test
    public void testModifyScaleNoteOutOfScale2() {
        client.getMusicManager().setScale("major", "C");
        block.setParams(new String[] { "5" });
        ArrayList<Note> result = block.modify(new Note(Note.NOTE_ON, 49)); // C#
        assertEquals(55, result.get(0).getPitch()); // Rounds up to A
    }
    
    @Test
    public void testModifyAboveValid() {
        block.setParams(new String[] { "2" });
        ArrayList<Note> result = block.modify(new Note(Note.NOTE_ON, Note.LAST_NOTE));
        assertEquals(Note.LAST_NOTE, result.get(0).getPitch());
    }
    
    @Test
    public void testModifyBelowValid() {
        block.setParams(new String[] { "-2" });
        ArrayList<Note> result = block.modify(new Note(Note.NOTE_ON, Note.FIRST_NOTE));
        assertEquals(Note.FIRST_NOTE, result.get(0).getPitch());
    }

}
