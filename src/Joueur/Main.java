package Joueur;

import java.io.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.0.1", 8080);
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            new Joe(s).start();
            System.out.println("Connexion réussie!");
            Scanner sc = new Scanner(System.in);
            String message = "";
            while (!message.equals("quit")) {
                message = sc.nextLine();
                out.println(message);
            }
            sc.close();
            s.close();
        } catch (Exception e) {
            // Traitement d'erreur
        }
    }
}