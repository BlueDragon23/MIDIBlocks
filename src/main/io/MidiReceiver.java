package io;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

public class MidiReceiver implements Receiver {

    private MusicManager musicManager;

    public MidiReceiver(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    @Override
    public void close() {
    }

    @Override
    public void send(MidiMessage event, long tick) {
        if (event instanceof ShortMessage) {
            if (musicManager.getClient().getOutput().isEmpty() && !musicManager.isNotified()) {
                JOptionPane.showMessageDialog(null,
                                "Warning: There is no output selected",
                                "No Output", JOptionPane.ERROR_MESSAGE);
                musicManager.setNotified(true);
                return;
            }
            ShortMessage shortM = (ShortMessage) event;
            musicManager.playNote(new Note(shortM.getStatus(), shortM.getData1(),
                            shortM.getData2(), musicManager.getClient().getTime()));
        }
    }

}
