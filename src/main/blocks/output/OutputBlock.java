package blocks.output;

import io.MidiOutput;
import io.UsbOutput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import blocks.AbstractBlock;
import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

/**
 * Always stays at the end of the processing line, and controls where the notes
 * go after they have been processed. May contain any combination of FILE, USB
 * and SYNTH
 * 
 * @author Aidan
 *
 */
public class OutputBlock extends AbstractBlock {

    private boolean fileEnabled = false;
    private boolean synthEnabled = false;
    private ArrayList<Note> stored;

    private UsbOutput usb;
    private static Receiver synthRcvr;
    private File file;

    public OutputBlock(MusicManager musicManager) {
        super(musicManager);
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            synthRcvr = synth.getReceiver();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        usb = new UsbOutput(musicManager);
        stored = new ArrayList<>();
    }

    @Override
    public ArrayList<Note> modify(Note input) {
        if (fileEnabled) {
            /* Store note to be saved to file */
            stored.add(input);
        }
        if (synthEnabled) {
            /* Send note over serial */
            usb.playNote(input);
        }
        /* Play note using synth */
        System.out.println(input.getPitch()); // Useful for debugging
        synthRcvr.send(input.convertToMidi().getMessage(), -1);
        return new ArrayList<Note>();
    }

    @Override
    public void setParams(String[] params) {
        return;
    }

    @Override
    public String[] getParams() {
        return new String[] {};
    }

    @Override
    public String[] getParamNames() {
        return new String[] {};
    }

    /**
     * Just pass it an empty string for disabling
     * 
     * @param mode
     * @param filename
     * @return
     */
    public boolean setFile(boolean mode, String filename) {
        fileEnabled = mode;
        if (mode) {
            file = new File(filename);
        }
        return fileEnabled;
    }

    public String getFilename() {
        return file.getAbsolutePath();
    }

    /**
     * Check whether there is currently file output
     * 
     * @return
     */
    public boolean isFile() {
        return fileEnabled;
    }
    
    public boolean isSynth() {
        return usb.isWorking();
    }

    /**
     * Set whether synth output is enabled
     * 
     * @param mode
     * @return
     */
    public boolean setSynth(boolean mode) {
        if (usb.isWorking() && mode) {
            synthEnabled = true;
        } else {
            synthEnabled = false;
        }
        return synthEnabled;
    }

    /**
     * If the synth output is enabled, send it a hardware configuration message
     */
    public void hardwareConfig() {
        if (synthEnabled) {
            usb.hardwareConfig();
        }
    }

    /**
     * Save the current queue to a file
     */
    public void save() {
        try {
            saveFile(file, stored);
            stored.clear();
        } catch (InvalidMidiDataException | MidiUnavailableException
                        | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the current MIDI queue to a file
     * 
     * @param file
     * @throws InvalidMidiDataException
     * @throws MidiUnavailableException
     * @throws IOException
     */
    private void saveFile(File file, ArrayList<Note> queue)
                    throws InvalidMidiDataException, MidiUnavailableException,
                    IOException {
        MidiOutput output = MidiOutput.getInstance();
        /* Create a new sequence */
        Sequence seq = new Sequence(Sequence.PPQ, MidiOutput.ticksPerNote);
        Track track = seq.createTrack();
        MetaMessage meta = new MetaMessage();
        long microsPerBeat = (long) (60 * Math.pow(10, 6) / musicManager.getClient().getTempo());
        byte[] bt = { (byte) ((microsPerBeat & 0xFF0000) / Math.pow(16, 4)), 
                        (byte) ((microsPerBeat & 0xFF00) / Math.pow(16, 2)), 
                        (byte) (microsPerBeat & 0xFF) }; // Tempo event - how many microseconds per quarter note
        meta.setMessage(0x51, bt, 3);
        MidiEvent me = new MidiEvent(meta, (long) 0);
        track.add(me);
        track = output.getSequence(queue, track, musicManager.getClient().getTempo()
                        * MidiOutput.ticksPerNote / 60); // Ticks
        // per
        // second
        // is
        // BPM
        // /
        // 60
        MidiSystem.write(seq, MidiSystem.getMidiFileTypes()[0], file);
    }

    public void clearFileQueue() {
        stored.clear();
    }

    @Override
    public String getName() {
        return "output";
    }

    /**
     * Attempt to connect to a synth block. Polling this is an awful idea, but
     * it'll work
     */
    public void tryConnection() {
        usb.tryConnection();
    }

}
