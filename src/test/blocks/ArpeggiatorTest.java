package blocks;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import MIDIBlocks.MidiBlocksClient;

public class ArpeggiatorTest {
    
    private MidiBlocksClient client;
    private ArpeggiatorBlock block;
    
    @Before
    public void setup() {
        client = new MidiBlocksClient();
        block = new ArpeggiatorBlock(client.getMusicManager());
    }

    @Test
    public void testSetParams() {
        block.setParams(new String[]{"DESCENDING"});
        assertEquals("DESCENDING", block.getParams()[0]);
    }

    @Test
    public void testGetParams() {
        assertEquals("ASCENDING", block.getParams()[0]);
    }

    @Test
    public void testGetParamNames() {
        assertEquals("Pattern", block.getParamNames()[0]);
    }

}
