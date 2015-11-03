package io;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import MIDIBlocks.MidiBlocksClient;
import MIDIBlocks.Note;

public class TestUsbOutput {

    @Test
    public void testGetBytes() {
        ArrayList<Note> queue = new ArrayList<>();
        queue.add(new Note(Note.NOTE_ON, 48, Note.DEFAULT_VELOCITY));
        queue.add(new Note(Note.NOTE_OFF, 48, Note.DEFAULT_VELOCITY));
        MidiBlocksClient client = new MidiBlocksClient();
        UsbOutput usb = new UsbOutput(client.getMusicManager());
        ArrayList<Byte> bytes = usb.getBytes(queue);
        assertEquals(new Byte((byte) (128 + 48)), bytes.get(0));
        assertEquals(new Byte((byte) 48), bytes.get(1));
    }

}
