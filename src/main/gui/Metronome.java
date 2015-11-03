package gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;
import MIDIBlocks.Tickable;

/**
 * Metronome class, flashes and plays an annoying sound. Mutable
 * 
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class Metronome extends JLabel implements Tickable {
    private int metroOscil = 0;
    private boolean mute = true;
    private MusicManager musicManager;

    private static int PRIME_PITCH = 60;
    private static int SECOND_PITCH = 55;

    /**
     * Create a new metronome
     * 
     * @param musicManager
     */
    public Metronome(MusicManager musicManager) {
        this.musicManager = musicManager;
        musicManager.getClient().addTickable(this);

        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setBackground(Color.RED);
        this.setOpaque(true);
        this.setText("Metronome");
    }

    /**
     * Mute the metronome
     */
    public void mute() {
        mute = !mute;
        if (mute) {
            int pitch = metroOscil % 4 == 0 ? PRIME_PITCH : SECOND_PITCH;
            musicManager.quickNote(new Note(Note.NOTE_OFF, pitch,
                            Note.DEFAULT_VELOCITY));
        }
    }

    /**
     * Play a sound and change the colour of the metronome
     */
    public void tick() {
        if (metroOscil % 4 == 0) {
            /* Play one sound */
            if (!mute) {
                musicManager.quickNote(new Note(Note.NOTE_ON, PRIME_PITCH,
                                Note.DEFAULT_VELOCITY));
            }
            Metronome.this.setBackground(new Color(40, 100, 255));
            metroOscil++;
            return;
        } else if (metroOscil % 4 == 1) {
            Metronome.this.setBackground(new Color(255, 40, 20));
        } else if (metroOscil % 4 == 2) {
            Metronome.this.setBackground(new Color(255, 40, 100));
        } else {
            Metronome.this.setBackground(new Color(255, 40, 150));
        }
        /* Play different sound */
        if (!mute) {
            musicManager.quickNote(new Note(Note.NOTE_ON, SECOND_PITCH,
                            Note.DEFAULT_VELOCITY));
        }
        metroOscil = (metroOscil + 1) % 4;
    }
}
