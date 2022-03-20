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

    Connection con;
    PreparedStatement pst;


    public Fournisseur() {
        connect();
        Actualiser();
        AjouterFournisseur();
    }
//============================================================= MAIN =================================================
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Fournisseur");
//        frame.setContentPane(new Fournisseur().MainFour);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }




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

    /*



                String nom,adresse,mail,numtel;
                nom = nomEmp.getText().trim();
                prenom = preEmp.getText().trim();
                adresse = addEmp.getText().trim();
                mail = mailEmp.getText().trim();
                post = tabpst[PostEmp.getSelectedIndex()];
                password = pwdEmp.getText().trim();
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
                                pst.setString(1, crypte(password));
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
     */
}
