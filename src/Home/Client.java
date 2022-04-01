package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Client {
    private JPanel MainClient;
    private JButton modifBtn;
    private JButton suppBtn;
    private JButton rechBtn;
    private JTextField nomclient;
    private JTextField prenomclient;
    private JTextField adresseclient;
    private JTextField mailclient;
    private JButton ajouBtn;
    private JButton confBtn;
    private JTextField inputClient;
    private JTable table1;
    private JButton retBtn;
    private JLabel currentUser;

    Connection con;
    PreparedStatement pst;
    JFrame frameClient;

    public Client(String NomCurrentUser)
    {
        frameClient = new JFrame("Client");
        frameClient.setContentPane(MainClient);
        frameClient.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameClient.setLocationRelativeTo(null);
        frameClient.setResizable(false);
        frameClient.pack();
        frameClient.setVisible(true);

        setCurrentUser(NomCurrentUser);
        connect();
        Actualiser();
        AjouterClient();
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
            pst = con.prepareStatement("select idclient,nom,prenom,adresse,mail from client");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //--------------------------------AJOUTER CLIENT----------------------------
    public void AjouterClient()
    {
        ajouBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nom,prenom,adresse,mail;
                nom = nomclient.getText().trim();
                prenom = prenomclient.getText().trim();
                adresse = adresseclient.getText().trim();
                mail = mailclient.getText().trim();


                if(ChampEstVide(nom, prenom, adresse, mail))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    nomclient.requestFocus();
                }
                else
                {
                    if(!MailEstUnique(mail) || !valideMail(mail))
                    {
                        JOptionPane.showMessageDialog(null, "Le client Déjà Existe ou  mail invalide");
                        mailclient.setText("");
                        mailclient.requestFocus();
                    }
                    else
                    {
                        try
                            {
                                pst = con.prepareStatement("insert into client (nom,prenom,adresse,mail) values (?,?,?,?)");
                                pst.setString(1, nom);
                                pst.setString(2, prenom);
                                pst.setString(3, adresse);
                                pst.setString(4, mail);
                                pst.executeUpdate();
                                JOptionPane.showMessageDialog(null, "client Ajouté !!");
                                Actualiser();
                                nomclient.setText("");
                                prenomclient.setText("");
                                adresseclient.setText("");
                                mailclient.setText("");
                            }
                            catch (SQLException e1)
                            {
                                e1.printStackTrace();
                            }

                    }
                }
            }
        });
    }

    //----------------------- Rechercher et afficher ------------------------
    public void Rechercher()
    {
        rechBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rech = inputClient.getText().trim();
                if(rech.equals(""))
                {
                    Actualiser();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select idclient,nom,prenom,adresse,mail from client where idclient like '"+rech+"%' or nom like '"+rech+"%'" +
                                "or prenom like '"+rech+"%' or adresse like '"+rech+"%'");
                        ResultSet rs = pst.executeQuery();
                        table1.setModel(DbUtils.resultSetToTableModel(rs));

                        //System.out.println(table1.getRowCount());

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

    //---------------------SUPPRIMER CLIENT----------------------------------
    public void Supprimer()
    {
        suppBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=inputClient.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Supprimer");
                    inputClient.setText("");
                    inputClient.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("delete from client where idclient = ?");
                        pst.setString(1, id);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "client Supprimé !!");
                        Actualiser();
                        inputClient.setText("");
                        inputClient.requestFocus();
                    }
                    catch (SQLException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    //-----------------UPDATE CLIENT-------------------------------
    public void Modifier()
    {
        modifBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=inputClient.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Modifier (id invalide)");
                    inputClient.setText("");
                    inputClient.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select nom,prenom,adresse,mail from client where idclient = "+id+";");
                        ResultSet rs = pst.executeQuery();

                        while(rs.next())
                        {
                            nomclient.setText(rs.getString("nom"));
                            prenomclient.setText(rs.getString("prenom"));
                            adresseclient.setText(rs.getString("adresse"));
                            mailclient.setText(rs.getString("mail"));

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

                String nom,prenom,adresse,mail, idconf;
                idconf = inputClient.getText().trim();
                nom = nomclient.getText().trim();
                prenom = prenomclient.getText().trim();
                adresse = adresseclient.getText().trim();
                mail = mailclient.getText().trim();


                if(ChampEstVide(nom, prenom, adresse, mail))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    nomclient.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("update client set nom = ? , prenom = ? , adresse = ? , mail = ?  where idclient = ? ");

                        pst.setString(1, nom);
                        pst.setString(2, prenom);
                        pst.setString(3, adresse);
                        pst.setString(4, mail);
                        pst.setString(5, idconf);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "client Modifié !!");
                        Actualiser();
                        inputClient.setText("");
                        inputClient.requestFocus();

                    }
                    catch (SQLException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }





    //================================================================================




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
            pst = con.prepareStatement("select mail from client");
            ResultSet rs = pst.executeQuery();

            while(rs.next())
            {
                listmail.add(rs.getString("mail"));
            }


            for(int j=0; j<listmail.size();j++)
            {
                if(inputMail.equals(listmail.get(j)))
                {
                    return false;
                }
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return true;
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
            pst = con.prepareStatement("select idclient from client");
            ResultSet rs = pst.executeQuery();

            //empiler tabid avec les id à partir de la base de donnée
            while(rs.next())
            {
                listid.add(rs.getString("idclient"));
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

    //-------------- set current user -------------------
    public void setCurrentUser(String currentUser)
    {
        System.out.println(currentUser);
        this.currentUser.setText(currentUser);
    }

    //------------ retour -------------
    public void RetourMainListChoix(String user)
    {
        retBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameClient.dispose();
                new Inter(user);
            }
        });
    }
    //
}
