package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class EntreeAdmin {
    private JTextField inputEntreeAdmin;
    private JButton modifBtn;
    private JButton confBtn;
    private JComboBox idpieceentree;
    private JComboBox idfourentree;
    private JTextField qteentree;
    private JTextField dateentree;
    private JButton rechBtn;
    private JPanel MainEntreeAdmin;
    private JButton retBtn;
    private JTable table1;
    private JLabel currentUser;
    private JTextField idempentree;


    private String indexFour;
    public String getIndexFour() {
        return indexFour;
    }
    public void setIndexFour(String indexFour) {
        this.indexFour = indexFour;
    }


    private String indexPiece;
    public String getIndexPiece()
    {
        return indexPiece;
    }
    public void setIndexPiece(String indexPiece) {
        this.indexPiece = indexPiece;
    }

    Connection con;
    PreparedStatement pst,pst1;
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

        setListeDeroulantefour();
        setListeDeroulantepiece();

        IdFourList();
        IdPieceList();


        Rechercher();
        Modifier();
        Confirme();

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
            pst = con.prepareStatement("select identree,idemp,idfour,idpiece,qte,date,montant from entree");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //---------------------------- retour --------------------------
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

    //------------------------------- set current user -------------------
    public void setCurrentUser(String currentUser)
    {
        System.out.println(currentUser);
        this.currentUser.setText(currentUser);
    }

    //----------------------- get and set id four in list déroulante --------------------
    public void IdFourList()
    {
        setIndexFour(idfourentree.getItemAt(0).toString());
        idfourentree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.getSource();
                String s = (String) idfourentree.getSelectedItem();
                setIndexFour(s);
            }
        });
    }


    //----------------------- get and set id piece in list déroulante --------------------
    public void IdPieceList()
    {
        setIndexPiece(idpieceentree.getItemAt(0).toString());
        idpieceentree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.getSource();
                String s = (String) idpieceentree.getSelectedItem();
                setIndexPiece(s);
            }
        });
    }

    //------------------------ list déroulante ------------------
    public void setListeDeroulantefour()
    {
        try {
            pst = con.prepareStatement("select idfour from fournisseur");
            ResultSet rs = pst.executeQuery();


            while (rs.next())
            {
                idfourentree.addItem(rs.getString("idfour"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //------------------------ list déroulante ------------------
    public void setListeDeroulantepiece()
    {
        try {
            pst = con.prepareStatement("select idpiece from piece");
            ResultSet rs = pst.executeQuery();


            while (rs.next())
            {
                idpieceentree.addItem(rs.getString("idpiece"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }




    //================================================================================


    //----------------------- Rechercher et afficher ------------------------
    public void Rechercher()
    {
        rechBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String rech = inputEntreeAdmin.getText().trim();
                if(rech.equals(""))
                {
                    Actualiser();
                }
                else
                {
                    if(IdExist(rech)==false)
                    {
                        JOptionPane.showMessageDialog(null, "ID n'existe pas !!");
                        inputEntreeAdmin.setText("");
                        inputEntreeAdmin.requestFocus();
                    }
                    else {
                        try {
                            pst = con.prepareStatement("select identree,idemp,idfour,idpiece,qte,date from entree where identree like '" + rech + "%' or idemp like '" + rech + "%'" +
                                    "or idfour like '" + rech + "%' or idpiece like '" + rech + "%' or qte like '" + rech + "%' or date like '" + rech + "%'");
                            ResultSet rs = pst.executeQuery();
                            table1.setModel(DbUtils.resultSetToTableModel(rs));

                            if (table1.getRowCount() == 0) {
                                JOptionPane.showMessageDialog(null, "Non Trouvé");
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
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
                        pst = con.prepareStatement("select idemp,idpiece,idfour,qte,date,montant from entree where identree = "+id+";");
                        ResultSet rs = pst.executeQuery();

                        while(rs.next())
                        {
                            idempentree.setText(rs.getString("idemp"));
                            idempentree.setEditable(false);
                            idpieceentree.setSelectedIndex(indexInListPiece(id));
                            idfourentree.setSelectedIndex(indexInListFournisseur(id));
                            qteentree.setText(rs.getString("qte"));
                            dateentree.setText(rs.getString("date"));
                            dateentree.setEditable(false);//date là où l'employé a passé l'entrée
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

    //------------------ Confirmer Modification ------------------------
    public void Confirme()
    {
        confBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String qte,idconf,idemp ;
                Date date;
                idconf = inputEntreeAdmin.getText().trim();
                qte = qteentree.getText().trim();
                date = Date.valueOf(dateentree.getText());
                idemp = idempentree.getText().trim();

                if(ChampEstVide(qte))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    qteentree.requestFocus();
                }
                else
                {
                    if(ChampsIdEstInt(qte)==false)
                    {
                        JOptionPane.showMessageDialog(null, "quantité invalide");
                        qteentree.setText("");
                        qteentree.requestFocus();
                    }
                    else
                    {
                        if(QteSupOuEgaleZero(qte) == false)
                        {
                            JOptionPane.showMessageDialog(null, "quantité invalide");
                            qteentree.setText("");
                            qteentree.requestFocus();
                        }
                        else
                        {
                            try
                            {
                                pst1 = con.prepareStatement("select prixunitaire from piece where idpiece ="+getIndexPiece()+";");
                                ResultSet rs1 = pst1.executeQuery();

                                pst = con.prepareStatement("update entree set idemp = ? , idpiece = ? , idfour = ? , qte = ? , date = ? , montant = ? where identree = ?");

                                while (rs1.next())
                                {
                                    Double montant1 = (parseInt(qte) * Double.parseDouble(rs1.getString("prixunitaire")));

                                    pst.setString(1, idemp);
                                    pst.setString(2, getIndexPiece());
                                    pst.setString(3, getIndexFour());
                                    pst.setString(4, qte);
                                    pst.setDate(5,date);
                                    pst.setDouble(6,montant1);
                                    pst.setString(7, idconf);


                                    pst.executeUpdate();
                                    JOptionPane.showMessageDialog(null, "Entrée Modifiée  !!");
                                    Actualiser();
                                    inputEntreeAdmin.setText("");
                                    qteentree.setText("");
                                    dateentree.setText("");
                                    inputEntreeAdmin.requestFocus();
                                }

                            }
                            catch (SQLException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }


    //================================================================================


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

    //-------------------------- Champ qte ---------------------
    public boolean QteSupOuEgaleZero(String champsId)
    {
        int qte = Integer.parseInt(champsId);
        if(qte >= 0)
        {
            return true;
        }
        return false;
    }

    //---------------------- indexInList des pieces selon id piece---------------
    public int indexInListPiece(String id)
    {
        try
        {
            pst = con.prepareStatement("select idpiece from entree where identree = "+id);
            ResultSet rs1 = pst.executeQuery();
            String s ="";
            while (rs1.next())
            {
                s = rs1.getString("idpiece");
            }

            ArrayList<String> idfo = new ArrayList<>();
            pst = con.prepareStatement("select idpiece from entree");
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                idfo.add(rs.getString("idpiece"));
                if(rs.getString("idpiece").equals(s))
                {
                    return idfo.indexOf(s);
                }
            }

            idfo.clear();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    //---------------------- indexInList des fournisseurs selon id piece---------------
    public int indexInListFournisseur(String id)
    {
        try
        {
            pst = con.prepareStatement("select idfour from entree where identree = "+id);
            ResultSet rs1 = pst.executeQuery();
            String s ="";
            while (rs1.next())
            {
                s = rs1.getString("idfour");
            }

            ArrayList<String> idfo = new ArrayList<>();
            pst = con.prepareStatement("select idfour from entree");
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                idfo.add(rs.getString("idfour"));
                if(rs.getString("idfour").equals(s))
                {
                    return idfo.indexOf(s);
                }
            }

            idfo.clear();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }


//


}

