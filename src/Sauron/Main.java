package Sauron;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket ecoute = new ServerSocket(8080);
            System.out.println("Server listening on port 8080");
            int id = 0;
            while (true) {
                Socket client = ecoute.accept();
                new Yves(id, client).start();
                id++;
            }
        } catch (IOException e) {
        }
    }
}