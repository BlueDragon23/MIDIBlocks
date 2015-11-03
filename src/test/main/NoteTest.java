package main;

import static org.junit.Assert.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

import org.junit.Before;
import org.junit.Test;

import MIDIBlocks.Note;

public class NoteTest {

    private Note note;

    @Before
    public void setup() {
        note = new Note(Note.NOTE_ON, 48, Note.DEFAULT_VELOCITY);
    }

    @Test
    public void testNote() throws InvalidMidiDataException {
        ShortMessage msg = new ShortMessage();
        msg.setMessage(ShortMessage.NOTE_ON, 48, 100);
        MidiEvent midi = new MidiEvent(msg, 0);
        Note testNote = new Note(midi);
        assertEquals(48, testNote.getPitch());
    }

    @Test
    public void testConvertToMidi() {
        MidiEvent event = note.convertToMidi();
        ShortMessage msg = (ShortMessage) event.getMessage();
        assertEquals(Note.NOTE_ON, msg.getStatus());
        assertEquals(48, msg.getData1());
        assertEquals(Note.DEFAULT_VELOCITY, msg.getData2());
    }

}
