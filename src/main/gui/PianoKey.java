package gui;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

/**
 * A PianoKey is a button on the keyboard, that will play a certain MIDI note when pressed
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class PianoKey extends JButton {

    /* Letters on the keyboard corresponding to the 15 visible keys */
    public static final String[] KEYBOARD_LETTERS = { "A", "W", "S", "E", "D",
            "R", "F", "T", "G", "Y", "H", "U", "J", "I", "K", "O", "L" };
    public static final int[] KEYBOARD_MNEM = { KeyEvent.VK_A, KeyEvent.VK_W,
            KeyEvent.VK_S, KeyEvent.VK_E, KeyEvent.VK_D, KeyEvent.VK_R,
            KeyEvent.VK_F, KeyEvent.VK_T, KeyEvent.VK_G, KeyEvent.VK_Y,
            KeyEvent.VK_H, KeyEvent.VK_U, KeyEvent.VK_J, KeyEvent.VK_I,
            KeyEvent.VK_K, KeyEvent.VK_O, KeyEvent.VK_L };

    private MusicManager musicManager;
    private int noteNum;
    private Color bgColor;
    private String noteStr;
    private String keyString;
    private boolean pressed;
    private boolean disabled = false;
    private boolean isRoot;

    /**
     * Create a new PianoKey
     * @param musicManager2
     * @param keyNum
     */
    public PianoKey(MusicManager musicManager2, int keyNum) {
        super();
        this.musicManager = musicManager2;

        keyString = PianoKey.KEYBOARD_LETTERS[keyNum];
        this.setText(keyString);

        /* Set up actions */
        pressed = false;
        Action keyPressed = new keyPressed(this);
        Action keyReleased = new keyReleased(this);
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                        KeyStroke.getKeyStroke("pressed "
                                        + PianoKey.KEYBOARD_LETTERS[keyNum]),
                        "pressed");
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                        KeyStroke.getKeyStroke("released "
                                        + PianoKey.KEYBOARD_LETTERS[keyNum]),
                        "released");
        this.getActionMap().put("pressed", keyPressed);
        this.getActionMap().put("released", keyReleased);
        this.addMouseListener(new keyMouseListener(this));
    }

    @Override
    public void paintComponent(Graphics g) {
        if (pressed && noteNum != 0) {
            setBackground(new Color(20, 100, 255));
        } else {
            setBackground(bgColor);
        }
        super.paintComponent(g);
        g.setColor(this.getForeground());
        int width = (int) g
                        .getFont()
                        .getStringBounds(
                                        noteStr
                                                        + Integer.toString(noteNum / 12),
                                        ((Graphics2D) g).getFontRenderContext())
                        .getWidth();
        if (noteNum != 0) {
            g.drawString(noteStr + Integer.toString(noteNum / 12 - 1),
                            this.getWidth() / 2 - width / 2, g.getFont()
                                            .getSize() + 20);
        }
        if (isRoot) {
            g.setColor(new Color(200, 20, 20));
            g.fillOval(this.getWidth() / 2 - 10, this.getHeight() * 8 / 10, 20,
                            20);
        }
    }

    /**
     * Set whether this piano key is a root note
     * 
     * @param isRoot
     */
    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    /**
     * Get the MIDI value this key will play
     * 
     * @return
     */
    public int getNote() {
        return noteNum;
    }

    /**
     * Set the note this key will play
     * 
     * @param noteNum
     *            a MIDI note number
     */
    public void setNote(int noteNum) {
        if (noteNum != 0) {
            this.noteNum = noteNum;
            this.noteStr = Note.getNoteName(noteNum);
            // Need to base offset on root note
            if (noteStr.contains("#")) {
                this.setForeground(Color.WHITE);
                this.setBackground(Color.BLACK);
                bgColor = Color.BLACK;
                this.setBorder(new BevelBorder(BevelBorder.RAISED,
                                Color.DARK_GRAY, Color.BLACK));
            } else {
                this.setForeground(Color.BLACK);
                this.setBackground(Color.WHITE);
                bgColor = Color.WHITE;
                this.setBorder(new LineBorder(Color.BLACK));
            }
        } else {
            this.noteNum = noteNum;
            this.noteStr = "";
            this.setForeground(Color.DARK_GRAY);
            this.setBackground(Color.DARK_GRAY);
            bgColor = Color.DARK_GRAY;
            this.setBorder(new LineBorder(Color.BLACK));
        }
    }

    /**
     * Set whether the system is enabled or disabled. If this is true, no input
     * will come from the virtual keyboard
     * 
     * @param disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Action handler for when a key is pressed
     */
    private void pressed() {
        if (disabled) {
            return;
        }
        if (musicManager.getClient().getOutput().isEmpty() && !musicManager.isNotified()) {
            JOptionPane.showMessageDialog(null,
                            "Warning: There is no output selected",
                            "No Output", JOptionPane.ERROR_MESSAGE);
            musicManager.setNotified(true);
            return;
        }
        if (!pressed) {
            pressed = true;
            System.out.println("pressed: " + this.noteStr + " " + this.noteNum);
            if (this.noteNum != 0) {
                musicManager.playNote(new Note(Note.NOTE_ON, noteNum,
                                Note.DEFAULT_VELOCITY, musicManager.getClient().getTime()));
                musicManager.getClient().addNote(new Note(Note.NOTE_ON, noteNum,
                                Note.DEFAULT_VELOCITY, musicManager.getClient().getTime()));
            }
        }
        repaint();
    }

    /**
     * Action handler for when a key is released
     */
    private void released() {
        if (disabled) {
            return;
        }
        pressed = false;
        System.out.println("released: " + this.noteStr + " " + this.noteNum);
        if (this.noteNum != 0) {
            musicManager.playNote(new Note(Note.NOTE_OFF, noteNum,
                            Note.DEFAULT_VELOCITY, musicManager.getClient().getTime()));
            musicManager.getClient().addNote(new Note(Note.NOTE_OFF, noteNum,
                            Note.DEFAULT_VELOCITY, musicManager.getClient().getTime()));
        }
        repaint();
    }

    private class keyPressed extends AbstractAction {

        private PianoKey key;

        keyPressed(PianoKey key) {
            super();
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            key.pressed();
        }

    }

    private class keyReleased extends AbstractAction {

        private PianoKey key;

        keyReleased(PianoKey key) {
            super();
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            key.released();
        }
    }

    /**
     * MouseAdapter so that keys can be triggered by mouse selection
     * 
     * @author Aidan
     *
     */
    private class keyMouseListener extends MouseAdapter {

        private PianoKey key;

        public keyMouseListener(PianoKey key) {
            this.key = key;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            key.pressed();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            key.released();
        }

        /**
         * If the mouse enters a key while the LMB is held down, play the key
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                key.pressed();
            }
        }
        
        /**
         * If the mouse exits a key while the LMB is held down, release the key
         */
        @Override
        public void mouseExited(MouseEvent e) {
            if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                key.released();
            }
        }
    }
}
