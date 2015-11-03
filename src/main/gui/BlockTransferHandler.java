package gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.TransferHandler;

import gui.BlockIcon;

/**
 * Transfer handler for BlockIcons along the IconList
 * 
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class BlockTransferHandler extends TransferHandler {

    /**
     * Only allow MOVE commands
     */
    @Override
    public int getSourceActions(JComponent comp) {
        return MOVE;
    }

    /**
     * If the element is a BlockIcon, return that, otherwise return null
     */
    @Override
    public Transferable createTransferable(JComponent comp) {
        if (comp instanceof BlockIcon) {
            return (BlockIcon) comp;
        }
        return null;
    }

    /**
     * Do nothing
     */
    @Override
    public void exportDone(JComponent comp, Transferable t, int action) {
    }

    /**
     * Can only import into a BlockIconPanel
     */
    @Override
    public boolean canImport(TransferSupport supp) {
        return (supp.getComponent() instanceof JPanel
                        && supp.getComponent().getName() != null && supp
                        .getComponent().getName().equals("BlockIconPanel"));
    }

    /**
     * Move a block it's prior location to the drop location
     */
    @Override
    public boolean importData(TransferSupport supp) {
        if (canImport(supp)) {
            IconList list = (IconList) supp.getComponent().getParent()
                            .getParent().getParent();
            JViewport panel = (JViewport) supp.getComponent().getParent();
            try {
                Transferable t = supp.getTransferable();
                int from = (int) t.getTransferData(DataFlavor.imageFlavor);
                list.move(from, (int) (Math.ceil(supp.getDropLocation()
                                .getDropPoint().getX()
                                * list.length()
                                / panel.getViewSize().getWidth() - 1)), false);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;

    }
}
