package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

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
    Connection con;
    PreparedStatement pst;



    //Constructeur
    public Employe() {
        connect();
        AjouterEmploye();
        Rechercher();
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

                if(ChampEstVide(nom,prenom,adresse,mail,password,salaire) == true)
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    nomEmp.requestFocus();
                }
                else
                {
                    if(!MailEstUnique(mail))
                    {
                        JOptionPane.showMessageDialog(null, "L'employé Déjà Existe !!");
                        mailEmp.setText("");
                        mailEmp.requestFocus();
                    }
                    else
                    {
                        if(ChampSalaireEstDouble(salaire) == false)
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


    //--------------- mail unique et chargement ---------------
    public boolean MailEstUnique(String inputMail)
    {
        String [] tabmail = new String[0];
        try
        {
            pst = con.prepareStatement("select mail from employe");
            ResultSet rs = pst.executeQuery();
            int i = 0;
            //empiler tabmail avec les mail à partir de la base de donnée
            while(rs.next())
            {
                tabmail[i] = rs.getString(4);
                i++;
            }
            //table1.setModel(DbUtils.resultSetToTableModel(rs));

            for(int j=0; j<tabmail.length;j++)
            {
                if(inputMail.equals(tabmail[j]))
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


}
