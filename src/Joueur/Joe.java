package Joueur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Joe extends Thread implements ActionListener {

    JFrame frame;
    JButton button_submit, button_bluff, button_refresh;
    JTextField text_dice_number, text_dice_type, text_dice_amount;

    UIManager uiManager = new UIManager();
    BufferedReader in;

    public Joe(Socket s) throws IOException {
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        frame = new JFrame("Perudonline client");
        button_submit = new JButton("Submit");
        button_bluff = new JButton("Bluff");
        button_refresh = new JButton("Refresh");
        text_dice_number = new JTextField(10);
        text_dice_type = new JTextField(10);
        text_dice_amount = new JTextField(10);
    }

    private void buildUI() {
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row #1 : dice number
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("Nombre de dés:"), gbc);

        gbc.gridx = 1;
        frame.add(text_dice_number, gbc);

        // Row #2 : dice type
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("Type de dés:"), gbc);

        gbc.gridx = 1;
        frame.add(text_dice_type, gbc);

        // Row #3 : action buttons
        gbc.gridx = 0;
        gbc.gridy = 2;

        frame.add(button_submit, gbc);

        gbc.gridx = 1;
        frame.add(button_bluff, gbc);

        gbc.gridx = 2;
        frame.add(button_refresh, gbc);

        button_bluff.addActionListener(this);
        button_refresh.addActionListener(this);
        button_submit.addActionListener(this);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.printf("Look and Feel not set: %s%n", e.getMessage());
        }


        try {
            while (true) {
                System.out.println("# Receive id number");
                System.out.println(in.readLine());

                System.out.println("# Receive round start message");
                System.out.println(in.readLine());

                System.out.println("# Receive dice number");
                int nbr_dice = Integer.parseInt(in.readLine());
                System.out.printf("Nbr_dice=%d%n", nbr_dice);

                System.out.println("# Draw Dices");
                int[] dices = new int[nbr_dice];
                do {
                    nbr_dice--;
                    dices[nbr_dice] = Integer.parseInt(in.readLine());
                    System.out.println(dices[nbr_dice]);
                } while (nbr_dice > 0);

                System.out.println("# Get active player's turn");

                System.out.println("# Get whether or not it is his turn");
                String turn = in.readLine();
                while (in.readLine().equals("NoTurn")) ;


            }
        } catch (Exception e) {
            System.out.printf("Error during run: %s%n", e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("Submit")) {
            text_dice_amount.setEnabled(false);
        }
    }
}
