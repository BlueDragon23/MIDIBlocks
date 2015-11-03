package blocks;

import java.util.ArrayList;

import MIDIBlocks.MidiBlocksClient;
import MIDIBlocks.Note;

public class TestOutputBlock extends AbstractBlock {
    
    private ArrayList<Note> notesReceived;

    public TestOutputBlock(MidiBlocksClient client) {
        super(client.getMusicManager());
        notesReceived = new ArrayList<>();
    }
    
    public ArrayList<Note> getNotesPlayed() {
        return notesReceived;
    }
    
    @Override
    public ArrayList<Note> modify(Note input) {
        notesReceived.add(input);
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

    @Override
    public String getName() {
        return "test output";
    }

}
