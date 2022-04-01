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
        GererFournisseur(NomCurrentUser);
        GererClient(NomCurrentUser);
        GererPiece(NomCurrentUser);
        GererEntree(NomCurrentUser);
        GererSortie(NomCurrentUser);
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

    //------------------- Gérer les fournisseurs -----------------
    public void GererFournisseur(String user)
    {
        BtnFour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameInter.dispose();
                new Fournisseur(user);
            }
        });
    }

    //------------------- Gérer les clients -----------------------
    public void GererClient(String user)
    {
        BtnCli.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameInter.dispose();
                new Client(user);
            }
        });
    }

    //------------------- Gérer les pièces ---------------------
    public void GererPiece(String user)
    {
        BtnPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameInter.dispose();
                new Piece(user);
            }
        });
    }

    //------------------- Gérer les entrées admin --------------------------
    public void GererEntree(String user)
    {
        Btnentree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameInter.dispose();
                new EntreeAdmin(user);
            }
        });
    }

    //------------------- Gérer les sorties admin -----------------------------
    public void GererSortie(String user)
    {
        Btnsortie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameInter.dispose();
                new SortieAdmin(user);
            }
        });
    }

    //------------------- Déconnexion ------------------------
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

//
}
