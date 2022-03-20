package Home;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Inter {
    JPanel MainInter;
    private JButton BtnEmp;
    private JButton BtnPiece;
    private JButton BtnCli;
    private JButton BtnFour;
    private JButton Btnentree;
    private JButton Btnsortie;
    private JButton decBtn;
    JFrame frameInter;



    public Inter(String NomCurrentUser) {

        System.out.println("NEW INTERFACE");
        frameInter = new JFrame("Inter");
        frameInter.setContentPane(MainInter);
        frameInter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameInter.pack();
        frameInter.setLocationRelativeTo(null);
        frameInter.setResizable(false);
        frameInter.setVisible(true);


        GererEmploye(NomCurrentUser);
        Deconnexion();

    }



    //------------------- Gérer les employes --------------------
    public void GererEmploye(String user)
    {
        BtnEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameInter.dispose();
                new Employe(user);
            }
        });
    }

    //------------------------ Déconnexion ------------------------
    public void Deconnexion()
    {
        decBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Déconnecté !");
                frameInter.dispose();
                new Login();
            }
        });

    }
}
