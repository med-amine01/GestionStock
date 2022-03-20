package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class SortieAdmin {
    private JComboBox comboBox1;
    private JTextField qtesortie;
    private JTextField datesortie;
    private JTextField montantsortie;
    private JButton rechBtn;
    private JTextField inputSortieAdmin;
    private JButton confBtn;
    private JButton modifBtn;
    private JButton retBtn;
    private JPanel MainSortieAdmin;
    private JTable table1;
    private JLabel currentUser;


    Connection con;
    PreparedStatement pst;
    JFrame frameSortieAdmin;

    public SortieAdmin(String NomCurrentUser)
    {
        frameSortieAdmin = new JFrame("SortieAdmin");
        frameSortieAdmin.setContentPane(MainSortieAdmin);
        frameSortieAdmin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameSortieAdmin.setLocationRelativeTo(null);
        frameSortieAdmin.setResizable(false);
        frameSortieAdmin.pack();
        frameSortieAdmin.setVisible(true);

        setCurrentUser(NomCurrentUser);
        connect();

        //Rechercher();
        //Modifier();
        //confirmer();
        RetourMainListChoix(NomCurrentUser);


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
            pst = con.prepareStatement("select idemp,nom,prenom,adresse,mail,salaire,post from employe");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    //------------ retour -------------
    public void RetourMainListChoix(String user)
    {
        retBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameSortieAdmin.dispose();
                new Inter(user);
            }
        });
    }
    //-------------- set current user -------------------
    public void setCurrentUser(String currentUser) {
        System.out.println(currentUser);
        this.currentUser.setText(currentUser);
    }
    //----------------------- Rechercher et afficher ------------------------
    public void Rechercher()
    {
        rechBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rech = inputSortieAdmin.getText().trim();
                if(rech.equals(""))
                {
                    Actualiser();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select * from sortie where idsortie like '"+rech+"%' or idemp like '"+rech+"%'" +
                                " or idpiece like '"+rech+"%' or qte like '"+rech+"%' or date like '"+rech+"%'");
                        ResultSet rs = pst.executeQuery();
                        table1.setModel(DbUtils.resultSetToTableModel(rs));


                        if(table1.getRowCount() == 0)
                        {
                            JOptionPane.showMessageDialog(null, "Non Trouvé");
                        }

                    }
                    catch (SQLException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    //-----------------UPDATE Entree-------------------------------
    public void Modifier()
    {
        modifBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=inputSortieAdmin.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Modifier (id invalide)");
                    inputSortieAdmin.setText("");
                    inputSortieAdmin.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select idpiece,qte,date,montant from sortie where idsortie = "+id+";");
                        ResultSet rs = pst.executeQuery();

                        while(rs.next())
                        {
                            //idpiece.setText(rs.getString("nom"));
                            qtesortie.setText(rs.getString("qte"));
                            datesortie.setText(rs.getString("date"));
                            montantsortie.setText(rs.getString("montant"));

                        }
                    }
                    catch (SQLException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    //----------------------------id chiffres-----------------------------
    public boolean ChampsIdEstInt(String champsId)
    {
        boolean b = false ;
        try
        {
            Integer.parseInt(champsId);
            b = true;
        }
        catch(NumberFormatException e)
        {
            b = false;
        }
        return b;
    }
    //--------------------------id exist---------------------------------
    public boolean IdExist(String id)
    {
        ArrayList listid = new ArrayList();
        try
        {
            pst = con.prepareStatement("select identree from entree");
            ResultSet rs = pst.executeQuery();

            while(rs.next())
            {
                listid.add(rs.getString("identree"));
            }


            for(int j=0; j<listid.size();j++)
            {
                if(id.equals(listid.get(j)))
                {
                    return true;
                }
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
