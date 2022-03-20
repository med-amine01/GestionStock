package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Sortie {
    private JPanel MainSortie;
    private JTextField qtsortie;
    private JTextField datesortie;
    private JTextField montantS;
    private JButton confBtn;
    private JTable table1;
    private JButton actBtn;
    private JComboBox comboBox1;
    private JButton retBtn;
    private JLabel currentEmp;

    Connection con;
    PreparedStatement pst;
    JFrame frameSortie;

    public Sortie(String vendid)
    {
        frameSortie = new JFrame("Sortie");
        frameSortie.setContentPane(MainSortie);
        frameSortie.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameSortie.setLocationRelativeTo(null);
        frameSortie.setResizable(false);
        frameSortie.pack();
        frameSortie.setVisible(true);

        setCurrentUser(vendid);
        connect();
        Actualiser();
        //actualiser();
        //confirmer();
        RetourMainListChoix(vendid);


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
            pst = con.prepareStatement("select idsortie,idpiece,qte,date,montant from sortie");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    //--------------------------actu-----------------------------
    public void actualiser()
    {
        actBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Actualiser();
            }
        });
    }

    //----------------confirmer----------------------------
    public void confirmer()
    {
        actBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ajout sortie
            }
        });
    }
    //-------------- set current empid -------------------
    public void setCurrentUser(String currentUser)
    {
        System.out.println(currentUser);
        this.currentEmp.setText(currentUser);
    }
    //------------ retour -------------
    public void RetourMainListChoix(String user)
    {
        retBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameSortie.dispose();
                new Login();
            }
        });
    }
}
