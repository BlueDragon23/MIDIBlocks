package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Simple panel to flank the processing line. Needs to be a class to override
 * paintComponent
 * 
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class ArrowPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawLine(0, this.getHeight() / 2, this.getWidth(),
                        this.getHeight() / 2);
    }
}
