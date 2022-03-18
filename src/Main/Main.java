package Main;


import Home.Employe;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        JPanel emp = new Employe().MainEmploye ;
        JFrame frame = new JFrame("Employe");
        frame.setContentPane(emp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
