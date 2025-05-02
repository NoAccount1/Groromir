package Sauron;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.logging.ErrorManager;

public class Main {
    static ErrorManager err = new ErrorManager();

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
        } catch (Exception e) {
            err.error("Error in server", e, ErrorManager.GENERIC_FAILURE);
        }
    }
}