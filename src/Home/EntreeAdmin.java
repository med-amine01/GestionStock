package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class EntreeAdmin {
    private JTextField inputEntreeAdmin;
    private JButton modifBtn;
    private JButton confBtn;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JTextField qteentree;
    private JTextField dateentree;
    private JTextField montantentree;
    private JButton rechBtn;
    private JPanel MainEntreeAdmin;
    private JButton retBtn;
    private JTable table1;
    private JLabel currentUser;


    Connection con;
    PreparedStatement pst;
    JFrame frameEntreeAdmin;

    public EntreeAdmin(String NomCurrentUser)
    {
        frameEntreeAdmin = new JFrame("EntreeAdmin");
        frameEntreeAdmin.setContentPane(MainEntreeAdmin);
        frameEntreeAdmin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameEntreeAdmin.setLocationRelativeTo(null);
        frameEntreeAdmin.setResizable(false);
        frameEntreeAdmin.pack();
        frameEntreeAdmin.setVisible(true);

        setCurrentUser(NomCurrentUser);
        connect();
        Actualiser();

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
            pst = con.prepareStatement("select * from entree");
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
                frameEntreeAdmin.dispose();
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
                String rech = inputEntreeAdmin.getText().trim();
                if(rech.equals(""))
                {
                    Actualiser();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select * from entree where identree like '"+rech+"%' or idemp like '"+rech+"%'" +
                                "or idfour like '"+rech+"%' or idpiece like '"+rech+"%' or qte like '"+rech+"%' or date like '"+rech+"%'");
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
                String id=inputEntreeAdmin.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Modifier (id invalide)");
                    inputEntreeAdmin.setText("");
                    inputEntreeAdmin.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select idpiece,idfour,qte,date,montant from entree where identree = "+id+";");
                        ResultSet rs = pst.executeQuery();

                        while(rs.next())
                        {
                            //idpiece.setText(rs.getString("nom"));
                            //idfour.setText(rs.getString("prenom"));
                            qteentree.setText(rs.getString("qte"));
                            dateentree.setText(rs.getString("date"));
                            montantentree.setText(rs.getString("montant"));

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
    //------------------ Confirmer Modification ------------------------
//    public void Confirme()
//    {
//        confBtn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                /*
//                String [] tabpst = new String[3];
//                for(int i=0; i<3; i++) {
//                    tabpst[i] = PostEmp.getSelectedItem().toString();
//                }
//                 */
//
//                String qte,date,montant,post,idconf;
//                idconf = inputEntreeAdmin.getText().trim();
//                qte = qteentree.getText().trim();
//                date = dateentree.getText().trim();
//                montant = montantentree.getText().trim();
//                //post = tabpst[PostEmp.getSelectedIndex()];
//
//
//
//                if(ChampEstVide(qte, date, montant))
//                {
//                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
//                    qteentree.requestFocus();
//                }
//                else
//                {
//                    try
//                    {
//                        pst = con.prepareStatement("update entree set nom = ? , prenom = ? , adresse = ? , mail = ? , salaire = ? , post = ? , password = ? where idemp = ? ");
//
//                        pst.setString(1, nom);
//                        pst.setString(2, prenom);
//                        pst.setString(3, adresse);
//                        pst.setString(4, mail);
//                        pst.setString(5, salaire);
//                        pst.setString(6, post);
//                        pst.setString(7, password);
//                        pst.setString(8, idconf);
//                        pst.executeUpdate();
//                        JOptionPane.showMessageDialog(null, "Employé Modifié !!");
//                        Actualiser();
//                        inputEmp.setText("");
//                        inputEmp.requestFocus();
//
//                    }
//                    catch (SQLException e1)
//                    {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
    //------------------------------- verification tous les champs ----------------------------
    public boolean ChampEstVide(String...champs)
    {
        boolean b = false;
        for(String ch : champs)
        {
            if(ch.length() == 0)
            {
                b = true;
                break;
            }
            else
            {
                b = false;
            }
        }
        return b;
    }
}
