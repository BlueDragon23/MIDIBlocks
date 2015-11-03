package gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import blocks.AbstractBlock;
import blocks.ArpeggiatorBlock;
import blocks.GateBlock;
import blocks.PitchShiftBlock;

/**
 * Simple parameterised dialog box to enter parameters for blocks
 * 
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class ParamsDialog extends JDialog {

    private String[] paramNames;
    private JComponent[] inputs;
    private AbstractBlock block;
    private boolean isOk = false;

    /**
     * Create a new ParamsDialog window
     * 
     * @param block
     */
    public ParamsDialog(AbstractBlock block) {
        this.block = block;
        paramNames = block.getParamNames();
        inputs = new JComponent[paramNames.length];
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Set block parameters");
        setAlwaysOnTop(true);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setBounds(350, 350, 400, 200);
        Container main = this.getContentPane();
        main.setLayout(new GridBagLayout());
        JPanel args = createArgsPanel(main);

        addInputFields(block, args);
        createChoiceButtons(main);
        this.setVisible(true);
    }

    /**
     * Add the input fields for the dialog
     * 
     * @param block
     *            The AbstractBlock to base the inputs on
     * @param args
     *            The panel to add the fields to
     */
    private void addInputFields(AbstractBlock block, JPanel args) {
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0 / paramNames.length;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        if (block instanceof PitchShiftBlock) {
            createInputField(args, gbc, paramNames[0], block.getParams()[0]);
        } else if (block instanceof ArpeggiatorBlock) {
            createComboBox(args, gbc, paramNames[0], block.getParams()[0]);
        } else if (block instanceof GateBlock) {
            createInputField(args, gbc, paramNames[0], block.getParams()[0]);
            gbc.gridx = 1;
            createComboBox(args, gbc, paramNames[1], block.getParams()[1]);
        }
    }

    /**
     * Create ok and cancel buttons to exit the dialog with an affirmative or
     * negative response
     * 
     * @param main
     *            The panel to add the buttons to
     */
    private void createChoiceButtons(Container main) {
        JButton ok = new JButton();
        ok.setToolTipText("Accept the changes");
        ok.setText("Ok");
        ok.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                ParamsDialog.this.setVisible(false);
                ParamsDialog.this.isOk = true;
                ParamsDialog.this.dispatchEvent(new WindowEvent(
                                ParamsDialog.this, WindowEvent.WINDOW_CLOSING));
            }

        });
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.gridx = 0;
        buttonGbc.gridy = 1;
        buttonGbc.weightx = 0.5;
        buttonGbc.weighty = 0.5;
        main.add(ok, buttonGbc);
        getRootPane().setDefaultButton(ok);

        JButton cancel = new JButton();
        cancel.addActionListener(new exitActionListener());
        cancel.setToolTipText("Cancel changes");
        cancel.setText("Cancel");
        buttonGbc.gridx = 1;
        main.add(cancel, buttonGbc);
        getRootPane().registerKeyboardAction(new exitActionListener(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Create a panel for the parameters to be placed in
     * 
     * @param root
     *            The pane to contain this panel
     * @return the created panel
     */
    private JPanel createArgsPanel(Container root) {
        JPanel args = new JPanel();
        GridBagConstraints argsCons = new GridBagConstraints();
        argsCons.gridx = 0;
        argsCons.gridy = 0;
        argsCons.gridwidth = 2;
        argsCons.fill = GridBagConstraints.BOTH;
        argsCons.anchor = GridBagConstraints.NORTH;
        root.add(args, argsCons);
        return args;
    }

    /**
     * Create a combo box for selection of parameters
     * 
     * @param root
     *            the pane to add this to
     * @param gbc
     *            constraints for the combo box
     * @param arg
     *            The label for the combo box
     */
    private void createComboBox(Container root, GridBagConstraints gbc,
                    String arg, String currentParam) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(arg + ": ");
        panel.add(label);
        JComboBox<String> input = new JComboBox<>();
        for (String value : block.getParamValues()) {
            input.addItem(value);
        }
        input.setSelectedItem(currentParam);
        inputs[gbc.gridx] = input;
        panel.add(input);
        root.add(panel, gbc);
    }

    /**
     * Determine how the dialog exited
     * 
     * @return true for ok, false if the window was closed or cancel was pressed
     */
    public boolean getExitStatus() {
        return isOk;
    }

    /**
     * Get the values that have been selected in the Dialog, in the order they
     * are supplied by the block
     * 
     * @return The selected values for parameters
     */
    public String[] getParams() {
        if (!isOk) {
            return new String[] {};
        }
        String[] params = new String[inputs.length];
        int i = 0;
        if (block instanceof PitchShiftBlock) {
            for (JComponent input : inputs) {
                String inputText = ((JTextField) input).getText();
                try {
                    Integer.parseInt(inputText);
                    params[i++] = inputText;
                } catch (NumberFormatException e) {
                    isOk = false;
                    JOptionPane.showMessageDialog(null, "Invalid number "
                                    + inputText,
                                    "Please enter a valid pitch shift",
                                    JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (block instanceof ArpeggiatorBlock) {
            params[0] = (String) ((JComboBox<?>) inputs[0]).getSelectedItem();
        } else if (block instanceof GateBlock) {
            String inputText = ((JTextField) inputs[0]).getText();
            try {
                Float.parseFloat(inputText);
                params[0] = inputText;
            } catch (NumberFormatException e) {
                isOk = false;
                JOptionPane.showMessageDialog(null, "Invalid number "
                                + inputText,
                                "Please enter a valid notes per tick",
                                JOptionPane.ERROR_MESSAGE);
            }
            params[1] = (String) ((JComboBox<?>) inputs[1]).getSelectedItem();
        }
        return params;
    }

    /**
     * Create a new text field to use as input
     * 
     * @param root
     *            The pane to add this to
     * @param gbc
     *            Constraints for the panel
     * @param arg
     *            The name of the parameter
     * @param currentParam
     *            The current value of this parameter
     */
    private void createInputField(Container root, GridBagConstraints gbc,
                    String arg, String currentParam) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(arg + ": ");
        panel.add(label);
        JTextField input = new JTextField();
        input.setText(currentParam);
        inputs[gbc.gridx] = input;
        panel.add(input);
        root.add(panel, gbc);
    }

    /**
     * Private class to destroy the window on exit
     * 
     * @author Aidan
     *
     */
    private class exitActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            ParamsDialog.this.setVisible(false);
            ParamsDialog.this.isOk = false;
            ParamsDialog.this.dispatchEvent(new WindowEvent(ParamsDialog.this,
                            WindowEvent.WINDOW_CLOSING));
        }
    }

}
