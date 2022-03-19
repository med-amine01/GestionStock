package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Login {
    public JPanel MainLogin;
    private JTextField idLog;
    private JButton LoginBtn;
    private JPasswordField pwdLog;
    Connection con;
    PreparedStatement pst;

    public Login() {
        connect();
        Authentifier();
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

    //-------------------------auth--------------------------
    public void Authentifier()
    {
        LoginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idLog.getText().trim();
                String pwd = pwdLog.getPassword().toString();


                if(ChampEstVide(id,pwd) == true)
                {
                    JOptionPane.showMessageDialog(null, "Verifiez vos champs");
                }
                else
                {
                    if(ChampsIdEstInt(id)==false || IdExist(id)==false)
                    {
                        JOptionPane.showMessageDialog(null, "id invalide ou n'existe pas");
                        idLog.setText("");
                        idLog.requestFocus();
                    }
                    else
                    {
                        try
                        {
                            pst = con.prepareStatement("select idemp,password, post from employe where idemp = "+id+";");
                            ResultSet rs = pst.executeQuery();

                            while(rs.next())
                            {
                                System.out.println("id "+ rs.getString("idemp"));
                                System.out.println("pass "+ rs.getString("password"));
                                System.out.println("poste "+ rs.getString("post"));

                                if(rs.getString("idemp").equals(id) && rs.getString("password").equals(pwd))
                                {
                                    if(rs.getString("post").equals("ADMIN"))
                                    {
                                        JOptionPane.showMessageDialog(null, "Authentifié ADMIN");
                                    }
                                    if(rs.getString("post").equals("Stock"))
                                    {
                                        JOptionPane.showMessageDialog(null, "Authentifié Stock");
                                    }
                                    if(rs.getString("post").equals("Vendeur"))
                                    {
                                        JOptionPane.showMessageDialog(null, "Authentifié vendeur");
                                    }
                                }
                                else
                                {
                                    JOptionPane.showMessageDialog(null, "ID ou Mot de passe ne correspond pas");
                                }
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
