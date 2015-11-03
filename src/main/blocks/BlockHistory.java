package blocks;

import gui.BlockIcon;
import gui.IconList;
import io.InvalidFileException;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Version control system for blocks. ish. Uses a patch system, to be able to
 * revert changes.
 * 
 * Valid options are - RIGHT "to_index", LEFT "to_index", ADD "block_index",
 * REMOVE "block_index" "block_name" "block_params", PARAM "block_index"
 * "param_num "old_value", MOVE "to_index" "from_index"
 * 
 * @author Aidan
 *
 */
public class BlockHistory {

    private LinkedList<String> patches;
    private IconList list;
    private static int HISTORY_SIZE = 20;

    /**
     * Create a brand new BlockHistory (Nice and shiny)
     * @param list
     */
    public BlockHistory(IconList list) {
        this.list = list;
        patches = new LinkedList<>();
    }

    /**
     * Add an action to the list. If there's too many, we'll throw the oldest one out
     * @param action
     */
    public void addAction(String action) {
        if (patches.size() < HISTORY_SIZE) {
            patches.add(action);
        } else {
            patches.removeFirst();
            patches.add(action);
        }
    }

    /**
     * Revert the most recent change
     */
    public void undo() {
        if (patches.size() > 0) {
            String action = patches.removeLast();
            String[] actionSep = action.split(" ", 2);
            String command = actionSep[0];
            /* Target may still be multiple words */
            String target = actionSep[1];
            /* Reverse the action */
            switch (command) {
            case "RIGHT":
                /* Move left */
                list.moveLeft(Integer.parseInt(target), true);
                break;
            case "LEFT":
                /* Move right */
                list.moveRight(Integer.parseInt(target), true);
                break;
            case "ADD":
                /* Remove */
                list.removeBlock(Integer.parseInt(target.split(" ")[0]), true);
                break;
            case "REMOVE":
                /* Add */
                String[] targetArray = target.split(" ", 4);
                for (String string : targetArray) {
                    System.out.println(string);
                }
                String index = targetArray[0];
                String name = targetArray[1];
                String[] params = new String[targetArray.length - 2];
                System.arraycopy(targetArray, 2, params, 0,
                                targetArray.length - 2);
                BlockIcon icon = new BlockIcon(AbstractBlock.getIndex(name),
                                list.getClient());
                icon.getBlock().setParams(params);
                int numParams = icon.getBlock().getParamNames().length;
                Component comp;
                for (int i = 0; i < numParams; i++) {
                    comp = icon.getInputField(i);
                    if (comp instanceof JTextField) {
                        ((JTextField) comp).setText(params[i]);
                    } else if (comp instanceof JComboBox) {
                        ((JComboBox<?>) comp).setSelectedItem(params[i]);
                    }
                }
                list.add(Integer.valueOf(index), icon, true);
                break;
            case "PARAM":
                /* Revert */
                icon = list.getBlock(Integer.parseInt(target.split(" ")[0]));
                int paramNum = Integer.parseInt(target.split(" ")[1]);
                params = icon.getBlock().getParams();
                params[paramNum] = target.split(" ")[2];
                icon.getBlock().setParams(params);
                JComponent input = icon.getInputField(paramNum);
                if (input instanceof JTextField) {
                    ((JTextField) input).setText(target.split(" ")[2]);
                } else if (input instanceof JComboBox<?>) {
                    ((JComboBox<?>) input)
                                    .setSelectedItem(target.split(" ")[2]);
                }
            case "MOVE":
                /* Move in the opposite direction */
                list.move(Integer.parseInt(target.split(" ")[0]),
                                Integer.parseInt(target.split(" ")[1]), true);
            }
        }
    }

    /**
     * Save the current configuration of blocks to default configuration file, blocks.conf
     * @throws IOException
     */
    public void save() throws IOException {
        save("blocks.conf");
    }

    /**
     * Convert the current state of the process list to a text format, and write
     * it to filename. The format of saving is POSITION NAME PARAMS...\n
     * 
     * @param filename
     * @param list
     * @throws IOException
     */
    public void save(String filename) throws IOException {
        BlockIcon block;
        List<String> processText = new ArrayList<>();
        processText.add(list.getClient().getSource());
        String blockString;
        for (int i = 0; i < list.length(); i++) {
            block = list.getElementAt(i);
            blockString = String.valueOf(i) + " ";
            blockString = blockString.concat(block.name() + " ");
            for (String param : block.getBlock().getParams()) {
                blockString = blockString.concat(param + " ");
            }
            processText.add(blockString);
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(new File(filename))));
            for (String string : processText) {
                writer.write(string);
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Load from the default blocks.conf file
     * @throws IOException
     */
    public void load() throws IOException {
        load("blocks.conf");
    }

    /**
     * Load a layout from a file and put it in the processList
     * 
     * @param filename
     * @param list
     * @throws IOException
     */
    public void load(String filename) throws IOException {
        list.getClient().stop();
        BufferedReader reader = new BufferedReader(new FileReader(new File(
                        filename)));
        list.clear();
        String line;
        line = reader.readLine();
        String[] inputs = line.split(line, 2);
        if (inputs[1] != null) {
            try {
                list.getClient().setSource(inputs[0], inputs[1]);
            } catch (InvalidFileException e) {
                e.printStackTrace();
            }
        } else {
            list.getClient().setSource(inputs[0]);
        }
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            list.add(Integer.parseInt(line.split(" ", 2)[0]),
                            makeBlock(line.split(" ", 2)[1]), false);
        }
        reader.close();
    }

    /**
     * Make a block
     * @param line String in the format NAME PARAMS
     * @return a BlockIcon
     */
    private BlockIcon makeBlock(String line) {
        String[] components = line.split(" ", 2);
        String name = components[0];
        String[] params = components[1].split(" ");
        BlockIcon block = new BlockIcon(AbstractBlock.getIndex(name),
                        list.getClient(), name + ".png");
        block.getBlock().setParams(params);
        return block;
    }
}
