package Joueur;

import java.net.*;

import java.util.logging.ErrorManager;

public class Main {
    static ErrorManager err = new ErrorManager();

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            new Joe(socket).start();
            System.out.println("Connexion réussie!");
        } catch (Exception e) {
            err.error("Error", e, ErrorManager.GENERIC_FAILURE);
        }
    }
}