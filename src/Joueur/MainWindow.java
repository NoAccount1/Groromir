package Joueur;

import javax.swing.*;
import javax.swing.UIManager;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame implements ActionListener {
    private JButton button;
    private JButton button2;

    public MainWindow() {
        super();

        build();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTitle("Perduonline client");
                setSize(400, 600);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setContentPane(contentPane());
            }
        });
    }


    private void build() {

    }

    private JPanel contentPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Perduonline");
        panel.add(label);

        button = new JButton("Cliquez ici !");
        button.addActionListener(this);
        panel.add(button);

        button2 = new JButton("Ou là !");
        button2.addActionListener(this);
        panel.add(button2);

        return panel;
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == button) {
            System.out.println("Vous avez cliqué ici.");
        } else if (source == button2) {
            System.out.println("Vous avez cliqué là.");
        }
    }
}
