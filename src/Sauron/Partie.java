package Sauron;

import java.util.HashMap;

public class Partie {

    int nbrRound;
    int nbrJoueur;
    String[] joueurs;
    HashMap<String, Integer> score = new HashMap<String, Integer>();
    HashMap<String, Boolean> alive = new HashMap<String, Boolean>();
    Round roundEnCours;


    public Partie(String[] joueurs) {
        this.nbrJoueur = joueurs.length;
        this.joueurs = joueurs;
        this.nbrRound = 0;
        for (String joueur : joueurs) {
            score.put(joueur, 5);
            alive.put(joueur, true);
        }
    }

    public void newRound() {
        roundEnCours = new Round();
    }


    /*
    public void run() {
        while (!isGameEnded) {
            roundEnCours = new Round();
        }
    }
    //*/

    private boolean isGameEnded() {
        int nbrAlive = 0;
        for (String i : joueurs) {
            if (alive.get(i)) {
                nbrAlive++;
            }
        }
        return (nbrAlive == 1);
    }
}
