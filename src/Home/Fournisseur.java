package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Fournisseur {

    private JPanel MainFour;
    private JTable table1;
    private JTextField nomFour;
    private JTextField addFour;
    private JTextField telFour;
    private JTextField mailFour;
    private JTextField inputFour;
    private JButton modBtn;
    private JButton suppBtn;
    private JButton rechBtn;
    private JButton ajouBtn;
    private JButton confBtn;
    private JButton retBtn;
    private JLabel currentUser;

    Connection con;
    PreparedStatement pst;
    JFrame frameFour;


    public Fournisseur(String NomCurrentUser) {
        frameFour = new JFrame("Fournisseur");
        frameFour.setContentPane(MainFour);
        frameFour.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameFour.pack();
        frameFour.setLocationRelativeTo(null);
        frameFour.setResizable(false);
        frameFour.setVisible(true);

        setCurrentUser(NomCurrentUser);
        connect();
        Actualiser();
        AjouterFournisseur();
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

    //--------------------------------AJOUTER Fournisseur----------------------------
    public void AjouterFournisseur()
    {
        ajouBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nom,adresse,mail,numtel;
                nom = nomFour.getText().trim();
                adresse = addFour.getText().trim();
                mail = mailFour.getText().trim();
                numtel = telFour.getText().trim();

                if(ChampEstVide(nom,adresse,mail,numtel))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    nomFour.requestFocus();
                }
                else
                {
                    if(ChampTelEstInteger(numtel) == false || telEstUnique(numtel) == false)
                    {
                        JOptionPane.showMessageDialog(null, "Le Fournisseur Déjà Existe ou Numéro Téléphone invalide");
                        telFour.setText("");
                        telFour.requestFocus();
                    }
                    else
                    {
                        if(MailEstUnique(mail) == false || valideMail(mail) == false)
                        {
                            JOptionPane.showMessageDialog(null, "Le Fournisseur Déjà Existe ou mail invalide");
                            mailFour.setText("");
                            mailFour.requestFocus();
                        }
                        else
                        {
                            try
                            {
                                pst = con.prepareStatement("insert into fournisseur (nom,adresse,tel,mail) values (?,?,?,?)");
                                pst.setString(1, nom);
                                pst.setString(2, adresse);
                                pst.setString(3, numtel);
                                pst.setString(4, mail);
                                pst.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Fournisseur Ajouté !!");
                                Actualiser();
                                nomFour.setText("");
                                addFour.setText("");
                                mailFour.setText("");
                                telFour.setText("");
                                nomFour.requestFocus();
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
    //-------------------- mail unique et chargement -------------------
    public boolean MailEstUnique(String inputMail)
    {
        ArrayList listmail = new ArrayList();
        try
        {
            pst = con.prepareStatement("select mail from fournisseur");
            ResultSet rs = pst.executeQuery();

            //empiler tabmail avec les mail à partir de la base de donnée
            while(rs.next())
            {
                listmail.add(rs.getString("mail"));
            }

            int i = 0 ;
            boolean bool = false;
            while(i<listmail.size() || bool == true)
            {
                if(inputMail.equals(listmail.get(i)))
                {
                    bool = true;
                    return false;
                }
                i++;
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return true ;
    }

    //----------------------- mail form ---------------------------
    public boolean valideMail(String inputMail)
    {
        if(inputMail.indexOf('@') != -1 && inputMail.indexOf('.') != -1)
        {
            int indiceAlt= inputMail.indexOf('@');
            String login = inputMail.substring(0,indiceAlt);
            String domain = inputMail.substring(indiceAlt,inputMail.lastIndexOf('.'));
            String ext = inputMail.substring(inputMail.lastIndexOf('.')+1);

            if(login.length()<2)
            {
                return false;
            }

            if(domain.length() <1)
            {
                return false;
            }

            if(ext.length() <2 || ext.length() > 3)
            {
                return false ;
            }
        }
        else
        {
            return false;
        }
        return true ;
    }

    //----------------- num tel valid-----------------------
    public boolean telEstUnique(String tel)
    {
        if(tel.length() != 8)
        {
            JOptionPane.showMessageDialog(null, "Numéro Téléphone Invalide !!");
            return false;
        }
        else
        {
            ArrayList listTel = new ArrayList();
            try
            {
                pst = con.prepareStatement("select tel from fournisseur");
                ResultSet rs = pst.executeQuery();

                while(rs.next())
                {
                    listTel.add(rs.getString("tel"));
                }
                //System.out.println(listTel);
                //table1.setModel(DbUtils.resultSetToTableModel(rs));

                int i = 0 ;
                boolean bool = false;
                while(i<listTel.size() || bool == true)
                {
                    if(tel.equals(listTel.get(i)))
                    {
                        bool = true;
                        return false;
                    }
                    i++;
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean ChampTelEstInteger(String tel)
    {
        boolean b = false ;
        try
        {
            Integer.parseInt(tel);
            b = true;
        }
        catch(NumberFormatException e)
        {
            b = false;
        }
        return b;
    }

    //---------------------- chargement du tableau -----------------------------
    public void Actualiser()
    {
        try
        {
            pst = con.prepareStatement("select * from fournisseur");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //----------------------- Rechercher et afficher ------------------------
    public void Rechercher()
    {
        rechBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rech = inputFour.getText().trim();
                if(rech.equals(""))
                {
                    Actualiser();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select idfour,nom,adresse,tel,mail from fournisseur where idfour like '"+rech+"%' or nom like '"+rech+"%'" +
                                "or adresse like '"+rech+"%' or mail like '"+rech+"%'");
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
    //---------------------SUPPRIMER FOURNISSEUR----------------------------------
    public void Supprimer()//les admins ne peuvent pas etre supprimés après auth
    {
        suppBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=inputFour.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Supprimer");
                    inputFour.setText("");
                    inputFour.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("delete from fournisseur where idfour = ?");
                        pst.setString(1, id);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Fournisseur Supprimé !!");
                        Actualiser();
                        inputFour.setText("");
                        inputFour.requestFocus();
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
            pst = con.prepareStatement("select idfour from fournisseur");
            ResultSet rs = pst.executeQuery();


            while(rs.next())
            {
                listid.add(rs.getString("idfour"));
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
    //-----------------UPDATE FOURNISSEUR-------------------------------
    public void Modifier()
    {
        modBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=inputFour.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Modifier (id invalide)");
                    inputFour.setText("");
                    inputFour.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select nom,adresse,tel,mail from fournisseur where idfour = "+id+";");
                        ResultSet rs = pst.executeQuery();

                        while(rs.next())
                        {
                            nomFour.setText(rs.getString("nom"));
                            addFour.setText(rs.getString("adresse"));
                            telFour.setText(rs.getString("tel"));
                            mailFour.setText(rs.getString("mail"));

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

                String nom,adresse,tel,mail,idconf;
                idconf = inputFour.getText().trim();
                nom = nomFour.getText().trim();
                adresse = addFour.getText().trim();
                tel = telFour.getText().trim();
                mail = mailFour.getText().trim();



                if(ChampEstVide(nom,adresse,tel,mail))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    nomFour.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("update fournisseur set nom = ?, adresse = ? , tel = ?, mail = ? where idfour = ? ");

                        pst.setString(1, nom);
                        pst.setString(2, adresse);
                        pst.setString(3, tel);
                        pst.setString(4, mail);
                        pst.setString(5, idconf);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Fournisseur Modifié !!");
                        Actualiser();
                        inputFour.setText("");
                        inputFour.requestFocus();

                    }
                    catch (SQLException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    //------------ retour -------------
    public void RetourMainListChoix(String user)
    {
        retBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameFour.dispose();
                new Inter(user);
            }
        });
    }
    //-------------- set current user -------------------
    public void setCurrentUser(String currentUser) {
        System.out.println(currentUser);
        this.currentUser.setText(currentUser);
    }

}
