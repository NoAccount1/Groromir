package Joueur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Joe extends Thread implements ActionListener {

    JFrame frame;
    JButton button_submit, button_bluff, button_refresh;
    JTextField text_dice_number, text_dice_type, text_dice_amount;

    UIManager uiManager = new UIManager();
    BufferedReader in;
    PrintWriter out;

    public Joe(Socket s) throws IOException {
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
        frame = new JFrame("Perudonline client");
        button_submit = new JButton("Submit");
        button_bluff = new JButton("Bluff");
        button_refresh = new JButton("Refresh");
        text_dice_number = new JTextField(10);
        text_dice_type = new JTextField(10);
        text_dice_amount = new JTextField(10);
    }

    //TODO: éviter que les joueurs puisse jouer leur tour en avance
    //TODO: UI

    String lecture;
    int nbr_dice;
    int[] dices = new int[5];
    String input = "";
    Scanner scanner = new Scanner(System.in);
    String poubelle;
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

    /*     Initialisation du thread :
                – reception de l'id ; str "osef"

            Début round :
                – reception de  info round début : str "Début round"
                – reception du nombre de dés : str "%d%n"
                    _stockage
                – reception des dés du joueurs : str "%d%n" x nombre de dés envoyés au-dessus
                    _stockage
                    _affichage

            Début tour :
                – reception de l'Id du joueur actif
                – reception de si c'est le tour du joueur : "Turn" / "NoTurn"
                – si NoTrun : attendre la fin du tour en écoutant
                – si Turn :
                    – envoie de l'input : str "Bluff" / "%d %d%n"
                    – reception de si l'input est valid : "valid input"/"invalid input"
                        si invalide recommencer
                        si valide écouter jusqu'à la fin du tour
     */

    private void draw(int nbr_dice) {
        try {
            for (int i = 0; i < nbr_dice; i++) {
                dices[i] = Integer.parseInt(in.readLine());
                System.out.println(dices[i]);
            }
        } catch (Exception e) {
            System.out.printf("Error in draw : %s%n", e);
        }
    }





    //
    private void recup_des() {
        try {
            nbr_dice = Integer.parseInt(in.readLine());
            System.out.printf("Vous avez %d dés %n", nbr_dice);
            draw(nbr_dice);
        } catch (Exception e) {
            System.out.printf("Failed to recup_dés : %s%n", e.getMessage());
        }
    }


    private void jouer_tour() {
        boolean valid = false;
        String respond;
        System.out.println("C'est votre tour !");
        try {
            while (!valid){
                while(!scanner.hasNextLine()) {
                    System.out.println("caca");
                    try {
                        sleep(500);
                    }catch (Exception e) {
                        System.out.printf("Error en dodo : %s%n", e);
                    }
                }
                input = scanner.nextLine();
                out.println(input);

                respond = in.readLine();

                if (respond.equals("valid input")) {
                    valid = true;
                } else {
                    if (input.equals("Bluff")) {
                        System.out.println("Impossible, aucun pari n'as été fait !");
                    } else {
                        System.out.println("L'action n'est pas reconnue.");
                    }
                }
            }
        } catch(Exception e) {
            System.out.printf("Failed to jouer tour : %s%n", e);
        }

    }


    public void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.printf("Look and Feel not set: %s%n", e.getMessage());
        }


        try {

            //
            System.out.println("# Receive id number");
            System.out.println(in.readLine());


            lecture = in.readLine();
            do {

                switch (lecture) {
                    case "Début round":
                        System.out.println("Début du round " + in.readLine());
                        recup_des();
                        break;
                    case "Turn":
                        jouer_tour();
                        break;
                    case "NoTurn":
                        System.out.println("Ce n'est pas votre tour");
                        break;
                    default:
                        System.out.println(lecture);
                        break;
                }
                lecture = in.readLine();
            } while(!lecture.equals("out"));

            lecture = in.readLine();
            while (!lecture.equals("endgame")) {
                switch (lecture) {
                    case "Début round":
                        System.out.println("Début du round " + in.readLine());
                        poubelle = in.readLine();
                        break;
                    case "NoTurn":
                        break;
                    default:
                        System.out.println(lecture);
                        break;
                }
                lecture = in.readLine();
            }

            System.out.println("La partie est terminée");
            System.out.println("type 'quit' to exit");

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

