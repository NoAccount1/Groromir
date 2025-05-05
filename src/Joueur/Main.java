package Joueur;

import Sauron.Yves;
import utils.LogFormatter;

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

import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main implements ActionListener {
    static ErrorManager err = new ErrorManager();
    protected static Logger logger = Logger.getLogger(Yves.class.getName());

    static JFrame frame;
    static JButton button_submit, button_bluff, button_refresh;
    static JTextField text_dice_number, text_dice_type, text_dice_amount;

    static BufferedReader in;
    static PrintWriter out;

    //    int nbr_dice;
    static String lecture;
    static Hand dices;
    static String input;
    static Scanner scanner = new Scanner(System.in);
    static String poubelle;

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
    }

    /*/
        Initialisation du thread :
                – reception de l'id ; str "osef"

            Début round :
                – reception de l'info round début : str "Début round"
                – reception du nombre de dés : str "%d%n"
                    _stockage
                – reception des dés du joueur : str "%d%n" x nombre de dés envoyés au-dessus
                    _stockage
                    _affichage

            Début tour :
                – reception de l'Id du joueur actif
                – reception du tour du joueur : "Turn" / "NoTurn"
                – si NoTrun : attendre la fin du tour en écoutant
                – si Turn :
                    – envoie de l'input : str "Bluff" / "%d %d%n"
                    – reception de si l'input est valid : "valid input"/"invalid input"
                        si invalide recommencer
                        si valide, écouter jusqu'à la fin du tour
     //*/

    //TODO: éviter que les joueurs puisse jouer leur tour en avance
    //TODO: UI

    public static void main(String[] args) {

        // GUI Related components
        frame = new JFrame("Perudonline client");
        button_submit = new JButton("Submit");
        button_bluff = new JButton("Bluff");
        button_refresh = new JButton("Refresh");
        text_dice_number = new JTextField(10);
        text_dice_type = new JTextField(10);
        text_dice_amount = new JTextField(10);


        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            logger.info("Connexion réussie!");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            logger.severe("Connexion error : %s%n".formatted(e.getMessage()));
        } catch (Exception e) {
            err.error("Error", e, ErrorManager.GENERIC_FAILURE);
        }

        dices = new Hand(in);

        try {
            logger.info("# Receive id number");
            logger.info(in.readLine());

            lecture = in.readLine();
            do {
                switch (lecture) {
                    case "Début round":
                        logger.info("Début du round " + in.readLine());
                        logger.fine("Getting number of dice");
                        int diceToDraw = Integer.parseInt(in.readLine());
                        dices.draw(diceToDraw);
                        break;
                    case "Turn":
                        jouer_tour();
                        break;
                    case "NoTurn":
                        logger.warning("Ce n'est pas votre tour");
                        break;
                    default:
                        System.out.println(lecture);
                        break;
                }
                lecture = in.readLine();
            } while (!lecture.equals("out"));

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
            logger.severe("Error during run: %s%n".formatted(e.getMessage()));
        }

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

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while setting look and feel", e.getMessage());
        }

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

//    /**
//     * Draw a given number of dice
//     *
//     * @param nbr_dice number of dice drawn
//     */
//    private void parseHand(int nbr_dice) {
//        try {
//            for (int i = 0; i < nbr_dice; i++) {
//                dices[i] = Integer.parseInt(in.readLine());
//                System.out.println(dices[i]);
//            }
//        } catch (Exception e) {
//            System.out.printf("Error in draw : %s%n", e);
//        }
//    }

//    /**
//     * Draw
//     */
//    private void drawDices() {
//        try {
//            nbr_dice = Integer.parseInt(in.readLine());
//            logger.info("Vous avez %d dés %n".formatted(nbr_dice));
//            parseHand(nbr_dice);
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, "Failed to recup_dés : %s%n", e.getMessage());
//        }
//    }

    private static void jouer_tour() {
        String serverResponse;
        logger.info("C'est votre tour !");
        out.flush();
        try {
            while (true) {
                input = scanner.nextLine();
                out.println(input);

                serverResponse = in.readLine();

                if (serverResponse.equals("valid input"))
                    break;

                if (input.equals("Bluff")) {
                    logger.warning("Impossible, aucun pari n'as été fait !");
                    break;
                }

                logger.warning("L'action n'est pas reconnue.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to jouer tour : %s%n", e);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action) {
            case "Submit":

                break;
            case "Bluff":
                break;
            default:
                break;
        }
    }
}