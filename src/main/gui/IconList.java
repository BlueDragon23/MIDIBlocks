package gui;

import MIDIBlocks.BlockManager;
import MIDIBlocks.MidiBlocksClient;
import blocks.BlockHistory;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class IconList extends JPanel {

    private ArrayList<BlockIcon> blocks;
    private BlockHistory history;
    private BlockManager blockManager;
    private JPanel blockIconPanel;

    private GridBagConstraints con;

    /**
     * Create a new IconList
     * 
     * @param blockManager
     */
    public IconList(BlockManager blockManager) {
        blocks = new ArrayList<>();
        history = new BlockHistory(this);
        this.setLayout(new GridBagLayout());
        this.setName("Icon List");
        addUndo();
        GridBagConstraints arrowCons = new GridBagConstraints();
        arrowCons.gridx = 0;
        arrowCons.gridy = 1;
        arrowCons.weightx = 0.1;
        arrowCons.fill = GridBagConstraints.BOTH;
        arrowCons.anchor = GridBagConstraints.WEST;
        this.add(new ArrowPanel(), arrowCons);
        arrowCons.gridx = 2;
        arrowCons.anchor = GridBagConstraints.EAST;
        this.add(new ArrowPanel(), arrowCons);
        blockIconPanel = new BlockIconPanel(blocks);
        blockIconPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        blockIconPanel.setLayout(new GridBagLayout());
        blockIconPanel.setName("BlockIconPanel");
        blockIconPanel.setTransferHandler(new BlockTransferHandler());
        blockIconPanel.setMinimumSize(new Dimension(1000, 500));
        JScrollPane scrollPane = new JScrollPane(blockIconPanel,
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 1;
        cons.gridy = 1;
        cons.fill = GridBagConstraints.BOTH;
        cons.weightx = 1;
        cons.weighty = 1;
        cons.anchor = GridBagConstraints.CENTER;
        // this.add(blockIconPanel, cons);
        this.add(scrollPane, cons);

        this.blockManager = blockManager;
        con = new GridBagConstraints();
        con.fill = GridBagConstraints.BOTH;
        con.gridy = 0;
        con.weightx = 1;
        con.weighty = 1;
        con.ipadx = 20;
        con.ipady = 20;
        con.insets = new Insets(10, 10, 10, 10);
    }

    /**
     * Add the undo button so you can undo things
     */
    private void addUndo() {
        JButton undo = new JButton("Undo");
        undo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
        GridBagConstraints cons = new GridBagConstraints();
        cons.anchor = GridBagConstraints.NORTH;
        cons.gridx = 1;
        cons.gridy = 0;
        this.add(undo, cons);
    }

    /**
     * Undo the last action
     */
    public void undo() {
        history.undo();
    }

    /*
     * A general note for these methods. isUndo is a property that says whether
     * a function is undoing an operation. If isUndo is true, the operation will
     * not be added into the history of changes
     */

    /**
     * Add a block to the list at index. If isUndo, the operation will not be
     * added to the history
     * 
     * @param index
     * 
     * @param block
     * 
     * @param isUndo
     */
    public void add(int index, BlockIcon block, boolean isUndo) {
        blocks.add(index, block);
        block.setList(this);
        con.gridx = index;
        blockIconPanel.add(block, con);
        blockManager.addBlock(block.getBlock(), index);
        if (!isUndo) {
            history.addAction("ADD " + index);
        }
        BlockIcon blockIcon;
        for (int i = 0; i < blocks.size(); i++) {
            blockIcon = blocks.get(i);
            blockIconPanel.remove(blockIcon);
            con.gridx = i;
            blockIconPanel.add(blockIcon, con);
        }
    }

    /**
     * In general, move functions return boolean for legacy reasons.
     * 
     * @param index
     * @return
     */
    public boolean move(int indexFrom, int indexTo, boolean isUndo) {
        if (indexFrom < 0 || indexFrom > blocks.size() - 1 || indexTo < 0
                        || indexTo > blocks.size() - 1) {
            return false;
        }

        BlockIcon block = blocks.get(indexFrom);
        blocks.remove(indexFrom);
        blockIconPanel.remove(block);
        blockManager.removeBlock(indexFrom);

        BlockIcon temp;
        if (indexFrom < indexTo) {
            for (int i = indexFrom; i < indexTo; i++) {
                /* Move to the left */
                temp = blocks.get(i);
                blockIconPanel.remove(temp);
                con.gridx = i;
                blockIconPanel.add(temp, con);
            }
        } else {
            for (int i = indexTo; i < indexFrom; i++) {
                /* Move to the right */
                temp = blocks.get(i);
                blockIconPanel.remove(temp);
                con.gridx = i + 1;
                blockIconPanel.add(temp, con);
            }
        }

        /* Add block to indexTo */
        blocks.add(indexTo, block);
        con.gridx = indexTo;
        blockIconPanel.add(block, con);
        blockManager.addBlock(block.getBlock(), indexTo);
        blockIconPanel.revalidate();
        blockIconPanel.repaint();
        /* Add to history */
        if (!isUndo) {
            history.addAction("MOVE " + indexTo + " " + indexFrom);
        }
        return true;
    }

    /**
     * Convience method to move based on a block instead of index
     * 
     * @param block
     * @param indexTo
     * @param isUndo
     * @return
     */
    public boolean move(BlockIcon block, int indexTo, boolean isUndo) {
        return move(blocks.indexOf(block), indexTo, isUndo);
    }

    /**
     * Move a block to the right
     * @param index
     * @param isUndo
     * @return
     */
    public boolean moveRight(int index, boolean isUndo) {
        if (index > blocks.size() - 2) {
            return false;
        }

        BlockIcon tmp = blocks.get(index);
        blockIconPanel.remove(tmp);
        con.gridx = index + 1;
        blockIconPanel.add(tmp, con);
        blocks.set(index, blocks.get(index + 1));
        blocks.set(index + 1, tmp);
        tmp = blocks.get(index);
        blockIconPanel.remove(tmp);
        con.gridx = index;
        blockIconPanel.add(tmp, con);
        blockManager.setBlock(index, blocks.get(index).getBlock());
        blockManager.setBlock(index + 1, blocks.get(index + 1).getBlock());

        blockIconPanel.revalidate();
        blockIconPanel.repaint();
        if (!isUndo) {
            history.addAction("RIGHT " + (index + 1));
        }
        return true;
    }

    /**
     * Move a block to the right
     * @param block
     * @param isUndo
     * @return
     */
    public boolean moveRight(BlockIcon block, boolean isUndo) {
        int index = blocks.indexOf(block);
        return moveRight(index, isUndo);
    }

    /**
     * Move a block to the left
     * @param index
     * @param isUndo
     * @return
     */
    public boolean moveLeft(int index, boolean isUndo) {
        if (index < 1) {
            return false;
        }

        BlockIcon tmp = blocks.get(index);
        blockIconPanel.remove(tmp);
        con.gridx = index - 1;
        blockIconPanel.add(tmp, con);
        blocks.set(index, blocks.get(index - 1));
        blocks.set(index - 1, tmp);
        tmp = blocks.get(index);
        blockIconPanel.remove(tmp);
        con.gridx = index;
        blockIconPanel.add(tmp, con);

        blockManager.setBlock(index, blocks.get(index).getBlock());
        blockManager.setBlock(index - 1, blocks.get(index - 1).getBlock());
        blockIconPanel.revalidate();
        blockIconPanel.repaint();

        if (!isUndo) {
            history.addAction("LEFT " + (index - 1));
        }
        return true;
    }

    /**
     * Move a block to the left
     * @param block
     * @param isUndo
     * @return
     */
    public boolean moveLeft(BlockIcon block, boolean isUndo) {
        return moveLeft(blocks.indexOf(block), isUndo);
    }

    /**
     * Get a block at a given index
     * 
     * @param index
     *            The position of the block
     * @return A BlockIcon at an index
     */
    public BlockIcon getBlock(int index) {
        return blocks.get(index);
    }

    /**
     * Get the index of a given block
     * 
     * @param block
     *            The BlockIcon at an index
     * @return the index of the BlockIcon, or -1
     */
    public int getIndex(BlockIcon block) {
        return blocks.indexOf(block);
    }

    /**
     * Remove the block at index. Returns false if index is outside of the list
     * @param index
     * @param isUndo
     * @return
     */
    public boolean removeBlock(int index, boolean isUndo) {
        if (index < 0 || index > blocks.size()) {
            return false;
        }

        if (!isUndo) {
            String historyStr = "REMOVE " + index + " "
                            + blocks.get(index).name();
            for (String str : blocks.get(index).getBlock().getParams()) {
                historyStr = historyStr.concat(" " + str);
            }
            history.addAction(historyStr);
        }
        blockIconPanel.remove(blocks.get(index));
        blocks.remove(index);
        blockManager.removeBlock(index);
        BlockIcon blockIcon;
        for (int i = 0; i < blocks.size() - 1; i++) {
            blockIcon = blocks.get(i);
            blockIconPanel.remove(blockIcon);
            con.gridx = i;
            blockIconPanel.add(blockIcon, con);
        }
        blockIconPanel.revalidate();
        blockIconPanel.repaint();
        return true;
    }

    /**
     * Remove a block. Returns false if the block can't be found
     * @param block
     * @param isUndo
     * @return
     */
    public boolean removeBlock(BlockIcon block, boolean isUndo) {
        return removeBlock(blocks.indexOf(block), isUndo);
    }

    /**
     * Add an action to the undo history for this list
     * @param action Should be in the format specified in {@link BlockHistory}
     */
    public void addToHistory(String action) {
        this.history.addAction(action);
    }

    /**
     * Save the history to a file
     */
    public void save(String filename) {
        try {
            history.save(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the history from a predetermined file
     */
    public void load(String filename) {
        try {
            history.load(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear the list
     */
    public void clear() {
        blocks.clear();
        blockManager.removeAll();
        blockIconPanel.removeAll();
    }

    /**
     * Get the size of the list
     * @return The number of blocks in the list
     */
    public int length() {
        return blocks.size();
    }

    /**
     * Get the block at a given index. Throws an IndexOutOfBoundsException is index is too large or small
     * @param index
     * @return
     */
    public BlockIcon getElementAt(int index) {
        return blocks.get(index);
    }

    /**
     * Return the blockManager associated with this list
     * @return
     */
    public MidiBlocksClient getClient() {
        return blockManager.getClient();
    }

}
