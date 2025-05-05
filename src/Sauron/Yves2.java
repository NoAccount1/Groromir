package Sauron;

import utils.LogFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Yves2 extends Thread {

    // log
    protected static Logger logger = Logger.getLogger(Yves.class.getName());
    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
    }
    ErrorManager err = new ErrorManager();

    int id;
    static AtomicInteger nbrId;
    BufferedReader in;
    PrintWriter out;
    static PrintWriter[] outs;

    public Yves2(int id, Socket client) {
        try {

            // génération de l'id du joueur et incrémentation du nombre de joueurs
            this.id = id;
            nbrId.incrementAndGet();

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

    }
}
