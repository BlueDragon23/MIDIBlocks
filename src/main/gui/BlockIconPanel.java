package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * A BlockIconPanel directly contains BlockIcons
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class BlockIconPanel extends JPanel implements Scrollable {
    
    private ArrayList<BlockIcon> blocks;

    /**
     * Create a new BlockIconPanel and set its list to be list
     * @param list
     */
    public BlockIconPanel(ArrayList<BlockIcon> list) {
        super();
        blocks = list;
    }
    
    /* Scrollable methods */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                    int orientation, int direction) {
        if (blocks.size() > 0) {
            return blocks.get(0).getWidth() * 3;
        } else {
            return 0;
        }
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                    int orientation, int direction) {
        if (blocks.size() > 0) {
            return blocks.get(0).getWidth();
        } else {
            return 0;
        }
    }

}
