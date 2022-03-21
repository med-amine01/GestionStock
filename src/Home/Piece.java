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

        setCurrentUser(NomCurrentUser);
        connect();
        setListeDeroulante();
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

    //--------------------------------AJOUTER PIECE----------------------------
    public void AjouterPiece()
    {
        ajoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                idfourp.setSelectedIndex(0);
                ArrayList<String> listIDfour = new ArrayList();
                int i = 0 ;
                while (i<idfourp.getItemCount())
                {
                    listIDfour.add(idfourp.getSelectedItem().toString());
                    i++;
                }


                String marque,modele,serie,qte,prixunitaire,idfour ;
                marque = marquepiece.getText().trim();
                modele = modelepiece.getText().trim();
                serie = seriepiece.getText().trim();
                qte = quantitepiece.getText().trim();
                prixunitaire = prixunitairep.getText().trim();
                idfour = listIDfour.get(idfourp.getSelectedIndex());



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
                                    try
                                    {
                                        pst = con.prepareStatement("insert into piece (marque,modele,serie,qte,prixunitaire,idfour) values (?,?,?,?,?,?)");
                                        pst.setString(1, marque);
                                        pst.setString(2, modele);
                                        pst.setString(3, serie);
                                        pst.setString(4, qte);
                                        pst.setString(5, prixunitaire);
                                        pst.setString(6, idfour);
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
                            }
                        }
                    }
                }
                listIDfour.clear();
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
                    JOptionPane.showMessageDialog(null, "Impossible De Supprimer");
                    inputpiece.setText("");
                    inputpiece.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("delete from piece where idepiece = ?");
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
                            marquepiece.setText(rs.getString("nom"));
                            modelepiece.setText(rs.getString("prenom"));
                            seriepiece.setText(rs.getString("adresse"));
                            quantitepiece.setText(rs.getString("mail"));
                            prixunitairep.setText(rs.getString("salaire"));

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
                /*
                String [] tabpst = new String[3];
                for(int i=0; i<3; i++) {
                    tabpst[i] = PostEmp.getSelectedItem().toString();
                }
                 */

                String marque,modele,serie,qte,prixunitaire,idfour, idconf;
                idconf = inputpiece.getText().trim();
                marque = marquepiece.getText().trim();
                modele = modelepiece.getText().trim();
                serie = seriepiece.getText().trim();
                qte = quantitepiece.getText().trim();
                prixunitaire = prixunitairep.getText().trim();
                //idfour = tabpst[PostEmp.getSelectedIndex()];

                if(ChampEstVide(marque, modele, serie, qte, prixunitaire))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    marquepiece.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("update piece set marque = ? , modele = ? , serie = ? , qte = ? , prixunitaire = ?  where idpiece = ? ");

                        pst.setString(1, marque);
                        pst.setString(2, modele);
                        pst.setString(3, serie);
                        pst.setString(4, qte);
                        pst.setString(5, prixunitaire);
                        pst.setString(6, idconf);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Pièce Modifié !!");
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
        ArrayList listid = new ArrayList();
        try
        {
            pst = con.prepareStatement("select idpiece from piece");
            ResultSet rs = pst.executeQuery();

            //empiler tabid avec les id à partir de la base de donnée
            while(rs.next())
            {
                listid.add(rs.getString("idpiece"));
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
        System.out.println(currentUser);
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
}
