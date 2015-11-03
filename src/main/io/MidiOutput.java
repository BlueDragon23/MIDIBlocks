package io;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;

import MIDIBlocks.Note;

public class MidiOutput {

    private static MidiOutput midiOutput;
    
    public static int ticksPerNote = 4;

    private MidiOutput() {

    }

    /**
     * Get an instance of this class
     * 
     * @return the MidiOutput instance
     */
    public static MidiOutput getInstance() {
        if (midiOutput != null) {
            return midiOutput;
        } else {
            midiOutput = new MidiOutput();
            return midiOutput;
        }
    }

    /**
     * Create a sequence based on the current note queue
     * 
     * @param ticksPerSecond
     * 
     * @return a MIDI sequence
     * @throws InvalidMidiDataException
     */
    public Track getSequence(ArrayList<Note> queue, Track track,
                    long ticksPerSecond) throws InvalidMidiDataException {

        MidiEvent event;
        for (Note note : queue) {
            System.out.println(note.getPitch());
            /* Convert Note objects to MidiEvents and add them to the track */
            event = note.convertToMidi();
            event.setTick((long) (note.getTick() * ticksPerSecond / 1000.0));
            System.out.println("Time is " + note.getTick() + " tick is "
                            + event.getTick());
            track.add(event);
        }
        return track;
    }

}
