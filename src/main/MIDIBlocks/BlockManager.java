package MIDIBlocks;

import java.util.ArrayList;

import blocks.AbstractBlock;
import blocks.output.OutputBlock;

public class BlockManager {
    
    private ArrayList<AbstractBlock> blocks;
    private OutputBlock outBlock;
    private MidiBlocksClient client;
    
    public BlockManager(MidiBlocksClient client) {
        blocks = new ArrayList<>();
        this.client = client;
    }
    
    public void addOutputBlock() {
        outBlock = new OutputBlock(client.getMusicManager());
        blocks.add(outBlock);
    }
    
    /**
     * Add a block to the end of the processing line
     * 
     * @param block
     *            the block to be added
     */
    public void addBlock(AbstractBlock block) {
        addBlock(block, blocks.size() - 1); // Always before output block
    }

    /**
     * Add a block to the processing line at the given position
     * 
     * @param block
     *            the block to be added
     * @param index
     *            the index where the block will be placed
     */
    public void addBlock(AbstractBlock block, int index) {
        blocks.add(index, block);
        if (blocks.size() - 1 > index && index > 0) {
            /* If there is an element before this one */
            blocks.get(index - 1).setSuccessor(block);
        }
        if (index < blocks.size() - 1) {
            /* If there is an element after this one */
            block.setSuccessor(blocks.get(index + 1));
        }
    }

    /**
     * Remove the block at the given position from the processing line
     * 
     * @param index
     *            the index of the block
     */
    public void removeBlock(int index) {
        if (index < 0 || index > blocks.size() - 1) {
            return;
        }
        if (index > 0 && blocks.size() > 2 && index < blocks.size() - 1) {
            blocks.get(index - 1).setSuccessor(blocks.get(index + 1));
        }
        blocks.remove(index);
    }

    public void removeAll() {
        blocks.clear();
        blocks.add(outBlock);
    }

    /**
     * Set the block at index to be @block
     * 
     * @param index
     *            the position to place the block at
     * @param block
     *            the block to be set
     */
    public void setBlock(int index, AbstractBlock block) {
        System.out.println(index);
        if (index < 0 || index > blocks.size()) {
            return;
        }
        blocks.set(index, block);
        if (index > 0) {
            blocks.get(index - 1).setSuccessor(block);
        }
        if (index < blocks.size() - 1) {
            block.setSuccessor(blocks.get(index + 1));
        }
    }
    
    public AbstractBlock get(int index) {
        return blocks.get(index);
    }
    
    public int size() {
        return blocks.size();
    }

    /**
     * Send a hardware configuration message to the serial output
     */
    public void hardwareConfig() {
        outBlock.hardwareConfig();
    }
    
    public OutputBlock getOutBlock() {
        return outBlock;
    }
    
    public MidiBlocksClient getClient() {
        return client;
    }

    
}
