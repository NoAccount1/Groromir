package Sauron;

import utils.LogFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;

public class Yves extends Thread {
    ErrorManager err = new ErrorManager();
    protected static Logger logger = Logger.getLogger(Yves.class.getName());

    //log
    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
    }


    int id;
    BufferedReader in;
    PrintWriter out;
    static PrintWriter[] outs = new PrintWriter[100];
    volatile static AtomicInteger nbr_id = new AtomicInteger(0); // éviter les races condition
    private int nbr_dice = 5;
    static int nbr_round = 1;
    static int joueur_deb_round = 0;
    static int joueur_actif = 0;
    static int[] last_action = {0, 0, 0};
    int[] empty_action = {0, 0, 0};
    static int[][] hands = new int[10][10];
    static boolean end_turn = false;
    static boolean end_round = false;
    static boolean end_game = false;
    static int[] pool = new int[10];
    boolean eliminate = false;

    //TODO: remplacer les sleep
    //TODO: modifier l'init pour avoir plus que 3 joueurs
    //TODO: ajouter les paco
    //TODO: améliorer le log
    //TODO: informer les autres joueurs quand un joueur est éliminé


    /*     Initialisation du thread :
                – envoie de l'id ; str "osef"

            Début round :
                – envoie info round début : str "osef"
                – envoie du nombre de dés : str "%d%n"
                – envoie des dés aux joueurs : str "%d%n" x nombre de dés envoyés au-dessus

            Début tour :
                – envoie de l'Id du joueur actif
                – envoie du tour du joueur : "Turn" / "NoTurn"
                – si NoTrun : attendre la din du tour en écoutant
                – si Turn :
                    – récupère un input : strSS "Bluff" / "%d %d%n"
                    – envoie si l'input est valid : "valid input"/"invalid input"
                        si invalide recommencer
                        si valide, écouter jusqu'à la fin du tour
     */

    //initialisation
    public Yves(int id, Socket client) {
        try {
            // génération de l'id du joueur et incrémentation du nombre de joueurs
            this.id = id;
            nbr_id.incrementAndGet();

            // création des flux joueur -> thread et thread -> joueur
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            // envoi de son identifiant au joueur
            out.printf("Id= %d%n", id);

            // ajout du stream Thread → joueur à la table des stream Thread → joueurs pour que les autres Threads y aient accès
            outs[id] = out;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
            err.error("Error in initialization", e, ErrorManager.GENERIC_FAILURE);
        }
    }

    public void run() {

        // ajout du nombre de dés au pool total de dés
        pool[id] = nbr_dice;

        // attend qu'il y ait au moins 3 joueurs avant de démarrer
        while (nbr_id.get() < 3) {
//            System.out.println("waiting for players");
        }

        // début du jeu
        while (!end_game) {

            //idem que pour avant le nouveau tour (l.189)
            try {
                sleep(100);
            } catch (InterruptedException e) {
                err.error("Error in sleep while waiting round", e, ErrorManager.GENERIC_FAILURE);
            }

            round();
        }

        logger.info("Fin de la partie");
        out.printf("endgame%n");

    }

    // fonction qui génère une main de 1 à 5 dés
    private int[] draw(int nb_dice) {
        // initialisation du random et de la main
        Random rand = new Random();
        int[] hand = new int[5];

        // génération de la main en fonction du nbr de dés nécéssaires
        for (int i = 0; i < nb_dice; i++) {
            hand[i] = rand.nextInt(6) + 1;
        }
        return hand;
    }

    //vérifie s'il faut relancer un round
    private boolean is_game_ended() {

        int nbr_player_not_dead = 0;
        for (int j : pool) {
            nbr_player_not_dead = j == 0 ? nbr_player_not_dead : nbr_player_not_dead + 1;
        }

        return (nbr_player_not_dead == 1);
    }

    // fonction qui gère le déroulement d'un round, un ensemble de tour
    private void round() {

        //actualise le nombre de dés et réinitialisation des actions
        nbr_dice = pool[id];
        last_action[0] = 0;
        last_action[1] = 0;
        last_action[2] = 0;

        // elimination du joueur si nécessaire






        // envoie au joueur que le round démarre
        logger.info("Round %d démarre%n".formatted(nbr_round));
        out.println("Début round");
        out.printf("%d%n", nbr_round);

        // envoie du nombre de dés au joueur
        logger.info("%d dés envoyés%n".formatted(nbr_dice));
        out.printf("%d%n", nbr_dice);

        // génération de la main du joueur pour ce round
        int[] hand = draw(nbr_dice);

        // ajout de la main à la liste des mains pour que les autres Thread y aient accès
        hands[id] = hand;

        // envoi de l'information des dés au joueur
        for (int i = 0; i < nbr_dice; i++) {
            out.printf("%d%n", hand[i]);
        }
        end_round = false;
        while (!end_round) {

            // sleep pour éviter que des joueurs soient bloqués dans le tour précédent
            try {
                sleep(100);
            } catch (InterruptedException e) {
                err.error("Error in sleep while waiting round", e, ErrorManager.GENERIC_FAILURE);
            }

            // lancement des tours
            tour();
        }

        // actualise le nombre de dés avant de vérifier les conditions de victoire :
        nbr_dice = pool[id];

        if ((nbr_dice == 0) && !eliminate) {
            eliminate = true;
            out.println("out");
            out.println("vous avez perdu");

        }

        end_game = is_game_ended();
        if (end_game && nbr_dice != 0) {
            out.println("out");
            out.println("Vous avez gagné !!");
            eliminate = true;

        }







    }


    // fonction qui prend l'input et le transforme en info processable sous forme de tables de 3 int, [0] : bluff ou pas ; [1] : nombre d'itérations ; [2] : chiffre du dé ;
    private void traitement_input() {

        // génération de l'action
        int[] action = new int[3];

        boolean action_valide = false;

        while (!action_valide) {

            // récupération de l'input
            try {
                String input = in.readLine();

                // vérification si c'est un bluff
                if (input.equals("Bluff")) {

                    //interdit au joueur de pouvoir dire bluff au premier tour
                    if (Arrays.equals(last_action, empty_action)) {
                        System.out.println("invalid input");
                        out.println("invalid input");

                    //traite le bluff
                    } else {
                    action[0] = 1;
                    action[1] = last_action[1];
                    action[2] = last_action[2];
                    action_valide = true;
                    System.out.println("valid input");
                    out.println("valid input");
                    }

                    // si ce n'est pas un bluff
                } else {

                    String[] action_tmp_str = input.split(" ");
                    if (is_formated(action_tmp_str)) {
                        int[] action_tmp = to_int_arr(action_tmp_str);

                        // vérifie si l'action est valide
                        if ((last_action[1] < action_tmp[0] || (last_action[1] == action_tmp[0] && last_action[2] < action_tmp[1])) && action_tmp[1] <= 6) {

                            // si l'action est valide l'intégrer au format
                            action_valide = true;
                            System.out.println("valid input");
                            out.println("valid input");
                            action[1] = action_tmp[0];
                            action[2] = action_tmp[1];

                        } else {
                            // si l'action n'est pas valide recommencer le traitement
                            out.println("invalid input");
                        }
                    }  else {
                        out.println("invalid input");
                    }
                }
            } catch (Exception e) {
                err.error("Error in input section", e, ErrorManager.GENERIC_FAILURE);
            }
        }
        last_action = action;
    }

    //prends en paramètre une action formatée sous la forme de la fonction au-dessus et fait ce que l'action est censé faire
    private void traitement_action(int[] action) {

        if (!(action[0] == 1)) {

            print_pari(action[1], action[2]);

            // passage du tour au joueur suivant
            joueur_actif = (joueur_actif + 1) % 3;
            end_turn = true;

        } else {

            // informe les joueurs que l'action est un bluff
            for (int i = 0; i < nbr_id.get(); i++) {
                outs[i].printf("Bluff !%n");
            }

            // informe les joueurs du nombre réel d'itératon
            for (int i = 0; i < nbr_id.get(); i++) {
                outs[i].printf("Il y a %d %d en tout%n", compte_iteration(action[2]), action[2]);
            }

            solve_bluff(action);
            joueur_deb_round = joueur_actif;
            end_turn = true;
            end_round = true;
        }
    }

    // fonction qui compte le nombre d'itérations d'un chiffre dans la pool totale
    private int compte_iteration(int chiffre) {

        // compte le nombre d'itérations d'un chiffre totales dans la pool
        int nombre_iteration = 0;
        for (int i = 0; i < nbr_id.get(); i++) {
            for (int j = 0; j < hands[i].length; j++) {
                if (hands[i][j] == chiffre) {
                    nombre_iteration++;
                }
            }
        }

        return nombre_iteration;
    }


    // informe tous les joueurs du pari effectué
    private void print_pari(int nbr_ite, int chiffre) {

        for (int i = 0; i < nbr_id.get(); i++) {
            outs[i].printf(
                    "%s parié qu'il y avait %d %d%n",
                    i == id ? "Vous avez" : "Le joueur %d a".formatted(id),
                    nbr_ite,
                    chiffre
            );
        }
    }

    // verifie que le bluff est valide
    private boolean check_bluff(int nbr_ite, int nbr_vise) {
        return nbr_ite < nbr_vise;
    }

    // effectue les actions inhérentesau bluff
    private void solve_bluff(int[] action) {

        // si le bluff est valide
        if (check_bluff(compte_iteration(action[2]), action[1])) {

            // préviens les joueurs que le joueur a perdu un dé
            for (int i = 0; i < nbr_id.get(); i++) {
                outs[i].printf(
                        "%s perdu 1 dé !%n",
                        i == (id + 2) % 3 ? "Vous avez" : "Le joueur %d a".formatted((id + 2) % 3)
                );
            }

            // enlève le dé de la pool
            pool[(id + 2) % 3] = pool[(id + 2) % 3] - 1;

            // si le bluff n'est pas valide
        } else {

            // préviens les joueurs que le joueur a perdu un dé
            for (int i = 0; i < nbr_id.get(); i++) {
                outs[i].printf(
                        "%s perdu 1 dé !%n",
                        i == id ? "Vous avez" : "Le joueur %d a".formatted(id)
                );
            }

            // enlève le dé de la pool
            pool[id] = pool[id] - 1;
        }
    }

    //joue le tour
    private void tour() {

        // reset des différents booléens
        end_turn = false;

        //traitement des joueurs morts
        while (pool[joueur_actif] == 0) {
            joueur_actif = (joueur_actif + 1) % 3;
        }

        // information de à qui est le tour
        out.printf("C'est au tour du joueur %d%n", joueur_actif);

        // si le joueur est actif
        if (joueur_actif == id) {
            // informe le joueur que c'est son tour
            out.println("Turn");

            traitement_input();
            traitement_action(last_action);

        } else {
            // si le joueur n'est pas actif, stand by and read
            out.println("NoTurn");
        }
        while (!end_turn) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                err.error("Error in waiting round end", e, ErrorManager.GENERIC_FAILURE);
            }
        }
    }

    //vérifie si l'input a le bon format
    private boolean is_formated(String[] input) {
        String[] nombre = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        boolean thisone = false;

        for (String i : input) {
            String[] tmp = i.split("");
            for (String j : tmp) {
                for (String k : nombre) {
                    if (j.equals(k)) {
                        thisone = true;
                        break;
                    }
                }
                if (!thisone) {
                    return false;
                }
            }
        }
        return true;
    }

    //transforme un input en input proccessable
    private int[] to_int_arr(String[] arr_str) {
        int[] int_arr = new int[arr_str.length];
        for (int i = 0; i < arr_str.length; i++) {
            int_arr[i] = Integer.parseInt(arr_str[i]);
        }
        return int_arr;
    }
}
