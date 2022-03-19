package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Login {

    public JPanel MainLogin;
    private JTextField idLog;
    private JButton LoginBtn;
    private JTextField pwdLog;
    Connection con;
    PreparedStatement pst;

    JFrame frame ;

    public Login() {
        frame = new JFrame("Login");
        frame.setContentPane(MainLogin);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
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

    //------------------------- auth --------------------------
    public void Authentifier()
    {
        LoginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idLog.getText().trim();
                String pwd = pwdLog.getText().trim();



                if(ChampEstVide(id, pwd) == true)
                {
                    JOptionPane.showMessageDialog(null, "Verifiez vos champs");
                }
                else
                {
                    if(ChampsIdEstInt(id) ==false || IdExist(id) == false)
                    {
                        JOptionPane.showMessageDialog(null, "id invalide ou n'existe pas");
                        idLog.setText("");
                        idLog.requestFocus();
                    }
                    else
                    {
                        try
                        {
                            pst = con.prepareStatement("select idemp, password, post , nom from employe where idemp = "+id+";");
                            ResultSet rs = pst.executeQuery();

                            while(rs.next()) //parcours sur les ids et pass
                            {
                                if(rs.getString("idemp").equals(id) && crypte(pwd).equals(rs.getString("password"))) //les champs valides (auth)
                                {
                                    //===================================== ADMIN ======================================================
                                    if(rs.getString("post").equals("ADMIN")) // get poste employe (admin)
                                    {
                                        try
                                        {
                                            pst = con.prepareStatement("select tentative from employe where idemp = "+id+";");
                                            ResultSet rs1 = pst.executeQuery(); // tentative

                                            while(rs1.next()) //parcous sur les tentatives
                                            {

                                                if(rs1.getString("tentative").equals("0"))
                                                {
                                                    ChangerPwd(id,"ADMIN",rs.getString("nom"));
                                                }
                                                else
                                                {
                                                    JOptionPane.showMessageDialog(null, "Authentifié ADMIN");
                                                    AdminWindow(rs.getString("nom"));
                                                    frame.dispose();
                                                }
                                            }
                                        }
                                        catch (SQLException ex)
                                        {
                                            ex.printStackTrace();
                                        }
                                    }




                                    //========================================== STOCK ==================================================
                                    if(rs.getString("post").equals("Stock")) // get poste employe (STOCK)
                                    {
                                        try
                                        {
                                            pst = con.prepareStatement("select tentative from employe where idemp = "+id+";");
                                            ResultSet rs2 = pst.executeQuery();

                                            while(rs2.next()) //parcous sur les tentatives
                                            {
                                                System.out.println(rs2.getString("tentative"));

                                                if(rs2.getString("tentative").equals("0"))
                                                {
                                                    ChangerPwd(id,"Stock",rs.getString("nom"));
                                                }
                                                else
                                                {
                                                    JOptionPane.showMessageDialog(null, "Authentifié Stock");
                                                    //AdminWindow();
                                                }
                                            }
                                        }
                                        catch (SQLException ex)
                                        {
                                            ex.printStackTrace();
                                        }
                                    }




                                    //=================================== Vendeur =====================================================
                                    if(rs.getString("post").equals("Vendeur")) // get poste employe (admin)
                                    {
                                        try
                                        {
                                            pst = con.prepareStatement("select tentative from employe where idemp = "+id+";");
                                            ResultSet rs3 = pst.executeQuery();

                                            while(rs3.next()) //parcous sur les tentatives
                                            {
                                                if(rs3.getString("tentative").equals("0"))
                                                {
                                                    ChangerPwd(id,"Vendeur",rs.getString("nom"));
                                                }
                                                else
                                                {
                                                    JOptionPane.showMessageDialog(null, "Authentifié Vendeur");
                                                    //AdminWindow();
                                                }
                                            }
                                        }
                                        catch (SQLException ex)
                                        {
                                            ex.printStackTrace();
                                        }
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

    //----------------- cryptage -----------------------------
    public static String crypte(String pass)
    {
        char [] chars = pass.toCharArray(); // hello -> [ 'h', 'e' , .... ]
        String res = "";
        for(char c : chars)
        {
            c+= 5; //avancement avec code ascii (chaque cara avance + 5 )
            res+=c;
        }

        return  res;
    }

    //-------------- Changer mot de passe -------------
    public void ChangerPwd(String id , String post, String CurentUser)
    {
        JFrame f=new JFrame();
        String nvpwd =JOptionPane.showInputDialog(f,"Entrez nouveau mot de passe :");
        if(nvpwd.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Verfiez mot de passe !!");
        }
        else
        {
            try
            {
                pst = con.prepareStatement("update employe set tentative = 1 , password = ? where idemp = ? ");
                pst.setString(1, crypte(nvpwd));
                pst.setString(2, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Mot de passe a été changé");

                //ADMIN window
                if(post.equals("ADMIN"))
                {
                    AdminWindow(CurentUser);
                    frame.dispose();
                }
                // stock window
                if(post.equals("Stock"))
                {
                    //AdminWindow();
                }
                //vendeur window
                if(post.equals("Vendeur"))
                {
                    //AdminWindow();
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    //---------------- ADMIN WINDOW -----------------
    public void AdminWindow(String CurrentUser)
    {
        JPanel emp = new Employe().MainEmploye ;
        JFrame frame = new JFrame("Employe");
        frame.setContentPane(emp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }


    //------------------ Stock WINDOW -----------------------


    //------------------ Vendeur WINDOW ----------------------



}
