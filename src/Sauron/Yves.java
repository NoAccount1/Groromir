package Sauron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class Yves extends Thread {

    int id;
    BufferedReader in;
    PrintWriter out;
    static PrintWriter[] outs = new PrintWriter[100];
    volatile static int nbr_id = 0;
    private int nbr_dice = 5;
    static int nbr_round = 1;
    static int joueur_deb_round = 0;
    static int joueur_actif = 0;
    static int[] last_action = {1, 2};
    static int[][] hands = new int[10][10];
    volatile static boolean end_turn = false;
    static int[] pool = new int[10];

    public Yves(int id, Socket client) {
        try {
            this.id = id;
            nbr_id++;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            out.println("Id=" + id);
            outs[id] = out;
        } catch (Exception e) {
            out.println("Error in Yves\nYves died from ligma");
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            out.println("" + nbr_dice);
            pool[id] = nbr_dice;
            while (true) {
                while (nbr_id < 3) {
//                    Thread.sleep(100);
                }
                round();
            }
        } catch (Exception e) {
            out.println("Error in run\nDies from cringe");
            e.printStackTrace();
        }
    }

    private int[] draw(int nb_dice) {
        Random rand = new Random();
        int[] hand = {0, 0, 0, 0, 0};
        for (int i = 0; i < nb_dice; i++) {
            hand[i] = rand.nextInt(5) + 1;
        }
        return hand;
    }

    private void round() {
        int[] hand = draw(nbr_dice);
        hands[id] = hand;
        for (int i = 0; i < nbr_dice; i++) {
            out.println("" + hand[i]);
        }
        out.println("round_start_ " + nbr_round);
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            tour();
        }
    }



    private void tour() {
        end_turn = false;
        boolean Bluff = false;
        out.println("player_turn " + joueur_actif);

        if (joueur_actif == id) {
            out.println("Turn");

            boolean action_valide = false;

            while (!action_valide) {
                try {
                    String input = in.readLine();
                    if (input.equals("Bluff")) {
                        Bluff = true;
                    } else {
                        int[] action = Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
                        if (last_action[0] < action[0] || (last_action[0] == action[0] && last_action[1] < action[1])) {
                            if (action[1] > 6) {
                                out.println("invalid input");
                            } else {
                                action_valide = true;
                                System.out.println("Reponse valide (nice calk)");
                                last_action = action;
                            }

                        } else {
                            out.println("invalid input");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error in round\nYves died (again)");
                    e.printStackTrace();
                }
            }
            if (!Bluff) {
                for (int i = 0; i < nbr_id; i++) {
                    if (i == id) {
                        outs[i].println("Vous avez parié qu'il y avait " + last_action[0] + " " + last_action[1]);
                    } else {
                        outs[i].println("Le joueur " + id + " a parié qu'il y avait " + last_action[0] + " " + last_action[1]);
                    }
                }
                joueur_actif = (joueur_actif + 1) % 3;
                end_turn = true;
            } else {
                try {
                    for (int i = 0; i < nbr_id; i++) {
                        outs[i].println("Bluff !");
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                int nombre_iteration = 0;
                for (int i = 0; i < nbr_id; i++) {
                    for (int j = 0; j < hands[i].length; j++) {
                        if (hands[i][j] == last_action[1]) {
                            nombre_iteration++;
                        }
                    }
                }

                    for (int i = 0; i < nbr_id; i++) {
                        outs[i].println("Il y avait " + nombre_iteration + " " + last_action[1]);
                    }
                try {
                    Thread.sleep(1000);
                    if (nombre_iteration < last_action[0]) {
                        for (int i = 0; i < nbr_id; i++) {
                            if (i == id) {
                                out.println("Vous avez perdu 1 Dé !");
                                pool[id]--;
                            } else {
                                outs[i].println("Le joueur " + id + " a perdu 1 Dé !");
                            }
                        }
                    } else {
                        for (int i = 0; i < nbr_id; i++) {
                            if (i == (id+2)%3) {
                                out.println("Vous avez perdu 1 Dé !");
                                pool[(id+2)%3]--;
                            } else {
                                outs[i].println("Le joueur " + (id+2)%3 + " a perdu 1 Dé !");
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {out.println("NoTurn");}
        while (!end_turn) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}