package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Employe {
    public JPanel MainEmploye;
    private JTextField preEmp;
    private JTextField addEmp;
    private JTextField nomEmp;
    private JPasswordField pwdEmp;
    private JComboBox PostEmp ;
    private JTable table1;
    private JButton ajouBtn;
    private JTextField inputEmp;
    private JButton modBtn;
    private JButton SuppBtn;
    private JButton rechBtn;
    private JTextField mailEmp;
    private JTextField salEmp;
    private JLabel currentUser;
    private JButton confBtn;
    Connection con;
    PreparedStatement pst;


    //Constructeur
    public Employe() {
        connect();
        AjouterEmploye();
        Rechercher();
        Supprimer();
        Modifier();
        Confirme();

    }


    //Connection to database
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


    //--------------------------------AJOUTER EMPLOYE----------------------------
    public void AjouterEmploye()
    {
        ajouBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String [] tabpst = new String[3];
                for(int i=0; i<3; i++) {
                    tabpst[i] = PostEmp.getSelectedItem().toString();
                }

                String nom,prenom,adresse,mail,post,password,salaire;
                nom = nomEmp.getText().trim();
                prenom = preEmp.getText().trim();
                adresse = addEmp.getText().trim();
                mail = mailEmp.getText().trim();
                post = tabpst[PostEmp.getSelectedIndex()];
                password = pwdEmp.getPassword().toString().trim();
                salaire = salEmp.getText().trim();

                if(ChampEstVide(nom, prenom, adresse, mail, password, salaire))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    nomEmp.requestFocus();
                }
                else
                {
                    if(!MailEstUnique(mail) || !valideMail(mail))
                    {
                        JOptionPane.showMessageDialog(null, "L'employé Déjà Existe ou  mail invalide");
                        mailEmp.setText("");
                        mailEmp.requestFocus();
                    }
                    else
                    {
                        if(!ChampSalaireEstDouble(salaire))
                        {
                            JOptionPane.showMessageDialog(null, "Verifiez Le salaire !!");
                            salEmp.setText("");
                            salEmp.requestFocus();
                        }
                        else
                        {
                            try
                            {
                                pst = con.prepareStatement("insert into employe (password,nom,prenom,adresse,mail,salaire,post,tentative) values (?,?,?,?,?,?,?,?)");
                                pst.setString(1, password);
                                pst.setString(2, nom);
                                pst.setString(3, prenom);
                                pst.setString(4, adresse);
                                pst.setString(5, mail);
                                pst.setString(6, salaire);
                                pst.setString(7, post);
                                pst.setString(8, "0");
                                pst.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Employé Ajouté !!");
                                Actualiser();
                                nomEmp.setText("");
                                preEmp.setText("");
                                addEmp.setText("");
                                mailEmp.setText("");
                                pwdEmp.setText("");
                                salEmp.setText("");
                                nomEmp.requestFocus();
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


    //----------------------- Rechercher et afficher ------------------------
    public void Rechercher()
    {
        rechBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String rech = inputEmp.getText().trim();
                if(rech.equals(""))
                {
                    Actualiser();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select idemp,nom,prenom,adresse,mail,salaire,post from employe where idemp like '"+rech+"%' or nom like '"+rech+"%'" +
                                "or prenom like '"+rech+"%' or adresse like '"+rech+"%' or salaire like '"+rech+"%' or post like '"+rech+"%'");
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

    //---------------------SUPPRIMER EMPLOYE----------------------------------
    public void Supprimer()//les admins ne peuvent pas etre supprimés
    {
        SuppBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=inputEmp.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Supprimer");
                    inputEmp.setText("");
                    inputEmp.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("delete from employe where idemp = ?");
                        pst.setString(1, id);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Employé Supprimé !!");
                        Actualiser();
                        inputEmp.setText("");
                        inputEmp.requestFocus();
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
            pst = con.prepareStatement("select idemp from employe");
            ResultSet rs = pst.executeQuery();

            //empiler tabid avec les id à partir de la base de donnée
            while(rs.next())
            {
                listid.add(rs.getString("idemp"));
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
    //-----------------UPDATE EMPLOYE-------------------------------
    public void Modifier()
    {
        modBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String id=inputEmp.getText();
                if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                {
                    JOptionPane.showMessageDialog(null, "Impossible De Modifier (id invalide)");
                    inputEmp.setText("");
                    inputEmp.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("select nom,prenom,adresse,mail,salaire,post,password from employe where idemp = "+id+";");
                        ResultSet rs = pst.executeQuery();

                        while(rs.next())
                        {
                            nomEmp.setText(rs.getString("nom"));
                            preEmp.setText(rs.getString("prenom"));
                            addEmp.setText(rs.getString("adresse"));
                            mailEmp.setText(rs.getString("mail"));
                            salEmp.setText(rs.getString("salaire"));


                            if(rs.getString("post").equals("Stock"))
                            {
                                PostEmp.setSelectedIndex(0);
                            }

                            if(rs.getString("post").equals("Vendeur"))
                            {
                                PostEmp.setSelectedIndex(1);
                            }

                            if(rs.getString("post").equals("ADMIN"))
                            {
                                PostEmp.setSelectedIndex(2);
                            }
                            pwdEmp.setText(rs.getString("password"));
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
                String [] tabpst = new String[3];
                for(int i=0; i<3; i++) {
                    tabpst[i] = PostEmp.getSelectedItem().toString();
                }

                String nom,prenom,adresse,mail,post,password,salaire, idconf;
                idconf = inputEmp.getText().trim();
                nom = nomEmp.getText().trim();
                prenom = preEmp.getText().trim();
                adresse = addEmp.getText().trim();
                mail = mailEmp.getText().trim();
                post = tabpst[PostEmp.getSelectedIndex()];
                password = pwdEmp.getPassword().toString().trim();
                salaire = salEmp.getText().trim();


                if(ChampEstVide(nom, prenom, adresse, mail, password, salaire))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    nomEmp.requestFocus();
                }
                else
                {
                    try
                    {
                        pst = con.prepareStatement("update employe set nom = ? , prenom = ? , adresse = ? , mail = ? , salaire = ? , post = ? , password = ? where idemp = ? ");

                        pst.setString(1, nom);
                        pst.setString(2, prenom);
                        pst.setString(3, adresse);
                        pst.setString(4, mail);
                        pst.setString(5, salaire);
                        pst.setString(6, post);
                        pst.setString(7, password);
                        pst.setString(8, idconf);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Employé Modifié !!");
                        Actualiser();
                        inputEmp.setText("");
                        inputEmp.requestFocus();

                    }
                    catch (SQLException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
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

    //------------------------- verification champ salaire ----------------------
    public boolean ChampSalaireEstDouble(String salaire)
    {
        boolean b = false ;
        try
        {
            Double.parseDouble(salaire);
            b = true;
        }
        catch(NumberFormatException e)
        {
            b = false;
        }
        return b;
    }


    //-------------------- mail unique et chargement -------------------
    public boolean MailEstUnique(String inputMail)
    {
        ArrayList listmail = new ArrayList();
        try
        {
            pst = con.prepareStatement("select mail from employe");
            ResultSet rs = pst.executeQuery();

            //empiler tabmail avec les mail à partir de la base de donnée
            while(rs.next())
            {
                listmail.add(rs.getString("mail"));
            }
            System.out.println(listmail);
            //table1.setModel(DbUtils.resultSetToTableModel(rs));

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

}
