package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import MIDIBlocks.MidiBlocksClient;
import MIDIBlocks.MidiBlocksLauncher;
import blocks.AbstractBlock;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

/**
 * The visual representation of a block, with some controls to make it useful
 * 
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class BlockIcon extends JPanel implements Transferable,
                DragGestureListener {

    private AbstractBlock block;
    private int blockIndex;
    private String filename;
    private JComboBox<String> blockSelect;
    private ImageIcon icon;
    private IconList list;
    private BlockIcon blockIcon;
    private ArrayList<JComponent> inputFields;

    /**
     * Base constructor, that adds all of the essential elements
     */
    private BlockIcon() {
        super();
        this.setLayout(new GridBagLayout());
        inputFields = new ArrayList<>();
        /*
         * Create border so there's some whitespace around the edge of the
         * component
         */
        this.setBorder(BorderFactory.createCompoundBorder(new BevelBorder(
                        BevelBorder.RAISED), new EmptyBorder(new Insets(5, 10,
                        5, 10))));
        this.setTransferHandler(new BlockTransferHandler());
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE,
                        this);
    }

    /**
     * Create a BlockIcon with an {@link AbstractBlock} based on index
     * 
     * @param index
     * @param client
     */
    public BlockIcon(int index, MidiBlocksClient client) {
        this();
        switch (index) {
        case 0:
            filename = "arpeggiator.png";
            break;
        case 1:
            filename = "chordify.png";
            break;
        case 2:
            filename = "gate.png";
            break;
        case 3:
            filename = "monophonic.png";
            break;
        case 4:
            filename = "pitchShift.png";
            break;
        default:
            filename = "NULL.png";
            break;
        }
        blockIndex = index;
        blockIcon = this;
        this.block = AbstractBlock.makeBlock(client.getMusicManager(), index);
        try {
            addIcon(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addBlockSelect();
        addParamsPanel();
        addBlockControls();
    }

    /**
     * Create a BlockIcon with {@link AbstractBlock} based on index, and the
     * image based on filename. Please make sure they match
     * 
     * @param index
     * @param client
     * @param filename
     *            Filename for the image to be displayed
     */
    public BlockIcon(int index, MidiBlocksClient client, String filename) {
        this();
        blockIndex = index;
        blockIcon = this;
        this.block = AbstractBlock.makeBlock(client.getMusicManager(), index);
        this.filename = filename;
        try {
            addIcon(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addBlockSelect();
        addParamsPanel();
        addBlockControls();
    }

    /**
     * Create a BlockIcon with {@link AbstractBlock} based on index, and the
     * image based on filename. Initialises the parameters to be params
     * 
     * @param index
     *            Index of the abstractBlock
     * @param client
     * @param filename
     *            Filename for the image to be displayed
     * @param params
     *            The starting parameters for the block
     */
    public BlockIcon(int index, MidiBlocksClient client, String filename,
                    String[] params) {
        this(index, client, filename);
        block.setParams(params);
    }

    /**
     * Get the internal block
     * 
     * @return
     */
    public AbstractBlock getBlock() {
        return block;
    }

    /**
     * Get the name of the block
     * 
     * @return
     */
    public String name() {
        return block.getName();
    }

    /**
     * Find out the filename for the block
     * 
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the {@link IconList} that this block belongs to
     * 
     * @return The IconList containing this block
     */
    public IconList getList() {
        return list;
    }

    /**
     * @param list
     *            the list to set
     */
    public void setList(IconList list) {
        this.list = list;
    }

    /**
     * Get the input field for a block at a given index. This will correspond to
     * the order expected by AbstractBlock.setParams();
     * 
     * @param index
     * @return
     */
    public JComponent getInputField(int index) {
        return inputFields.get(index);
    }

    /**
     * Add the image representing this block to the blockIcon
     * 
     * @param filename
     *            the path to the image
     * @throws IOException
     */
    private void addIcon(String filename) throws IOException {
        BufferedImage image = ImageIO.read(MidiBlocksLauncher.class
                        .getClassLoader().getResourceAsStream(filename));
        icon = new ImageIcon(image);
        JLabel lbl = new JLabel(icon);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.3;
        this.add(lbl, constraints);
    }

    /**
     * Add a combo box for block selection
     */
    private void addBlockSelect() {
        blockSelect = new JComboBox<String>(new String[] { "Arpeggiator",
                "Chordify", "Gate", "Monophonic", "Pitch Shift" });
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.7;
        blockSelect.setSelectedIndex(blockIndex);
        this.add(blockSelect, constraints);
    }

    /**
     * Add a panel for selection of parameters, dependent on block type
     */
    private void addParamsPanel() {
        JPanel paramsPanel = new JPanel();
        paramsPanel.setLayout(new GridBagLayout());
        addParams(paramsPanel);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        this.add(paramsPanel, constraints);
    }

    /**
     * Add the parameter selectors to the params panel
     * 
     * @param root
     */
    private void addParams(JPanel root) {
        GridBagConstraints paramNameCons = new GridBagConstraints();
        paramNameCons.fill = GridBagConstraints.HORIZONTAL;
        paramNameCons.weightx = 0.3;
        paramNameCons.anchor = GridBagConstraints.WEST;
        paramNameCons.gridx = 0;
        GridBagConstraints paramEntryCons = new GridBagConstraints();
        paramEntryCons.fill = GridBagConstraints.HORIZONTAL;
        paramEntryCons.gridx = 1;
        paramEntryCons.weightx = 0.5;
        paramEntryCons.anchor = GridBagConstraints.EAST;
        /* Add required params to panel */
        int i = 0;
        for (String param : block.getParamNames()) {
            paramNameCons.gridy = i;
            root.add(new JLabel(param + ": "), paramNameCons);
            paramEntryCons.gridy = i++;
            if (param == "Shift") {
                JTextField shift = new JTextField(block.getParams()[0]);
                shift.addFocusListener(new FocusListener() {

                    @Override
                    public void focusLost(FocusEvent event) {
                        setShift(shift.getText());
                    }

                    @Override
                    public void focusGained(FocusEvent event) {
                        /* Do nothing */
                    }
                });
                shift.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            setShift(shift.getText());
                        }
                    }
                });
                inputFields.add(shift);
                root.add(shift, paramEntryCons);
            } else if (param == "Pattern") {
                JComboBox<String> patterns = new JComboBox<>();
                patterns.addFocusListener(new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent arg0) {
                        /* Do nothing */
                    }

                    @Override
                    public void focusLost(FocusEvent arg0) {
                        setPattern((String) patterns.getSelectedItem());
                    }

                });

                patterns.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            setPattern((String) patterns.getSelectedItem());
                        }
                    }
                });
                for (String str : block.getParamValues()) {
                    patterns.addItem(str);
                }
                patterns.setSelectedItem(block.getParams()[0]);
                inputFields.add(patterns);
                root.add(patterns, paramEntryCons);
            } else if (param == "Mode") {
                JComboBox<String> mode = new JComboBox<>();
                mode.addFocusListener(new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent arg0) {
                        /* Do nothing */
                    }

                    @Override
                    public void focusLost(FocusEvent arg0) {
                        setMode((String) mode.getSelectedItem());
                    }

                });

                mode.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            setMode((String) mode.getSelectedItem());
                        }
                    }
                });
                for (String str : block.getParamValues()) {
                    mode.addItem(str);
                }
                mode.setSelectedItem(block.getParams()[1]);
                inputFields.add(mode);
                root.add(mode, paramEntryCons);
            } else if (param == "Notes per tick") {
                JTextField notesPerTick = new JTextField(block.getParams()[0]);
                inputFields.add(notesPerTick);
                root.add(notesPerTick, paramEntryCons);
                notesPerTick.addFocusListener(new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent arg0) {
                        /* Do nothing */
                    }

                    @Override
                    public void focusLost(FocusEvent arg0) {
                        setNotesPerTick((String) notesPerTick.getText());
                    }

                });

                notesPerTick.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            setNotesPerTick((String) notesPerTick.getText());
                        }
                    }
                });
            }
        }

    }

    /**
     * Add the controls that allow the block to move and be removed
     */
    private void addBlockControls() {
        JPanel controlPanel = new JPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 0.1;
        addButtons(controlPanel);
        this.add(controlPanel, constraints);
    }

    /**
     * Add the buttons to the panel root. 
     * @param root
     */
    private void addButtons(JPanel root) {
        JButton buttonL = new JButton();
        buttonL.setText("<");
        buttonL.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                list.moveLeft(blockIcon, false);
            }
        });
        root.add(buttonL);
        JButton buttonX = new JButton();
        buttonX.setText("X");
        buttonX.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                list.removeBlock(blockIcon, false);
            }
        });
        root.add(buttonX);
        JButton buttonR = new JButton();
        buttonR.setText(">");
        buttonR.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                list.moveRight(blockIcon, false);
            }
        });
        root.add(buttonR);
    }

    /**
     * Action handler for the shift parameter field
     * @param fieldText
     */
    private void setShift(String fieldText) {
        if (fieldText == "") {
            fieldText = "0";
        }
        try {
            Integer.parseInt(fieldText);
            list.addToHistory("PARAM " + list.getIndex(this) + " 0 "
                            + block.getParams()[0]);
            block.setParams(new String[] { fieldText });
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Shift of " + fieldText
                            + " is invalid", "Invalid parameter",
                            JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Action handler for the pattern parameter field
     * @param fieldText
     */
    private void setPattern(String fieldText) {
        list.addToHistory("PARAM " + list.getIndex(this) + " 0 "
                        + block.getParams()[0]);
        block.setParams(new String[] { fieldText });
    }

    /**
     * Action handler for the mode parameter field
     * @param fieldText
     */
    private void setMode(String fieldText) {
        list.addToHistory("PARAM " + list.getIndex(this) + " 1 "
                        + block.getParams()[1]);
        block.setParams(new String[] { block.getParams()[0], fieldText });
    }

    /**
     * Action handler for the notes per tick parameter field
     * @param fieldText
     */
    private void setNotesPerTick(String fieldText) {
        if (fieldText == "") {
            fieldText = "0.0";
        }
        try {
            Float.parseFloat(fieldText);
            list.addToHistory("PARAM " + list.getIndex(this) + " 0 "
                            + block.getParams()[0]);
            block.setParams(new String[] { fieldText, block.getParams()[1] });
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Notes per tick of "
                            + fieldText + " is invalid", "Invalid parameter",
                            JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get the index for this block in the list
     */
    @Override
    public Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException, IOException {
        return list.getIndex(this);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        this.getTransferHandler().exportAsDrag(this, dge.getTriggerEvent(),
                        TransferHandler.MOVE);
    }

}