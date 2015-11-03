package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import MIDIBlocks.BlockManager;
import MIDIBlocks.MidiBlocksClient;
import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MidiBlocksView {

    private JFrame frame;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ButtonGroup buttonGroup_1 = new ButtonGroup();
    Metronome lblMetronome;

    private MidiBlocksClient client;
    private Keyboard keyboard;
    private IconList processingList;
    private BlockManager blockManager;
    private MusicManager musicManager;

    /**
     * Create the application.
     */
    public MidiBlocksView(MidiBlocksClient client) {
        this.client = client;
        blockManager = client.getBlockManager();
        musicManager = client.getMusicManager();
    }

    /**
     * Initialize the contents of the frame.
     */
    public void initialize(MidiBlocksController controller) {
        Font fontAwesome = null;
        try {
            fontAwesome = new Font("fonts/fontawesome-webfont.ttf",
                            Font.TRUETYPE_FONT, 20);
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            String key;
            while (keys.hasMoreElements()) {
                key = (String) keys.nextElement();
                if (key != null && key.contains("font")) {
                    UIManager.put(key, fontAwesome);
                }
            }
            UIManager.put("Label.font", fontAwesome);
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look
            // and feel.
            fontAwesome = UIManager.getFont("defaultFont");
        }

        frame = new JFrame();
        frame.setBounds(100, 100, 1080, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gridBagLayout);

        JPanel controls = new JPanel();
        GridBagConstraints gbc_controls = new GridBagConstraints();
        gbc_controls.weightx = 1.0;
        gbc_controls.weighty = 0.1;
        gbc_controls.fill = GridBagConstraints.BOTH;
        gbc_controls.gridx = 0;
        gbc_controls.gridy = 0;
        frame.getContentPane().add(controls, gbc_controls);
        controls.setLayout(new GridBagLayout());

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
        gbc_tabbedPane.weighty = 1.0;
        gbc_tabbedPane.weightx = 0.5;
        gbc_tabbedPane.insets = new Insets(10, 0, 10, 5);
        gbc_tabbedPane.fill = GridBagConstraints.BOTH;
        gbc_tabbedPane.gridx = 0;
        gbc_tabbedPane.gridy = 0;
        controls.add(tabbedPane, gbc_tabbedPane);

        addBlockSelect(tabbedPane);

        /* Add Tempo and Scales to the TabbedPane */

        JPanel settingsPanel = new JPanel();
        tabbedPane.addTab("Settings", null, settingsPanel, null);
        settingsPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
                        null, null));
        settingsPanel.setLayout(new GridBagLayout());

        JFormattedTextField tempoField = addTempo(settingsPanel);

        JComboBox<String> modeSelect = addModeSelect(settingsPanel);

        addRootNotes(settingsPanel);

        /* Add Interface Selection to the TabbedPane */

        JPanel interfaceSelect = new JPanel();
        tabbedPane.addTab("Interface Selection", null, interfaceSelect, null);
        interfaceSelect.setLayout(new GridBagLayout());

        addInputSelect(controller, interfaceSelect);

        addOutputSelect(controller, interfaceSelect);

        addStartStop(interfaceSelect);

        addMetronome(controls);

        /* Create processing line elements */

        addProcessingLine();

        /* Create the virtual keyboard */

        addKeyboard();

        createMenu(tempoField, modeSelect, controller);
    }

    /**
     * Create a menu bar, populated with items
     * 
     * @param tempoField
     * @param modeSelect
     * @param controller
     *            TODO
     */
    private void createMenu(JFormattedTextField tempoField,
                    JComboBox<String> modeSelect,
                    MidiBlocksController controller) {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem mntmOpen = new JMenuItem("Open MIDI");
        fileMenu.add(mntmOpen);

        JMenuItem mntmSave = new JMenuItem("Save MIDI");
        mntmSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                client.save("testfile.mid");
            }
        });
        fileMenu.add(mntmSave);

        JMenuItem mntmSaveConfiguration = new JMenuItem("Save Configuration");
        mntmSaveConfiguration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                "Configuration file", "conf");
                chooser.setFileFilter(filter);
                int retVal = chooser.showOpenDialog(null);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    processingList.save(chooser.getSelectedFile()
                                    .getAbsolutePath());
                }
            }
        });
        fileMenu.add(mntmSaveConfiguration);

        JMenuItem mntmLoadConfiguration = new JMenuItem("Load Configuration");
        mntmLoadConfiguration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                "Configuration file", "conf");
                chooser.setFileFilter(filter);
                int retVal = chooser.showOpenDialog(null);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    processingList.load(chooser.getSelectedFile()
                                    .getAbsolutePath());
                }
            }
        });
        fileMenu.add(mntmLoadConfiguration);

        JMenuItem mntmLoadScales = new JMenuItem("Load Scales");
        mntmLoadScales.addActionListener(controller.scaleListener);
        fileMenu.add(mntmLoadScales);

        JMenu toolsMenu = new JMenu("Tools");
        menuBar.add(toolsMenu);

        JMenuItem mntmTempo = new JMenuItem("Tempo");
        mntmTempo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String retVal = JOptionPane.showInputDialog(
                                "Enter a new tempo", client.getTempo());
                if (retVal == null) {
                    return;
                }
                try {
                    int newTempo = Integer.parseInt(retVal);
                    client.setTempo(newTempo);
                    tempoField.setValue(client.getTempo());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, retVal
                                    + " is not a valid tempo", "Tempo error",
                                    JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        toolsMenu.add(mntmTempo);

        JMenuItem mntmScale = new JMenuItem("Scale");
        mntmScale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String retVal = (String) JOptionPane.showInputDialog(null,
                                "Select a scale", "Scale select",
                                JOptionPane.QUESTION_MESSAGE, null, musicManager
                                                .getModeNames().toArray(),
                                client.getMode());
                if (retVal != null) {
                    musicManager.setScale(retVal, client.getRootNote());
                    modeSelect.setSelectedItem(client.getMode());
                }
            }
        });
        toolsMenu.add(mntmScale);

        JMenuItem mntmUndo = new JMenuItem("Undo");
        mntmUndo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processingList.undo();
            }
        });
        toolsMenu.add(mntmUndo);
    }

    /**
     * Create the virtual keyboard
     */
    private void addKeyboard() {
        JPanel keyboardContainer = new JPanel();
        GridBagConstraints gbc_keyboardContainer = new GridBagConstraints();
        gbc_keyboardContainer.anchor = GridBagConstraints.NORTH;
        gbc_keyboardContainer.weightx = 1.0;
        gbc_keyboardContainer.weighty = 0.4;
        gbc_keyboardContainer.fill = GridBagConstraints.BOTH;
        gbc_keyboardContainer.gridx = 0;
        gbc_keyboardContainer.gridy = 2;
        frame.getContentPane().add(keyboardContainer, gbc_keyboardContainer);
        GridBagLayout gbl_keyboardContainer = new GridBagLayout();
        keyboardContainer.setLayout(gbl_keyboardContainer);

        keyboard = new Keyboard(musicManager);
        musicManager.setKeyboard(keyboard);
        keyboard.setBorder(null);
        GridBagConstraints gbc_keyboard = new GridBagConstraints();
        gbc_keyboard.weightx = 1.0;
        gbc_keyboard.weighty = 1.0;
        gbc_keyboard.fill = GridBagConstraints.BOTH;
        gbc_keyboard.anchor = GridBagConstraints.NORTH;
        gbc_keyboard.gridx = 0;
        gbc_keyboard.gridy = 1;
        keyboardContainer.add(keyboard, gbc_keyboard);

        JPanel keyControls = new JPanel();
        keyControls.setBorder(new LineBorder(new Color(0, 0, 0)));
        GridBagConstraints gbc_keyControls = new GridBagConstraints();
        gbc_keyControls.weightx = 1.0;
        gbc_keyControls.anchor = GridBagConstraints.NORTH;
        gbc_keyControls.fill = GridBagConstraints.HORIZONTAL;
        gbc_keyControls.gridx = 0;
        gbc_keyControls.gridy = 0;
        keyboardContainer.add(keyControls, gbc_keyControls);
        GridBagLayout gbl_keyControls = new GridBagLayout();
        keyControls.setLayout(gbl_keyControls);

        addKeyboardControls(keyControls);
    }

    /**
     * Add the options to move the keyboard from side to side
     * 
     * @param keyControls
     */
    private void addKeyboardControls(JPanel keyControls) {
        JButton octLeft = new JButton("<<");
        octLeft.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                keyboard.setRootPos(keyboard.getRootPos() + musicManager.getOctave());
            }
        });
        GridBagConstraints gbc_octLeft = new GridBagConstraints();
        gbc_octLeft.insets = new Insets(0, 0, 0, 5);
        gbc_octLeft.gridx = 0;
        gbc_octLeft.gridy = 0;
        keyControls.add(octLeft, gbc_octLeft);

        /* Create movement buttons */

        JButton incLeft = new JButton("<");
        incLeft.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                keyboard.setRootPos(keyboard.getRootPos() + 1);
            }
        });
        GridBagConstraints gbc_incLeft = new GridBagConstraints();
        gbc_incLeft.insets = new Insets(0, 0, 0, 5);
        gbc_incLeft.gridx = 1;
        gbc_incLeft.gridy = 0;
        keyControls.add(incLeft, gbc_incLeft);

        JLabel lblKeyboardControls = new JLabel("Keyboard Controls");
        lblKeyboardControls.setLabelFor(keyControls);
        GridBagConstraints gbc_lblKeyboardControls = new GridBagConstraints();
        gbc_lblKeyboardControls.insets = new Insets(0, 0, 0, 5);
        gbc_lblKeyboardControls.gridx = 2;
        gbc_lblKeyboardControls.gridy = 0;
        keyControls.add(lblKeyboardControls, gbc_lblKeyboardControls);

        JButton incRight = new JButton(">");
        incRight.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                keyboard.setRootPos(keyboard.getRootPos() - 1);
            }
        });
        GridBagConstraints gbc_incRight = new GridBagConstraints();
        gbc_incRight.insets = new Insets(0, 0, 0, 5);
        gbc_incRight.gridx = 3;
        gbc_incRight.gridy = 0;
        keyControls.add(incRight, gbc_incRight);

        JButton octRight = new JButton(">>");
        octRight.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                keyboard.setRootPos(keyboard.getRootPos() - musicManager.getOctave());
            }
        });
        GridBagConstraints gbc_octRight = new GridBagConstraints();
        gbc_octRight.gridx = 4;
        gbc_octRight.gridy = 0;
        keyControls.add(octRight, gbc_octRight);
    }

    /**
     * Add the processing line, that contains the visual representation of
     * blocks as well as some controls
     */
    private void addProcessingLine() {
        JPanel processingLine = new JPanel();
        GridBagConstraints gbc_processingLine = new GridBagConstraints();
        gbc_processingLine.weightx = 1.0;
        gbc_processingLine.weighty = 0.3;
        gbc_processingLine.fill = GridBagConstraints.BOTH;
        gbc_processingLine.insets = new Insets(0, 0, 5, 0);
        gbc_processingLine.gridx = 0;
        gbc_processingLine.gridy = 1;
        frame.getContentPane().add(processingLine, gbc_processingLine);
        GridBagLayout gbl_processingLine = new GridBagLayout();
        processingLine.setLayout(gbl_processingLine);

        JPanel playback = new JPanel();
        GridBagConstraints gbc_playback = new GridBagConstraints();
        gbc_playback.weighty = 0.7;
        gbc_playback.fill = GridBagConstraints.BOTH;
        gbc_playback.gridx = 0;
        gbc_playback.gridy = 1;
        processingLine.add(playback, gbc_playback);
        GridBagLayout gbl_playback = new GridBagLayout();
        playback.setLayout(gbl_playback);

        processingList = new IconList(blockManager);
        processingList.setBorder(new BevelBorder(BevelBorder.RAISED));
        GridBagConstraints gbc_processingList = new GridBagConstraints();
        gbc_processingList.weighty = 1.0;
        gbc_processingList.weightx = 1.0;
        gbc_processingList.fill = GridBagConstraints.BOTH;
        gbc_processingList.anchor = GridBagConstraints.WEST;
        gbc_processingList.gridx = 0;
        gbc_processingList.gridy = 0;
        playback.add(processingList, gbc_processingList);

        JPanel processingHeader = new JPanel();
        GridBagConstraints gbc_processingHeader = new GridBagConstraints();
        gbc_processingHeader.weightx = 1.0;
        gbc_processingHeader.fill = GridBagConstraints.HORIZONTAL;
        gbc_processingHeader.anchor = GridBagConstraints.NORTHWEST;
        gbc_processingHeader.gridx = 0;
        gbc_processingHeader.gridy = 0;
        processingLine.add(processingHeader, gbc_processingHeader);

        JLabel lblProcessingLine = new JLabel("Processing Line");
        processingHeader.add(lblProcessingLine);
    }

    /**
     * Add the pane to choose blocks to add to the line
     * 
     * @param tabbedPane
     */
    private void addBlockSelect(JTabbedPane tabbedPane) {
        JPanel blocksPanel = new JPanel();
        tabbedPane.addTab("Blocks", null, blocksPanel, "Add blocks to the line");
        blocksPanel.setBorder(new CompoundBorder(new BevelBorder(
                        BevelBorder.RAISED, null, null, null, null),
                        new EmptyBorder(10, 10, 10, 10)));
        GridBagLayout gbl_blocksPanel = new GridBagLayout();
        gbl_blocksPanel.columnWeights = new double[] { 1.0 };
        blocksPanel.setLayout(gbl_blocksPanel);

        JLabel lblBlocks = new JLabel("Blocks:");
        GridBagConstraints gbc_lblBlocks = new GridBagConstraints();
        gbc_lblBlocks.anchor = GridBagConstraints.NORTHWEST;
        gbc_lblBlocks.fill = GridBagConstraints.BOTH;
        gbc_lblBlocks.weighty = 1.0;
        gbc_lblBlocks.weightx = 1.0;
        gbc_lblBlocks.gridx = 0;
        gbc_lblBlocks.gridy = 0;
        blocksPanel.add(lblBlocks, gbc_lblBlocks);

        JComboBox<String> blockSelect = new JComboBox<>();
        blockSelect.setModel((ComboBoxModel<String>) new DefaultComboBoxModel<String>(
                        new String[] { "Arpeggiator", "Chordify", "Gate",
                                "Monophonic", "Pitch Shift" }));
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.gridx = 0;
        gbc_comboBox.gridy = 1;
        blocksPanel.add(blockSelect, gbc_comboBox);

        JButton btnAddBlock = new JButton("Add Block");
        btnAddBlock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String blockText = (String) blockSelect.getSelectedItem();
                int blockIndex;
                switch (blockText) {
                case "Arpeggiator":
                    blockIndex = 0;
                    break;
                case "Chordify":
                    blockIndex = 1;
                    break;
                case "Gate":
                    blockIndex = 2;
                    break;
                case "Monophonic":
                    blockIndex = 3;
                    break;
                case "Pitch Shift":
                    blockIndex = 4;
                    break;
                default:
                    blockIndex = -1;
                    break;
                }
                BlockIcon blockIcon = new BlockIcon(blockIndex, client);
                processingList.add(processingList.length(), blockIcon, false);
            }
        });
        GridBagConstraints gbc_btnAddBlock = new GridBagConstraints();
        gbc_btnAddBlock.gridx = 0;
        gbc_btnAddBlock.gridy = 2;
        blocksPanel.add(btnAddBlock, gbc_btnAddBlock);
    }

    /**
     * Add the metronome, that is totally psychadelic
     * 
     * @param controls
     */
    private void addMetronome(JPanel controls) {
        JPanel metronomePanel = new JPanel();
        metronomePanel.setBorder(null);
        GridBagConstraints gbc_metronomePanel = new GridBagConstraints();
        gbc_metronomePanel.insets = new Insets(0, 0, 10, 10);
        gbc_metronomePanel.fill = GridBagConstraints.BOTH;
        gbc_metronomePanel.weighty = 1.0;
        gbc_metronomePanel.weightx = 0.4;
        gbc_metronomePanel.gridx = 1;
        gbc_metronomePanel.gridy = 0;
        controls.add(metronomePanel, gbc_metronomePanel);
        GridBagLayout gbl_metronomePanel = new GridBagLayout();
        metronomePanel.setLayout(gbl_metronomePanel);

        JPanel metroPanel = new JPanel();
        metroPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
                        null, null));
        GridBagConstraints gbc_metroPanel = new GridBagConstraints();
        gbc_metroPanel.weighty = 1.0;
        gbc_metroPanel.weightx = 1.0;
        gbc_metroPanel.fill = GridBagConstraints.BOTH;
        gbc_metroPanel.gridx = 0;
        gbc_metroPanel.gridy = 1;
        metronomePanel.add(metroPanel, gbc_metroPanel);
        GridBagLayout gbl_panel = new GridBagLayout();
        metroPanel.setLayout(gbl_panel);

        lblMetronome = new Metronome(musicManager);
        GridBagConstraints gbc_lblMetronome = new GridBagConstraints();
        gbc_lblMetronome.weighty = 1.0;
        gbc_lblMetronome.weightx = 1.0;
        gbc_lblMetronome.fill = GridBagConstraints.BOTH;
        gbc_lblMetronome.gridx = 0;
        gbc_lblMetronome.gridy = 0;
        metroPanel.add(lblMetronome, gbc_lblMetronome);

        JButton btnMute = new JButton("Mute");
        btnMute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                lblMetronome.mute();
            }
        });
        GridBagConstraints gbc_btnMute = new GridBagConstraints();
        gbc_btnMute.anchor = GridBagConstraints.EAST;
        gbc_btnMute.insets = new Insets(0, 0, 5, 0);
        gbc_btnMute.gridx = 0;
        gbc_btnMute.gridy = 0;
        metronomePanel.add(btnMute, gbc_btnMute);
    }

    /**
     * Add the controls for playback/recording
     * 
     * @param interfaceSelect
     */
    private void addStartStop(JPanel interfaceSelect) {
        JPanel startStopPanel = new JPanel();
        startStopPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null,
                        null, null, null));
        GridBagConstraints gbc_startStopPanel = new GridBagConstraints();
        gbc_startStopPanel.anchor = GridBagConstraints.NORTHWEST;
        gbc_startStopPanel.weightx = 0.1;
        gbc_startStopPanel.weighty = 1.0;
        gbc_startStopPanel.fill = GridBagConstraints.BOTH;
        gbc_startStopPanel.gridx = 2;
        gbc_startStopPanel.gridy = 0;
        interfaceSelect.add(startStopPanel, gbc_startStopPanel);
        GridBagLayout gbl_startStopPanel = new GridBagLayout();
        startStopPanel.setLayout(gbl_startStopPanel);

        JButton btnStart = new JButton("Play/Record");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (client.getOutput().isEmpty() && !musicManager.isNotified()) {
                    JOptionPane.showMessageDialog(null,
                                    "Warning: There is no output selected",
                                    "No Output", JOptionPane.ERROR_MESSAGE);
                    musicManager.setNotified(true);
                }
                client.play();
            }
        });
        GridBagConstraints gbc_btnStart = new GridBagConstraints();
        gbc_btnStart.weighty = 0.5;
        gbc_btnStart.weightx = 1.0;
        gbc_btnStart.insets = new Insets(0, 0, 5, 0);
        gbc_btnStart.gridx = 0;
        gbc_btnStart.gridy = 0;
        startStopPanel.add(btnStart, gbc_btnStart);

        JButton btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.stop();
            }
        });
        GridBagConstraints gbc_btnStop = new GridBagConstraints();
        gbc_btnStop.weighty = 0.5;
        gbc_btnStop.weightx = 1.0;
        gbc_btnStop.gridx = 0;
        gbc_btnStop.gridy = 1;
        startStopPanel.add(btnStop, gbc_btnStop);
    }

    /**
     * Add options to choose output
     * 
     * @param controller
     * @param interfaceSelect
     */
    private void addOutputSelect(MidiBlocksController controller,
                    JPanel interfaceSelect) {
        JPanel outputSelectPanel = new JPanel();
        GridBagConstraints gbc_outputSelectPanel = new GridBagConstraints();
        gbc_outputSelectPanel.insets = new Insets(0, 0, 0, 5);
        gbc_outputSelectPanel.weighty = 1.0;
        gbc_outputSelectPanel.weightx = 0.5;
        gbc_outputSelectPanel.fill = GridBagConstraints.BOTH;
        gbc_outputSelectPanel.anchor = GridBagConstraints.NORTHWEST;
        gbc_outputSelectPanel.gridx = 1;
        gbc_outputSelectPanel.gridy = 0;
        interfaceSelect.add(outputSelectPanel, gbc_outputSelectPanel);
        outputSelectPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null,
                        null, null, null));
        GridBagLayout gbl_outputSelectPanel = new GridBagLayout();
        outputSelectPanel.setLayout(gbl_outputSelectPanel);

        JLabel lblOutputSelect = new JLabel("Output Selection");
        GridBagConstraints gbc_lblOutputSelect = new GridBagConstraints();
        gbc_lblOutputSelect.weighty = 0.2;
        gbc_lblOutputSelect.weightx = 1.0;
        gbc_lblOutputSelect.gridx = 0;
        gbc_lblOutputSelect.gridy = 0;
        outputSelectPanel.add(lblOutputSelect, gbc_lblOutputSelect);

        JToggleButton tglbtnOutFile = new JToggleButton("File");
        tglbtnOutFile.addActionListener(controller.outListener);
        tglbtnOutFile.setActionCommand("File");
        GridBagConstraints gbc_tglbtnOutFile = new GridBagConstraints();
        gbc_tglbtnOutFile.weighty = 0.5;
        gbc_tglbtnOutFile.weightx = 1.0;
        gbc_tglbtnOutFile.fill = GridBagConstraints.BOTH;
        gbc_tglbtnOutFile.gridx = 0;
        gbc_tglbtnOutFile.gridy = 1;
        outputSelectPanel.add(tglbtnOutFile, gbc_tglbtnOutFile);

        JToggleButton tglbtnSynthesizer = new JToggleButton("Synthesizer");
        tglbtnSynthesizer.addActionListener(controller.outListener);
        tglbtnSynthesizer.setActionCommand("Synth");
        GridBagConstraints gbc_tglbtnSynthesizer = new GridBagConstraints();
        gbc_tglbtnSynthesizer.weighty = 0.5;
        gbc_tglbtnSynthesizer.weightx = 1.0;
        gbc_tglbtnSynthesizer.fill = GridBagConstraints.BOTH;
        gbc_tglbtnSynthesizer.gridx = 0;
        gbc_tglbtnSynthesizer.gridy = 2;
        outputSelectPanel.add(tglbtnSynthesizer, gbc_tglbtnSynthesizer);
    }

    /**
     * Add options to choose input
     * 
     * @param controller
     * @param interfaceSelect
     */
    private void addInputSelect(MidiBlocksController controller,
                    JPanel interfaceSelect) {
        JPanel inputSelectPanel = new JPanel();
        GridBagConstraints gbc_inputSelectPanel = new GridBagConstraints();
        gbc_inputSelectPanel.fill = GridBagConstraints.BOTH;
        gbc_inputSelectPanel.weighty = 1.0;
        gbc_inputSelectPanel.weightx = 0.5;
        gbc_inputSelectPanel.anchor = GridBagConstraints.NORTHWEST;
        gbc_inputSelectPanel.insets = new Insets(0, 0, 0, 5);
        gbc_inputSelectPanel.gridx = 0;
        gbc_inputSelectPanel.gridy = 0;
        interfaceSelect.add(inputSelectPanel, gbc_inputSelectPanel);
        inputSelectPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null,
                        null, null, null));
        GridBagLayout gbl_inputSelectPanel = new GridBagLayout();
        inputSelectPanel.setLayout(gbl_inputSelectPanel);

        JLabel lblInput = new JLabel("Input Selection");
        lblInput.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblInput = new GridBagConstraints();
        gbc_lblInput.anchor = GridBagConstraints.NORTH;
        gbc_lblInput.weighty = 0.2;
        gbc_lblInput.weightx = 1.0;
        gbc_lblInput.fill = GridBagConstraints.BOTH;
        gbc_lblInput.gridx = 0;
        gbc_lblInput.gridy = 0;
        inputSelectPanel.add(lblInput, gbc_lblInput);

        JToggleButton tglbtnFile = new JToggleButton("File");
        tglbtnFile.addActionListener(controller.sourceListener);
        buttonGroup.add(tglbtnFile);
        GridBagConstraints gbc_tglbtnFile = new GridBagConstraints();
        gbc_tglbtnFile.anchor = GridBagConstraints.NORTH;
        gbc_tglbtnFile.weighty = 0.3;
        gbc_tglbtnFile.weightx = 1.0;
        gbc_tglbtnFile.fill = GridBagConstraints.BOTH;
        gbc_tglbtnFile.gridx = 0;
        gbc_tglbtnFile.gridy = 1;
        inputSelectPanel.add(tglbtnFile, gbc_tglbtnFile);

        JToggleButton tglbtnDevice = new JToggleButton("Device");
        tglbtnDevice.addActionListener(controller.sourceListener);
        buttonGroup.add(tglbtnDevice);
        GridBagConstraints gbc_tglbtnDevice = new GridBagConstraints();
        gbc_tglbtnDevice.anchor = GridBagConstraints.NORTH;
        gbc_tglbtnDevice.weighty = 0.3;
        gbc_tglbtnDevice.weightx = 1.0;
        gbc_tglbtnDevice.fill = GridBagConstraints.BOTH;
        gbc_tglbtnDevice.gridx = 0;
        gbc_tglbtnDevice.gridy = 2;
        inputSelectPanel.add(tglbtnDevice, gbc_tglbtnDevice);

        JToggleButton tglbtnVirtualKeyboard = new JToggleButton(
                        "Virtual Keyboard");
        tglbtnVirtualKeyboard.addActionListener(controller.sourceListener);
        buttonGroup.add(tglbtnVirtualKeyboard);
        tglbtnVirtualKeyboard.setSelected(true);
        GridBagConstraints gbc_tglbtnVirtualKeyboard = new GridBagConstraints();
        gbc_tglbtnVirtualKeyboard.anchor = GridBagConstraints.NORTH;
        gbc_tglbtnVirtualKeyboard.weighty = 0.3;
        gbc_tglbtnVirtualKeyboard.weightx = 1.0;
        gbc_tglbtnVirtualKeyboard.fill = GridBagConstraints.BOTH;
        gbc_tglbtnVirtualKeyboard.gridx = 0;
        gbc_tglbtnVirtualKeyboard.gridy = 3;
        inputSelectPanel.add(tglbtnVirtualKeyboard, gbc_tglbtnVirtualKeyboard);
    }

    /**
     * Add the root notes to the setting panel
     * 
     * @param settingsPanel
     */
    private void addRootNotes(JPanel settingsPanel) {
        JPanel notes = new JPanel();
        GridBagConstraints gbc_notes = new GridBagConstraints();
        gbc_notes.weighty = 0.5;
        gbc_notes.weightx = 1.0;
        gbc_notes.gridwidth = 2;
        gbc_notes.fill = GridBagConstraints.BOTH;
        gbc_notes.insets = new Insets(0, 0, 0, 0);
        gbc_notes.gridx = 0;
        gbc_notes.gridy = 1;
        settingsPanel.add(notes, gbc_notes);
        GridBagLayout gbl_notes = new GridBagLayout();
        notes.setLayout(gbl_notes);

        /* Add potential root notes to the settings */

        JToggleButton[] rootNotes = new JToggleButton[12];
        GridBagConstraints[] rootNoteConstraints = new GridBagConstraints[12];
        for (int i = 0; i < 12; i++) {
            rootNotes[i] = new JToggleButton(Note.getNoteName(i));
            buttonGroup_1.add(rootNotes[i]);
            rootNotes[i].addMouseListener(new RootSelect(rootNotes[i]));
            rootNoteConstraints[i] = new GridBagConstraints();
            rootNoteConstraints[i].weighty = 1.0;
            rootNoteConstraints[i].weightx = 0.1;
            rootNoteConstraints[i].fill = GridBagConstraints.BOTH;
            rootNoteConstraints[i].anchor = GridBagConstraints.NORTHWEST;
            rootNoteConstraints[i].insets = new Insets(0, 0, 0, 0);
            rootNoteConstraints[i].gridx = i % 6;
            rootNoteConstraints[i].gridy = i / 6;
            notes.add(rootNotes[i], rootNoteConstraints[i]);
        }
        rootNotes[0].setSelected(true);
    }

    /**
     * Add the combo box for choosing the mode
     * 
     * @param settingsPanel
     * @return
     */
    private JComboBox<String> addModeSelect(JPanel settingsPanel) {
        JPanel mode = new JPanel();
        GridBagConstraints gbc_mode = new GridBagConstraints();
        gbc_mode.weightx = 0.6;
        gbc_mode.weighty = 0.3;
        gbc_mode.fill = GridBagConstraints.BOTH;
        gbc_mode.insets = new Insets(0, 0, 5, 0);
        gbc_mode.gridx = 1;
        gbc_mode.gridy = 0;
        settingsPanel.add(mode, gbc_mode);
        GridBagLayout gbl_mode = new GridBagLayout();
        mode.setLayout(gbl_mode);

        JLabel lblMode = new JLabel("Mode:");
        lblMode.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblMode = new GridBagConstraints();
        gbc_lblMode.weightx = 0.3;
        gbc_lblMode.weighty = 1.0;
        gbc_lblMode.fill = GridBagConstraints.BOTH;
        gbc_lblMode.anchor = GridBagConstraints.WEST;
        gbc_lblMode.insets = new Insets(0, 0, 0, 5);
        gbc_lblMode.gridx = 0;
        gbc_lblMode.gridy = 0;
        mode.add(lblMode, gbc_lblMode);

        JComboBox<String> modeSelect = new JComboBox<>();
        modeSelect.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    musicManager.setScale((String) event.getItem(),
                                    client.getRootNote());

                }
            }
        });
        lblMode.setLabelFor(modeSelect);
        modeSelect.setModel(client.getModeModel());
        modeSelect.setSelectedIndex(modeSelect.getItemCount() - 1);
        GridBagConstraints gbc_modeSelect = new GridBagConstraints();
        gbc_modeSelect.fill = GridBagConstraints.HORIZONTAL;
        gbc_modeSelect.weightx = 0.5;
        gbc_modeSelect.weighty = 1.0;
        gbc_modeSelect.anchor = GridBagConstraints.WEST;
        gbc_modeSelect.gridx = 1;
        gbc_modeSelect.gridy = 0;
        mode.add(modeSelect, gbc_modeSelect);
        return modeSelect;
    }

    /**
     * Add the text field for choosing tempo
     * 
     * @param settingsPanel
     * @return
     */
    private JFormattedTextField addTempo(JPanel settingsPanel) {
        JPanel tempo = new JPanel();
        GridBagConstraints gbc_tempo = new GridBagConstraints();
        gbc_tempo.weighty = 0.3;
        gbc_tempo.weightx = 0.2;
        gbc_tempo.fill = GridBagConstraints.BOTH;
        gbc_tempo.insets = new Insets(0, 0, 5, 5);
        gbc_tempo.gridx = 0;
        gbc_tempo.gridy = 0;
        settingsPanel.add(tempo, gbc_tempo);
        GridBagLayout gbl_tempo = new GridBagLayout();
        tempo.setLayout(gbl_tempo);

        JLabel lblTempo = new JLabel("Tempo:");
        lblTempo.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblTempo = new GridBagConstraints();
        gbc_lblTempo.weightx = 0.3;
        gbc_lblTempo.weighty = 1.0;
        gbc_lblTempo.fill = GridBagConstraints.BOTH;
        gbc_lblTempo.anchor = GridBagConstraints.WEST;
        gbc_lblTempo.insets = new Insets(0, 0, 0, 5);
        gbc_lblTempo.gridx = 0;
        gbc_lblTempo.gridy = 0;
        tempo.add(lblTempo, gbc_lblTempo);

        JFormattedTextField tempoField = new JFormattedTextField();
        lblTempo.setLabelFor(tempoField);
        tempoField.setHorizontalAlignment(SwingConstants.CENTER);
        tempoField.setText("120");
        client.addTempoListener(tempoField);
        tempoField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent arg0) {
                /* Don't need to do anything */
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                /* Update tempo based on current value */
                JFormattedTextField text = (JFormattedTextField) arg0
                                .getSource();
                String tempo = text.getText();
                try {
                    int tempoVal = Integer.parseInt(tempo);
                    client.setTempo(tempoVal);
                } catch (NumberFormatException e) {
                    /* Entered an invalid number */
                    JOptionPane.showMessageDialog(null,
                                    "Please enter a valid number",
                                    "Tempo Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        });
        GridBagConstraints gbc_tempoField = new GridBagConstraints();
        gbc_tempoField.fill = GridBagConstraints.HORIZONTAL;
        gbc_tempoField.weighty = 1.0;
        gbc_tempoField.weightx = 0.7;
        gbc_tempoField.anchor = GridBagConstraints.WEST;
        gbc_tempoField.gridx = 1;
        gbc_tempoField.gridy = 0;
        tempo.add(tempoField, gbc_tempoField);
        return tempoField;
    }

    /**
     * @return the frame
     */
    public JFrame getFrame() {
        return frame;
    }

    public MidiBlocksClient getClient() {
        return client;
    }

    // Should be moved to controller

    private class RootSelect extends MouseAdapter {

        private JToggleButton button;

        RootSelect(JToggleButton button) {
            this.button = button;
        }

        @Override
        public void mouseClicked(MouseEvent arg0) {
            musicManager.setScale(client.getMode(), button.getText());
        }
    }

}
