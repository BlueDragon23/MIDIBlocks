package blocks.output;

import io.UsbOutput;
import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

public class UsbOutputStrategy extends AbstractOutputStrategy {

    private UsbOutput out;

    /**
     * Create a new UsbOutputStrategy
     * 
     * @param musicManager
     */
    public UsbOutputStrategy(MusicManager musicManager) {
        super();
        out = new UsbOutput(musicManager);
    }

    /**
     * Determine whether the USB connection is active
     * 
     * @return true if there is a serial device connected
     */
    public boolean isWorking() {
        return out.isWorking();
    }

    @Override
    public void output(Note input) {
        out.playNote(input);
        return;
    }

    @Override
    public void setParams(String param) {
        return;
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public String getParamNames() {
        return "";
    }

    @Override
    public String getName() {
        return "USB";
    }

    /**
     * Send a hardware configuration message over serial
     */
    public void hardwareConfig() {
        out.hardwareConfig();
    }

}
