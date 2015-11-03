package blocks.output;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;

import MIDIBlocks.Note;

public class SynthOutputStrategy extends AbstractOutputStrategy {

    private static Receiver synthRcvr;

    public SynthOutputStrategy() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            synthRcvr = synth.getReceiver();
        } catch (MidiUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void output(Note input) {
        /*
         * Send the note directly to the synth. Should be modified to send to
         * stdout
         */
        System.out.println(input.getPitch());
        synthRcvr.send(input.convertToMidi().getMessage(), -1);
        return;
    }

    @Override
    public void setParams(String params) {
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
        return "SYNTH";
    }

}
