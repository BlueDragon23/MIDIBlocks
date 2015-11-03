package MIDIBlocks;

import io.InvalidFileException;
import io.MidiFileReader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.ComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.Timer;

public class MidiBlocksClient implements ActionListener {

    private String source;
    private MidiFileReader fileReader;
    private long programStart;
    private JFormattedTextField tempoListener;
    private Timer timer;
    private Timer timer2;
    private int tempo;
    private ArrayList<Note> noteQueue;
    private ArrayList<Tickable> tickables;

    private BlockManager blockManager;
    private MusicManager musicManager;
    private int songPosition;

    /**
     * Create a new client
     */
    public MidiBlocksClient() {
        source = null;
        tickables = new ArrayList<>();
        noteQueue = new ArrayList<>();
        fileReader = MidiFileReader.getInstance();
        programStart = System.currentTimeMillis();
        blockManager = new BlockManager(this);
        musicManager = new MusicManager(this, blockManager);
        blockManager.addOutputBlock();
        tempo = 120;    
        timer = new Timer(10, this);
        timer.setActionCommand("timer");
        timer2 = new Timer(60000 / tempo, this);
        timer2.setActionCommand("timer2");
        timer2.start();
    }

    /**
     * Get the time since the timer was started, which is at most the time since
     * the program start
     * 
     * @return
     */
    public long getTime() {
        return System.currentTimeMillis() - programStart;
    }

    /**
     * Reset the running time of the program
     */
    public void restartTime() {
        this.programStart = System.currentTimeMillis();
    }

    /**
     * Set the input method for the program. Should only be used with FILE
     * 
     * @param mode
     *            One of the possibilities from SourceMode
     * @param filename
     *            The filename of the file to load
     * @throws InvalidFileException
     */
    public void setSource(String mode, String filename)
                    throws InvalidFileException {
        if (mode.equals("FILE")) {
            loadFile(filename);
            source = mode;
            if (musicManager.getKeyboard() != null) {
                musicManager.getKeyboard().setDisabled(true);
            }
        }
    }

    /**
     * Set the input method for the program
     * 
     * @param mode
     *            One from SourceMode
     */
    public void setSource(String mode) {
        switch (mode) {
        case "FILE":
            System.out.println("Require a filename");
            break;
        case "DRIVER":
            source = mode;
            if (musicManager.getKeyboard() != null) {
                musicManager.getKeyboard().setDisabled(false);
            }
            break;
        case "KEYBOARD":
            source = mode;
            if (musicManager.getKeyboard() != null) {
                musicManager.getKeyboard().setDisabled(true);
            }
            break;
        default:
            break;
        }
    }

    public String getSource() {
        if (source.equals("FILE")) {
            return source + " " + blockManager.getOutBlock().getFilename();
        } else {
            return source;
        }
    }

    /**
     * Set the output method
     * 
     * @param mode
     * @param enabled
     * @return
     */
    public boolean setOutput(String mode, boolean enabled) {
        switch (mode) {
        case "FILE":
            blockManager.getOutBlock().setFile(false, "");
            return false;
        case "SYNTH":
            return blockManager.getOutBlock().setSynth(enabled);
        }
        return false;
    }

    public String getOutput() {
        return blockManager.getOutBlock().isFile() ? "FILE"
                        + (blockManager.getOutBlock().isSynth() ? " SYNTH" : "") : blockManager.getOutBlock()
                        .isSynth() ? "SYNTH" : "";
    }

    /**
     * Set the output method. Should only be used with FILE
     * 
     * @param mode
     *            Either "FILE" or "SYNTH"
     * @param filename
     *            File to load
     * @throws InvalidFileException
     */
    public boolean setOutput(String mode, String filename)
                    throws InvalidFileException {
        switch (mode) {
        case "FILE":
            return blockManager.getOutBlock().setFile(true, filename);
        case "SYNTH":
            break;
        }
        return false;
    }

    /**
     * Load a MIDI file as a source
     * 
     * @param filename
     *            the MIDI file to load
     * @throws InvalidFileException
     */
    public void loadFile(String filename) throws InvalidFileException {
        try {
            stop();
            noteQueue = this.fileReader.readFile(filename);
            tempo = fileReader.getTempo();
            /* Update tempo on screen */
            if (tempoListener != null) {
                tempoListener.setValue(tempo);
            }
        } catch (InvalidMidiDataException | IOException e) {
            throw new InvalidFileException();
        }
    }

    /**
     * Add a tempo listener that will be updated when the client tempo updates
     * 
     * @param text
     */
    public void addTempoListener(JFormattedTextField text) {
        tempoListener = text;
    }
    
    /**
     * Add a note to the queue to be played
     * 
     * @param note
     *            the note to be added
     */
    public void addNote(Note note) {
        if (note != null) {
            noteQueue.add(note);
        }
        return;
    }

    /**
     * Get the currently set mode
     * 
     * @return
     */
    public String getMode() {
        return musicManager.getMode();
    }

    /**
     * Get the currently set root note
     * 
     * @return
     */
    public String getRootNote() {
        return musicManager.getRootNote();
    }

    
    
    public void addTickable(Tickable t) {
        tickables.add(t);
    }
    
    /**
     * Save the current note queue to the file @filename
     * 
     * @param filename
     *            the file where the midi should be saved
     */
    public void save(String filename) {
        blockManager.getOutBlock().save();
    }
    
    /**
     * Playback if the source is a file, otherwise start recording
     */
    public void play() {
        if (source.equals("FILE")) {
            /* Commence playback */
            restartTime();
            songPosition = 0;
            timer.start();
        } else if (blockManager.getOutBlock().isFile()) {
            /* Start recording if the output is a file and input is not */
            System.out.println("Detected file output");
            restartTime();
            clearQueue();
        }
    }

    /**
     * Stop playback if the source if a file, stop recording otherwise
     */
    public void stop() {
        if (source.equals("FILE")) {
            timer.stop();
            songPosition = 0;
        }
        if (blockManager.getOutBlock().isFile()) {
            System.out.println("Detected file output");
            blockManager.getOutBlock().save();
        }
    }
    
    /**
     * Get the current tempo for the client
     * 
     * @return
     */
    public int getTempo() {
        return tempo;
    }

    /**
     * Update the tempo of the program
     * 
     * @param tempo
     */
    public void setTempo(int tempo) {
        /* Should change to the Observer pattern */
        this.tempo = tempo;
        timer2.setDelay(60000 / tempo);
    }

    /**
     * Remove all notes from the note queue
     */
    public void clearQueue() {
        noteQueue.clear();
        blockManager.getOutBlock().clearFileQueue();
    }
    
    /**
     * Notify any tickable objects and play notes if a song is playing
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("timer2")) {
            /* Tick every beat */
            for (Tickable t : tickables) {
                t.tick();
            }
        } else if (e.getActionCommand().equals("timer")) {
            /* Going off every 10ms to check if a note should be played */
            if (noteQueue.size() > 0 && songPosition < noteQueue.size()) {
                Note note = noteQueue.get(songPosition);
                if (note.getTick() <= getTime()) {
                    System.out.println(note.getTick());
                    songPosition++;
                    musicManager.playNote(note);
                }
            }
            blockManager.getOutBlock().tryConnection();
        }
    }

    public ComboBoxModel<String> getModeModel() {
        return musicManager.getModeModel();
    }

    public void hardwareConfig() {
        blockManager.hardwareConfig();
    }
    
    public BlockManager getBlockManager() {
        return blockManager;
    }
    
    public MusicManager getMusicManager() {
        return musicManager;
    }

}
