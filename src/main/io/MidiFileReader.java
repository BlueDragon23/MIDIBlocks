package io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

import MIDIBlocks.Note;

public class MidiFileReader {

    private static MidiFileReader instance;

    private Sequence sequence;

    private MidiFileReader() {

    }

    public static MidiFileReader getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new MidiFileReader();
            return instance;
        }
    }

    /**
     * Read the file contents into the sequence for this reader
     * 
     * @param filename
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    private void openFile(String filename) throws InvalidMidiDataException,
                    IOException {
        sequence = MidiSystem.getSequence(new File(filename));
    }

    /**
     * Read a file and convert it to an ArrayList of notes
     * 
     * @param filename
     *            A filename for a valid MIDI file
     * @return Every valid note in the input MIDI file
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    public ArrayList<Note> readFile(String filename)
                    throws InvalidMidiDataException, IOException {
        openFile(filename);
        ArrayList<Note> notes = new ArrayList<>();
        MidiEvent event;
        Note note;
        long ticksPerSecond = (long) (sequence.getTickLength() * (Math.pow(10, 6))
                        / sequence.getMicrosecondLength());
        Sequencer seq;
        try {
            seq = MidiSystem.getSequencer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            return new ArrayList<Note>();
        }
        int bpm = (int) seq.getTempoInBPM();
        long ticksPerBeat = ticksPerSecond * 60 / bpm;
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                event = track.get(i);
                note = new Note(event);
                if (note.getPitch() > Note.FIRST_NOTE
                                && note.getPitch() < Note.LAST_NOTE) {
                    notes.add(note);
                }
                note.setTick(note.getTick() * 60000 / (ticksPerBeat * bpm));
                
            }
        }
        return notes;
    }

    /**
     * Try to get the tempo from a loaded sequence. Should only be called after
     * a sequence has been loaded, otherwise behaviour is undefined
     * 
     * @return
     */
    public int getTempo() {
        Sequencer sequencer = null;
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.setSequence(sequence);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return Math.round(sequencer.getTempoInBPM());
    }
}
