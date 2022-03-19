package Main;


import Home.Employe;
import Home.Login;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Login");
        frame.setContentPane(new Login().MainLogin);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

//        JPanel emp = new Employe().MainEmploye ;
//        JFrame frame = new JFrame("Employe");
//        frame.setContentPane(emp);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
    }
}
