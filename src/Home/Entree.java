package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Entree {
    private JPanel MainEntree;
    private JTable table1;
    private JTextField qtentree;
    private JTextField datentree;
    private JTextField montantE;
    private JButton confBtn;
    private JLabel montant;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JButton actBtn;
    private JButton retBtn;
    JFrame frameEnt;

    Connection con;
    PreparedStatement pst;

    public Entree()
    {
        frameEnt = new JFrame("Entree");
        frameEnt.setContentPane(MainEntree);
        frameEnt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameEnt.pack();
        frameEnt.setLocationRelativeTo(null);
        frameEnt.setResizable(false);
        frameEnt.setVisible(true);

        connect();
        Actualiser();
        confirmer();
        //actualiser();
        //Retour();

    }
    //-------------------- Connection à la base de donnée -----------------------
    public void connect()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/gestionstock", "root","");
            System.out.println("Connecté !!");

        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
    //---------------------- chargement du tableau -----------------------------
    public void Actualiser()
    {
        try
        {
            pst = con.prepareStatement("select identree,idemp,idfour,idpiece,qte,date,montant from entree");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //----------------confirmer----------------------------
    public void confirmer()
    {
        confBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


            }
        });
    }
}
