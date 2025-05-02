package Joueur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Joe extends Thread {
    BufferedReader in;

    public Joe(Socket s) throws IOException {
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public void run() {
        try {
            while (true) {
                // Receive "connected" message
                System.out.println(in.readLine());

                // Receive dice number
                int nbr_dice = Integer.parseInt(in.readLine());
                System.out.println("Nbr_dice=" + nbr_dice);

                // Draw Dices
                int[] dices = new int[nbr_dice];
                do {
                    nbr_dice--;
                    dices[nbr_dice] = Integer.parseInt(in.readLine());
                    System.out.println(dices[nbr_dice]);
                } while (nbr_dice > 0);

                // Receive round start message
                System.out.println(in.readLine());

                // Receive round player
                System.out.println(in.readLine());
                String turn;
                do {
                    turn = in.readLine();
                    System.out.println(turn);
                } while (!turn.equals("Turn"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
