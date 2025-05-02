package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Awt implements ActionListener {

    JFrame f;
    JButton addBut, subBut, mulBut, divBut, changeLFButton;
    JTextField t1, t2, t3;
    JLabel l;
    JComboBox<String> lookAndFeelComboBox;

    Awt() {

        f = new JFrame("Swing Calculator with Look and Feel");
        t1 = new JTextField(10);
        t2 = new JTextField(10);
        t3 = new JTextField(10);
        addBut = new JButton("Add");
        subBut = new JButton("Sub");
        mulBut = new JButton("Mul");
        divBut = new JButton("Div");

        changeLFButton = new JButton("Change Look and Feel");
        l = new JLabel("Result:");
        lookAndFeelComboBox = new JComboBox<>(new String[] {
                "Metal", "Nimbus", "Motif", "Windows", "System"
        });
    }

    public void awt1() {

        // Use GridBagLayout for better control
        f.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Add padding between components
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Enter Number 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        f.add(new JLabel("Enter Number 1:"), gbc);

        gbc.gridx = 1;
        f.add(t1, gbc);

        // Row 2: Enter Number 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        f.add(new JLabel("Enter Number 2:"), gbc);

        gbc.gridx = 1;
        f.add(t2, gbc);

        // Row 3: Result
        gbc.gridx = 0;
        gbc.gridy = 2;
        f.add(new JLabel("Result:"), gbc);

        gbc.gridx = 1;
        f.add(t3, gbc);

        // Row 4: Buttons (Add, Sub, Mul, Div)
        gbc.gridx = 0;
        gbc.gridy = 3;
        f.add(addBut, gbc);

        gbc.gridx = 1;
        f.add(subBut, gbc);

        gbc.gridx = 2;
        f.add(mulBut, gbc);

        gbc.gridx = 3;
        f.add(divBut, gbc);

        // Row 5: Look and Feel ComboBox and Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        f.add(lookAndFeelComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        f.add(changeLFButton, gbc);

        // Add action listeners
        addBut.addActionListener(this);
        subBut.addActionListener(this);
        mulBut.addActionListener(this);
        divBut.addActionListener(this);
        changeLFButton.addActionListener(this);

        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Change Look and Feel")) {
            String selectedLF = (String) lookAndFeelComboBox.getSelectedItem();
            try {
                switch (selectedLF) {
                    case "Metal":
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                        break;
                    case "Nimbus":
                        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                        break;
                    case "Motif":
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                        break;
                    case "Windows":
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                        break;
                    case "System":
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        break;
                }
                SwingUtilities.updateComponentTreeUI(f);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error setting Look and Feel: " + ex.getMessage());
            }
        }
        else {
            try {
                int a = Integer.parseInt(t1.getText().trim());
                int b = Integer.parseInt(t2.getText().trim());
                int result = 0;

                if (s.equals("Add")) {
                    result = a + b;
                } else if (s.equals("Sub")) {
                    result = a - b;
                } else if (s.equals("Mul")) {
                    result = a * b;
                } else if (s.equals("Div")) {
                    if (b == 0) {
                        t3.setText("Error: Division by 0");
                        return;
                    }
                    result = a / b;
                }

                t3.setText(String.valueOf(result));
            } catch (NumberFormatException ex) {
                t3.setText("Invalid Input");
            }
        }
    }

    public static void main(String args[]) {


        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Look and Feel not set: " + e.getMessage());
        }

        Awt a = new Awt();
        a.awt1();
    }
}