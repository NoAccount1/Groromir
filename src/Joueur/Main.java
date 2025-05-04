package Joueur;

import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

import java.util.logging.ErrorManager;

public class Main {
    static ErrorManager err = new ErrorManager();

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            new Joe(socket).start();
            System.out.println("Connexion réussie!");
//            Scanner scanner = new Scanner(System.in);
//            String message = "";
//            while (!message.equals("quit")) {
//                message = scanner.nextLine();
//            }
//            scanner.close();
//            socket.close();
        } catch (Exception e) {
            err.error("Error", e, ErrorManager.GENERIC_FAILURE);
        }
    }
}