package Joueur;

import Sauron.Yves;

import javax.swing.*;
import javax.swing.UIManager;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.ErrorManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindow extends JFrame implements ActionListener {
    static ErrorManager err = new ErrorManager();
    protected static Logger logger = Logger.getLogger(Yves.class.getName());

    JFrame frame;
    JButton button_submit, button_bluff, button_refresh;
    JTextField text_dice_number, text_dice_type, text_dice_amount;

    public MainWindow() {
        super();

        build();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTitle("Perduonline client");
                setSize(400, 600);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setContentPane(contentPane());

                pack();
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
            }
        });
    }

    private void build() {

    }

    private JPanel contentPane() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row #1 : dice number
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre de dés:"), gbc);

        gbc.gridx = 1;
        panel.add(text_dice_number, gbc);

        // Row #2 : dice type
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Type de dés:"), gbc);

        gbc.gridx = 1;
        panel.add(text_dice_type, gbc);

        // Row #3 : action buttons
        gbc.gridx = 0;
        gbc.gridy = 2;

        panel.add(button_submit, gbc);

        gbc.gridx = 1;
        panel.add(button_bluff, gbc);

        gbc.gridx = 2;
        panel.add(button_refresh, gbc);

        button_bluff.addActionListener(this);
        button_refresh.addActionListener(this);
        button_submit.addActionListener(this);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while setting look and feel", e.getMessage());
        }

        return panel;
    }

    public void updateTitleAppendix(String appendix) {
        frame.setTitle("Perduonline client %s".formatted(appendix));
    }

    public void refreshUi() {
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == button_submit) {
            System.out.println("Vous avez cliqué ici.");
        } else if (source == button_bluff) {
            System.out.println("Vous avez cliqué là.");
        }
    }
}
