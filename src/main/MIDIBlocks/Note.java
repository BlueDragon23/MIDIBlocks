package MIDIBlocks;

import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Internal representation of a note
 * 
 * @author Aidan
 *
 */
public class Note implements Comparable<Object> {

    private int status;
    private int pitch;
    private int velocity;
    private long time;

    /* Possible note names, beginning at C */
    static public ArrayList<String> noteNames = new ArrayList<>(
                    Arrays.asList(new String[] { "C", "C#/Db", "D", "D#/Eb",
                            "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B" }));

    /* The lowest MIDI value required */
    static public final int FIRST_NOTE = 24; // C1

    /* The highest MIDI value required */
    static public final int LAST_NOTE = 107; // B7

    /* Convenience variable for MIDI note off */
    static public final int NOTE_OFF = ShortMessage.NOTE_OFF;

    /* Convenience variable for MIDI note on */
    static public final int NOTE_ON = ShortMessage.NOTE_ON;

    /* Default note velocity. Arbitrary */
    static public final int DEFAULT_VELOCITY = 80;

    /**
     * Create a Note representation of a MidiEvent. The tick should be changed
     * to time in ms before creation
     * 
     * @param event
     *            the MidiEvent to be converted to a Note
     */
    public Note(MidiEvent event) {
        MidiMessage msg = event.getMessage();
        time = event.getTick();
        int status = msg.getStatus();
        if (status < 128 || status > 159) {
            /* Not a note on or note off command */
            return;
        }
        ShortMessage shortm = (ShortMessage) msg;
        this.status = shortm.getCommand();
        if (!(this.status == NOTE_ON || this.status == NOTE_OFF)) {
            /* Some other signal */
            this.status = -1;
        }
        this.pitch = shortm.getData1();
        this.velocity = shortm.getData2();
    }

    /**
     * Create a note from the basic parameters, with a default velocity and
     * default timestamp of 0
     * 
     * @param status
     *            either NOTE_ON or NOTE_OFF
     * @param pitch
     *            a pitch between 0 and 127 inclusive
     */
    public Note(int status, int pitch) {
        this(status, pitch, DEFAULT_VELOCITY, 0);
    }

    /**
     * Create a note from the basic parameters, with a default timestamp of 0
     * 
     * @param status
     *            either NOTE_ON or NOTE_OFF
     * @param pitch
     *            a pitch between 0 and 127 inclusive
     * @param velocity
     *            the volume of the note
     */
    public Note(int status, int pitch, int velocity) {
        this(status, pitch, velocity, 0);
    }

    /**
     * Create a note from the basic parameters
     * 
     * @param status
     *            either NOTE_ON or NOTE_OFF
     * @param pitch
     *            a pitch between 0 and 127 inclusive
     * @param velocity
     *            the volume of the note
     * @param time
     *            the time in ms since the start of the song
     */
    public Note(int status, int pitch, int velocity, long time) {
        this.status = status;
        this.pitch = pitch;
        this.velocity = velocity;
        this.time = time;
    }

    /**
     * Return the pitch of this note
     * 
     * @return an integer between 0 and 127 inclusive
     */
    public int getPitch() {
        return pitch;
    }

    /**
     * Set the pitch of this note, being in the range 0-127, corresponding to
     * the MIDI classification
     * 
     * @param pitch
     *            a MIDI pitch
     */
    public void setPitch(int pitch) {
        if (0 <= pitch && pitch <= 127) {
            this.pitch = pitch;
        }
    }

    /**
     * Get the status of this note, Note.NOTE_ON representing note on,
     * Note.NOTE_OFF representing note off
     * 
     * @return Note.NOTE_ON or Note.NOTE_OFF
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * Set the status of the note to be status. Must be either Note.NOTE_ON or
     * Note.NOTE_OFF
     */
    public void setStatus(int status) {
        if (status == NOTE_ON || status == NOTE_OFF) {
            this.status = status;
        }
    }

    public int getVelocity() {
        return this.velocity;
    }

    /**
     * Retrieve the timestamp for this note
     * 
     * @return timestamp
     */
    public long getTick() {
        return this.time;
    }

    /**
     * Set the timestamp to tick
     * 
     * @param tick
     */
    public void setTick(long tick) {
        this.time = tick;
    }

    /**
     * Convert a Note to an equivalent MidiEvent
     * 
     * @return the MIDI representation of this note
     */
    public MidiEvent convertToMidi() {
        ShortMessage msg = new ShortMessage();
        try {
            msg.setMessage(status, pitch, velocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return new MidiEvent(msg, time);
    }

    /**
     * Returns the name of the /num note, where the list runs from C to B
     * 
     * @param num
     *            the number of the note name desired
     * @return the string representation of a note
     */
    static public String getNoteName(int num) {
        return noteNames.get(num % 12);
    }

    /**
     * Get the position of the note relative to the C chromatic scale
     * 
     * @param note
     * @return
     */
    static public int getNoteNum(String note) {
        return noteNames.indexOf(note);
    }

    @Override
    public int compareTo(Object arg0) {
        Note note0 = (Note) arg0;
        return this.pitch - note0.getPitch();
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof Note)) {
            return false;
        }
        Note note2 = (Note) arg0;
        if (note2.getStatus() == status && note2.getPitch() == pitch) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return status + pitch * 17 + velocity * 23;
    }

}
