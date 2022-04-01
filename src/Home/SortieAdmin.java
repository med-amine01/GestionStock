package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class SortieAdmin {
    private JComboBox idpiecesortie;
    private JTextField qtesortie;
    private JTextField datesortie;
    private JButton rechBtn;
    private JTextField inputSortieAdmin;
    private JButton confBtn;
    private JButton modifBtn;
    private JButton retBtn;
    private JPanel MainSortieAdmin;
    private JTable table1;
    private JLabel currentUser;
    private JTextField idempsortie;


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
        Actualiser();

        setListeDeroulantepiece();
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
            pst = con.prepareStatement("select idsortie,idpiece,qte,date,montant from sortie");
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


    //================================================================================


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
                        pst = con.prepareStatement("select idsortie,idemp,idpiece,qte,date from sortie where idsortie like '"+rech+"%' or idemp like '"+rech+"%'" +
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
                        pst = con.prepareStatement("select idemp,idpiece,qte,date,montant from sortie where idsortie = "+id+";");
                        ResultSet rs = pst.executeQuery();

                        while(rs.next())
                        {
                            idempsortie.setText(rs.getString("idemp"));
                            idempsortie.setEditable(false);
                            idpiecesortie.setSelectedIndex(indexInListPiece(id));
                            qtesortie.setText(rs.getString("qte"));
                            datesortie.setText(rs.getString("date"));
                            datesortie.setEditable(false);

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
                String qte, idconf,idemp ;
                Date date;
                idconf = inputSortieAdmin.getText().trim();
                qte = qtesortie.getText().trim();
                date = Date.valueOf(datesortie.getText());
                idemp = idempsortie.getText().trim();

                if(ChampEstVide(qte))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    qtesortie.requestFocus();
                }
                else
                {
                    if(ChampsIdEstInt(qte)==false)
                    {
                        JOptionPane.showMessageDialog(null, "quantité invalide");
                        qtesortie.setText("");
                        qtesortie.requestFocus();
                    }
                    else
                    {
                        if(QteSupOuEgaleZero(qte) == false)
                        {
                            JOptionPane.showMessageDialog(null, "quantité invalide");
                            qtesortie.setText("");
                            qtesortie.requestFocus();
                        }
                        else
                        {
                            try
                            {
                                pst1 = con.prepareStatement("select prixunitaire from piece where idpiece ="+getIndexPiece()+";");
                                ResultSet rs1 = pst1.executeQuery();

                                pst = con.prepareStatement("update sortie set idemp = ? , idpiece = ? ,  qte = ? , date = ? , montant = ? where idsortie = ?");

                                while (rs1.next())
                                {
                                    Double montant1 = (parseInt(qte) * Double.parseDouble(rs1.getString("prixunitaire")));

                                    pst.setString(1, idemp);
                                    pst.setString(2, getIndexPiece());
                                    pst.setString(3, qte);
                                    pst.setDate(4,date);
                                    pst.setDouble(5,montant1);
                                    pst.setString(6, idconf);
                                    pst.executeUpdate();
                                    JOptionPane.showMessageDialog(null, "Sortie Modifiée  !!");
                                    Actualiser();
                                    inputSortieAdmin.setText("");
                                    qtesortie.setText("");
                                    datesortie.setText("");
                                    inputSortieAdmin.requestFocus();
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
            pst = con.prepareStatement("select idsortie from sortie");
            ResultSet rs = pst.executeQuery();

            while(rs.next())
            {
                listid.add(rs.getString("idsortie"));
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

    //----------------------- get and set id piece in list déroulante --------------------
    public void IdPieceList()
    {
        setIndexPiece(idpiecesortie.getItemAt(0).toString());
        idpiecesortie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.getSource();
                String s = (String) idpiecesortie.getSelectedItem();
                setIndexPiece(s);
            }
        });
    }

    //------------------------ list déroulante ------------------
    public void setListeDeroulantepiece()
    {
        try {
            pst = con.prepareStatement("select idpiece from piece");
            ResultSet rs = pst.executeQuery();


            while (rs.next())
            {
                idpiecesortie.addItem(rs.getString("idpiece"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    //

}
