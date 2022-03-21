package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Piece {
    private JTable table1;
    private JTextField marquepiece;
    private JTextField modelepiece;
    private JTextField seriepiece;
    private JTextField quantitepiece;
    private JTextField prixunitairep;
    private JPanel MainPiece;
    private JButton modifBtn;
    private JButton suppBtn;
    private JButton rechercherPieceButton;
    private JButton confBtn;
    private JButton ajoutBtn;
    private JTextField inputpiece;
    private JComboBox idfourp;
    private JButton retBtn;
    private JLabel currentUser;


    private String indexFour;
    public String getIndexFour() {
        return indexFour;
    }

    public void setIndexFour(String indexFour) {
        this.indexFour = indexFour;
    }



    Connection con;
    PreparedStatement pst;
    JFrame framePiece;

    public Piece(String NomCurrentUser)
    {
        framePiece = new JFrame("Piece");
        framePiece.setContentPane(MainPiece);
        framePiece.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        framePiece.setLocationRelativeTo(null);
        framePiece.setResizable(false);
        framePiece.pack();
        framePiece.setVisible(true);

        connect();
        setListeDeroulante();
        IdFourList();
        setCurrentUser(NomCurrentUser);
        Actualiser();
        AjouterPiece();
        Rechercher();
        Supprimer();
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
            pst = con.prepareStatement("select idpiece,marque,modele,serie,qte,prixunitaire,idfour from piece");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    //----------------------- get and set id four in list déroulante --------------------
    public void IdFourList()
    {
        setIndexFour(idfourp.getItemAt(0).toString());
        idfourp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.getSource();
                String s = (String) idfourp.getSelectedItem();
                setIndexFour(s);
            }
        });
    }

    //--------------------------------AJOUTER PIECE----------------------------
    public void AjouterPiece()
    {
        ajoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String marque,modele,serie,qte,prixunitaire ;
                marque = marquepiece.getText().trim();
                modele = modelepiece.getText().trim();
                serie = seriepiece.getText().trim();
                qte = quantitepiece.getText().trim();
                prixunitaire = prixunitairep.getText().trim();


                if(ChampEstVide(marque, modele, serie, qte, prixunitaire))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    marquepiece.requestFocus();
                }
                else
                {
                    if(ChampsIdEstInt(qte)==false)
                    {
                        JOptionPane.showMessageDialog(null, "quantité invalide");
                        quantitepiece.setText("");
                        quantitepiece.requestFocus();
                    }
                    else
                    {
                        if(QteSupOuEgaleZero(qte) == false)
                        {
                            JOptionPane.showMessageDialog(null, "quantité invalide");
                            quantitepiece.setText("");
                            quantitepiece.requestFocus();
                        }
                        else
                        {
                            if(ChampPrixEstDouble(prixunitaire)==false)
                            {
                                JOptionPane.showMessageDialog(null, "prix unitaire invalide !!");
                                prixunitairep.setText("");
                                prixunitairep.requestFocus();
                            }
                            else
                            {
                                if(PrixSupZero(prixunitaire) == false)
                                {
                                    JOptionPane.showMessageDialog(null, "prix unitaire invalide !!");
                                    prixunitairep.setText("");
                                    prixunitairep.requestFocus();
                                }
                                else
                                {
                                    if(MMSEstUnique(marque, "marque") || MMSEstUnique(modele, "modele") || MMSEstUnique(serie, "serie"))
                                    {
                                        try
                                        {
                                            pst = con.prepareStatement("insert into piece (marque,modele,serie,qte,prixunitaire,idfour) values (?,?,?,?,?,?)");
                                            pst.setString(1, marque);
                                            pst.setString(2, modele);
                                            pst.setString(3, serie);
                                            pst.setString(4, qte);
                                            pst.setString(5, prixunitaire);
                                            pst.setString(6, getIndexFour());


                                            pst.executeUpdate();
                                            JOptionPane.showMessageDialog(null, "pièce Ajoutée !!");
                                            Actualiser();
                                            marquepiece.setText("");
                                            modelepiece.setText("");
                                            seriepiece.setText("");
                                            quantitepiece.setText("");
                                            prixunitairep.setText("");
                                        }
                                        catch (SQLException e1)
                                        {
                                            e1.printStackTrace();
                                        }
                                    }
                                    else
                                    {
                                        JOptionPane.showMessageDialog(null, "Marque et Modele et Série Déjà EXIST !!");
                                        marquepiece.setText("");
                                        modelepiece.setText("");
                                        seriepiece.setText("");
                                        marquepiece.requestFocus();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    //----------------------- Rechercher et afficher ------------------------
    public void Rechercher()
    {
        rechercherPieceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rech = inputpiece.getText().trim();
                if(rech.equals(""))
                {
                    Actualiser();
                }
                else
                {
                    if(IdExist(rech)==false)
                    {
                        JOptionPane.showMessageDialog(null, "ID n'existe pas !!");
                        inputpiece.setText("");
                        inputpiece.requestFocus();
                    }
                    else
                    {
                        try
                        {
                            pst = con.prepareStatement("select idpiece,marque,modele,serie,idfour from piece where idpiece like '"+rech+"%' or marque like '"+rech+"%'" +
                                    "or modele like '"+rech+"%' or serie like '"+rech+"%' or idfour like '"+rech+"%'");
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
            }
        });
    }

    //---------------------SUPPRIMER PIECE----------------------------------
    public void Supprimer()
    {
        suppBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=inputpiece.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Supprimer (champ invalide ou ID n'existe pas)");
                    inputpiece.setText("");
                    inputpiece.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("delete from piece where idpiece = ?");
                        pst.setString(1, id);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Pièce Supprimée !!");
                        Actualiser();
                        inputpiece.setText("");
                        inputpiece.requestFocus();
                    }
                    catch (SQLException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    //-----------------UPDATE PIECE-------------------------------
    public void Modifier()
    {
        modifBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=inputpiece.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Modifier (id invalide)");
                    inputpiece.setText("");
                    inputpiece.requestFocus();
                }
                else
                {

                    try
                    {
                        pst = con.prepareStatement("select marque,modele,serie,qte,prixunitaire,idfour from piece where idpiece = "+id+";");
                        ResultSet rs = pst.executeQuery();

                        while(rs.next())
                        {
                            marquepiece.setText(rs.getString("marque"));
                            modelepiece.setText(rs.getString("modele"));
                            seriepiece.setText(rs.getString("serie"));
                            quantitepiece.setText(rs.getString("qte"));
                            prixunitairep.setText(rs.getString("prixunitaire"));
                            idfourp.setSelectedIndex(indexInListFournisseur(id));

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
                String marque,modele,serie,qte,prixunitaire, idconf ;
                idconf = inputpiece.getText().trim();
                marque = marquepiece.getText().trim();
                modele = modelepiece.getText().trim();
                serie = seriepiece.getText().trim();
                qte = quantitepiece.getText().trim();
                prixunitaire = prixunitairep.getText().trim();


                if(ChampEstVide(marque, modele, serie, qte, prixunitaire))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    marquepiece.requestFocus();
                }
                else
                {
                    if(ChampsIdEstInt(qte)==false)
                    {
                        JOptionPane.showMessageDialog(null, "quantité invalide");
                        quantitepiece.setText("");
                        quantitepiece.requestFocus();
                    }
                    else
                    {
                        if(QteSupOuEgaleZero(qte) == false)
                        {
                            JOptionPane.showMessageDialog(null, "quantité invalide");
                            quantitepiece.setText("");
                            quantitepiece.requestFocus();
                        }
                        else
                        {
                            if(ChampPrixEstDouble(prixunitaire)==false)
                            {
                                JOptionPane.showMessageDialog(null, "prix unitaire invalide !!");
                                prixunitairep.setText("");
                                prixunitairep.requestFocus();
                            }
                            else
                            {
                                if(PrixSupZero(prixunitaire) == false)
                                {
                                    JOptionPane.showMessageDialog(null, "prix unitaire invalide !!");
                                    prixunitairep.setText("");
                                    prixunitairep.requestFocus();
                                }
                                else
                                {
                                    if(MMSEstUnique(marque, "marque", idconf) || MMSEstUnique(modele, "modele",idconf) || MMSEstUnique(serie, "serie",idconf))
                                    {
                                        try
                                        {
                                            pst = con.prepareStatement("update piece set marque = ? , modele = ? , serie = ? , qte = ? , prixunitaire = ? , idfour = ? where idpiece = ?");
                                            pst.setString(1, marque);
                                            pst.setString(2, modele);
                                            pst.setString(3, serie);
                                            pst.setString(4, qte);
                                            pst.setString(5, prixunitaire);
                                            pst.setString(6, getIndexFour());
                                            pst.setString(7, idconf);
                                            pst.executeUpdate();
                                            JOptionPane.showMessageDialog(null, "Pièce Modifié !!");
                                            Actualiser();
                                            inputpiece.setText("");
                                            marquepiece.setText("");
                                            modelepiece.setText("");
                                            seriepiece.setText("");
                                            quantitepiece.setText("");
                                            prixunitairep.setText("");
                                            inputpiece.requestFocus();
                                        }
                                        catch (SQLException e1)
                                        {
                                            e1.printStackTrace();
                                        }
                                    }
                                    else
                                    {
                                        JOptionPane.showMessageDialog(null, "Marque et Modele et Série Déjà EXIST !!");
                                        marquepiece.setText("");
                                        modelepiece.setText("");
                                        seriepiece.setText("");
                                        marquepiece.requestFocus();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }


    //==============================================================================================


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

    //-------------------------- Champ prix ---------------------
    public boolean PrixSupZero(String champsId)
    {
        double prix = Double.parseDouble(champsId);
        if(prix > 0.0)
        {
            return true;
        }
        return false;
    }

    //------------------------- verification champ prixunitaire ----------------------
    public boolean ChampPrixEstDouble(String prix)
    {
        boolean b = false ;
        try
        {
            Double.parseDouble(prix);
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
        ArrayList<String> listid = new ArrayList();
        try
        {
            pst = con.prepareStatement("select idpiece from piece");
            ResultSet rs = pst.executeQuery();


            while(rs.next())
            {
                listid.add(rs.getString("idpiece"));
            }


            int i = 0 ;
            boolean bool = false;
            while(i<listid.size() || bool == true)
            {
                if(id.equals(listid.get(i)))
                {
                    bool = true;
                    return true;
                }
                i++;
            }

            listid.clear();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    //------------------- retour -------------
    public void RetourMainListChoix(String user)
    {
        retBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePiece.dispose();
                new Inter(user);
            }
        });
    }

    //-------------- set current user -------------------
    public void setCurrentUser(String currentUser) {
        this.currentUser.setText(currentUser);
    }

    //------------------------ list déroulante ------------------
    public void setListeDeroulante()
    {
        try {
            pst = con.prepareStatement("select idfour from fournisseur");
            ResultSet rs = pst.executeQuery();


            while (rs.next())
            {
                idfourp.addItem(rs.getString("idfour"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //------------------- Marque Serie Modele est UNIQUE Modification----------------------
    public boolean MMSEstUnique(String mms, String rqt, String id)
    {
        ArrayList<String> listMMS = new ArrayList();
        try
        {
            pst = con.prepareStatement("select "+rqt+" from piece where idpiece != '"+id+ "'");
            ResultSet rs = pst.executeQuery();


            while(rs.next())
            {
                listMMS.add(rs.getString(rqt));
            }


            int i = 0 ;
            boolean bool = false;
            while(i<listMMS.size() || bool == true)
            {
                if(mms.equals(listMMS.get(i))) // l9a mms fi base donc mahich unique
                {
                    bool = true;
                    return false;
                }
                i++;
            }

            listMMS.clear();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return true; //donc unique eli jebeteha en para
    }
    //------------------- Marque Serie Modele est UNIQUE----------------------
    public boolean MMSEstUnique(String mms, String rqt)
    {
        ArrayList<String> listMMS = new ArrayList();
        try
        {
            pst = con.prepareStatement("select "+rqt+" from piece");
            ResultSet rs = pst.executeQuery();


            while(rs.next())
            {
                listMMS.add(rs.getString(rqt));
            }


            int i = 0 ;
            boolean bool = false;
            while(i<listMMS.size() || bool == true)
            {
                if(mms.equals(listMMS.get(i))) // l9a mms fi base donc mahich unique
                {
                    bool = true;
                    return false;
                }
                i++;
            }

            listMMS.clear();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return true; //donc unique eli jebeteha en para
    }


    //---------------------- indexInList de la fournisseur selon id piece---------------
    public int indexInListFournisseur(String id)
    {
        try
        {
            //bech yjib juste (idfour) piece mte3 id
            pst = con.prepareStatement("select idfour from piece where idpiece = "+id);
            ResultSet rs1 = pst.executeQuery();
            String s ="";
            while (rs1.next())
            {
                s = rs1.getString("idfour");
            }

            //bech yjib les pieces lkol w yee9if aand id
            ArrayList<String> idfo = new ArrayList<>();
            pst = con.prepareStatement("select idfour from piece");
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


}
