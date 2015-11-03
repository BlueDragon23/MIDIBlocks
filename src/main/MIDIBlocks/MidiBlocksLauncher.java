package MIDIBlocks;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;

import gui.MidiBlocksController;
import gui.MidiBlocksView;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class MidiBlocksLauncher {

    public static void main(String[] args) {
        MidiBlocksLauncher launch = new MidiBlocksLauncher();
        launch.view.getFrame().setVisible(true);
    }

    private MidiBlocksClient client;
    private MidiBlocksView view;
    private MidiBlocksController controller;

    /**
     * Launch the program
     */
    public MidiBlocksLauncher() {
        this.client = new MidiBlocksClient();
        client.setSource("KEYBOARD");
        try {
            InputStream scales = MidiBlocksLauncher.class.getClassLoader().getResourceAsStream("scales.csv");
            File tempScales = File.createTempFile("scales",
                            Long.toString(System.currentTimeMillis()));
            FileUtils.copyInputStreamToFile(scales, tempScales);
            client.getMusicManager().loadScales(tempScales.getAbsolutePath());
            tempScales.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.getMusicManager().setScale("chromatic", "C");
        try {
            MidiSystem.getSynthesizer().open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        this.view = new MidiBlocksView(client);
        this.controller = new MidiBlocksController(client);
        view.initialize(controller);
    }

}
