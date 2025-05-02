package Sauron;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ErrorManager;

public class Yves extends Thread {
    ErrorManager err = new ErrorManager();
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
            out.printf("Id= %d%n", id);

            // ajout du stream Thread → joueur à la table des stream Thread → joueurs pour que les autres Threads y aient accès
            outs[id] = out;

        } catch (Exception e) {
            err.error("Error in initialization", e, ErrorManager.GENERIC_FAILURE);
        }
    }

    public void run() {

        // ajout du nombre de dés au pool total de dés
        pool[id] = nbr_dice;

        // attend qu'il y ait au moins 3 joueurs avant de démarrer
        while (nbr_id.get() < 3) {
        }

        // début du jeu
        while (!end_game) {
            round();
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

        // ajout de la main à la liste des mains pour que les autres Thread y aient accès
        hands[id] = hand;

        // envoi de l'information des dés au joueur
        for (int i = 0; i < nbr_dice; i++) {
            out.printf("%d%n", hand[i]);
        }

        while (!end_round) {

            // sleep pour éviter que des joueurs soient bloqués dans le tour précédent
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                err.error("Error in sleep while waiting round", e, ErrorManager.GENERIC_FAILURE);
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
        out.printf("player_turn %d%n", joueur_actif);

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
                Thread.sleep(100);
            } catch (InterruptedException e) {
                err.error("Error in waiting round end", e, ErrorManager.GENERIC_FAILURE);
            }
        }
    }
}