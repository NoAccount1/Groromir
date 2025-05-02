package Sauron;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Yves extends Thread {

    int id;
    BufferedReader in;
    PrintWriter out;
    static PrintWriter[] outs = new PrintWriter[100];
    volatile static AtomicInteger nbr_id = new AtomicInteger(0); // éviter les races condition
    private int nbr_dice = 5;
    static int nbr_round = 1;
    static int joueur_deb_round = 0;
    static int joueur_actif = 0;
    static int[] last_action = {1, 2};
    static int[][] hands = new int[10][10];
    static boolean end_turn = false;
    static boolean end_round = false;
    static int[] pool = new int[10];

    public Yves(int id, Socket client) {
        try {
            // génération de l'id du joueur et incrémentation du nombre de joueurs
            this.id = id;
            nbr_id.incrementAndGet();

            // création des flux joueur thread et thread serveur
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            // envoi de son identifiant au joueur
            out.println("Id=" + id);

            // ajout du stream Thread -> joueur à la tables des stream Thread -> joueurs pour que les autres Threads y ait accès
            outs[id] = out;

        } catch (Exception e) {
            out.println("Error in Yves\nYves died from ligma");
            e.printStackTrace();
        }
    }

    public void run() {
        try {

            // TODO: à déplacer ..........................................................................................................................................
            out.println("" + nbr_dice);

            // ajout du nombre de dés au pool total de dés
            pool[id] = nbr_dice;

            // attend qu'il y ait au moins 3 joueurs avant de démarrer
            while (nbr_id.get() < 3) {
            }

            // début du jeu
            while (true) {

                round();
            }
        } catch (Exception e) {
            out.println("Error in run\nDies from cringe");
            e.printStackTrace();
        }
    }

    // fonction qui génère une main de 1 à 5 dés

    private int[] draw(int nb_dice) {
        // initialisation du random et de la main
        Random rand = new Random();
        int[] hand = new int[5];

        // génération de la main en fonction du nbr de dés nécéssaires
        for (int i = 0; i < nb_dice; i++) {
            hand[i] = rand.nextInt(5) + 1;
        }
        return hand;
    }


    // fonction qui gère le déroulement d'un round, un ensemble de tour
    private void round() {

        // génération de la main du joueur pour ce round
        int[] hand = draw(nbr_dice);

        // ajout de la main à la liste des mains pour que les autres Thread y ait accès
        hands[id] = hand;

        // envoi de l'information des dés au joueur
        for (int i = 0; i < nbr_dice; i++) {
            out.println("" + hand[i]);
        }

        // envoie au joueur que le round démarre
        out.println("round_start_ " + nbr_round);

        // TODO: début du round (ajout d'une condition d'arrèt) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        while (!end_round) {

            // sleep pour éviter que des joueurs soient bloqués dans le tour précédent
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            end_round = false;
            // lancement des tours
            tour();
        }
    }

    private void tour() {

        // reset des différents booléens
        end_turn = false;
        boolean Bluff = false;
        boolean action_valide = false;


        // information de à qui est le tour
        out.println("player_turn " + joueur_actif);

        if (joueur_actif == id) {

            // informe le joueur que c'est son tour
            out.println("Turn");

            // TODO: récupèration et traitement de l'input à mettre ailleur !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            while (!action_valide) {

                // récupération de l'input
                try {
                    String input = in.readLine();

                    // vérification si c'est un bluff
                    if (input.equals("Bluff")) {
                        Bluff = true;

                        // si ce n'est pas un bluff
                    } else {

                        // transforme l'input en information processable
                        int[] action = Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();

                        // vérifie si l'action est valide
                        if (last_action[0] < action[0] || (last_action[0] == action[0] && last_action[1] < action[1])) {
                            if (action[1] > 6) {
                                out.println("invalid input");

                                // si l'action est valide remplacer l'action précédente par celle-ci
                            } else {
                                action_valide = true;
                                System.out.println("Reponse valide (nice calk)");
                                last_action = action;
                            }

                            // si l'action n'est pas valide
                        } else {
                            out.println("invalid input");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error in round\nYves died (again)");
                    e.printStackTrace();
                }
            }

            // informe les joueurs du résultat du tour

            if (!Bluff) {

                // si ce n'est pas un bluff, informe les joueurs du pari qui a été fait
                for (int i = 0; i < nbr_id.get(); i++) {
                    if (i == id) {
                        outs[i].println("Vous avez parié qu'il y avait " + last_action[0] + " " + last_action[1]);
                    } else {
                        outs[i].println("Le joueur " + id + " a parié qu'il y avait " + last_action[0] + " " + last_action[1]);
                    }
                }

                // passage du tour au joueur précédent
                joueur_actif = (joueur_actif + 1) % 3;
                end_turn = true;

                // si c'est un bluff
            } else {

                // informe les joueurs que le joueur actif à dit bluff
                try {
                    for (int i = 0; i < nbr_id.get(); i++) {
                        outs[i].println("Bluff !");
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // compte le nombre d'itérations totales dans le pool
                int nombre_iteration = 0;
                for (int i = 0; i < nbr_id.get(); i++) {
                    for (int j = 0; j < hands[i].length; j++) {
                        if (hands[i][j] == last_action[1]) {
                            nombre_iteration++;
                        }
                    }
                }

                // informe les joueurs du résultat
                for (int i = 0; i < nbr_id.get(); i++) {
                    outs[i].println("Il y avait " + nombre_iteration + " " + last_action[1]);
                }
                try {
                    Thread.sleep(1000);

                    // vérifie si le bluff est validé au non
                    if (nombre_iteration < last_action[0]) {

                        // si le bluff est invalidé enlève un dé au joueur actif
                        for (int i = 0; i < nbr_id.get(); i++) {
                            if (i == id) {
                                out.println("Vous avez perdu 1 Dé !");
                                pool[id]--;
                            } else {
                                outs[i].println("Le joueur " + id + " a perdu 1 Dé !");
                            }
                        }

                        // si le bluff est validé, enlève un dé au joueur précédent
                    } else {
                        for (int i = 0; i < nbr_id.get(); i++) {
                            if (i == (id + 2) % 3) {
                                out.println("Vous avez perdu 1 Dé !");
                                pool[(id + 2) % 3]--;
                            } else {
                                outs[i].println("Le joueur " + (id + 2) % 3 + " a perdu 1 Dé !");
                            }
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // si le joueur n'est pas actif, stand by and read
        } else {
            out.println("NoTurn");
        }
        while (!end_turn) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}