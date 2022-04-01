package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.TimerTask;

import static java.lang.Integer.*;

public class Entree {
    private JPanel MainEntree;
    private JTable table1;
    private JTextField qtentree;
    private JTextField datentree;
    private JButton ajoutBtn;
    private JComboBox idpieceentree;
    private JComboBox idfourentree;
    private JButton actBtn;
    private JButton retBtn;
    private JLabel currentEmp;



    private String indexFour;
    public String getIndexFour()
    {
        return indexFour;
    }
    public void setIndexFour(String indexFour)
    {
        this.indexFour = indexFour;
    }


    private String indexPiece;
    public String getIndexPiece()
    {
        return indexPiece;
    }
    public void setIndexPiece(String indexPiece)
    {
        this.indexPiece = indexPiece;
    }


    Connection con;
    PreparedStatement pst,pst1;
    JFrame frameEnt;



    public Entree(String empid)
    {
        frameEnt = new JFrame("Entree");
        frameEnt.setContentPane(MainEntree);
        frameEnt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameEnt.pack();
        frameEnt.setLocationRelativeTo(null);
        frameEnt.setResizable(false);
        frameEnt.setVisible(true);
        datentree.setEditable(false);

        //gggg

        setCurrentUser(empid);
        connect();
        setListeDeroulantefour();
        setListeDeroulantepiece();
        datentree.setText(dateentree().toString());
        IdFourList();
        IdPieceList();

        actualiser();
        AjouterEntree();// 2 cas : entrée fausse ou valide
        //AjoutVersPiece()<=>update table piece (qte) : pour chaque entrée si TTL dépassé ou si entrée modifiée par admin : on fait update dans piece, sinon on fait rien
        //setTTL(parametre : identree) dans chaque AjouterEntree
        RetourMainListChoix();


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
            pst = con.prepareStatement("select identree,idemp,idfour,idpiece,qte,date,montant from entree");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //-------------- set current empid -------------------
    public void setCurrentUser(String currentUser)
    {
        System.out.println(currentUser);
        this.currentEmp.setText(currentUser);
    }

    //------------ retour -------------
    public void RetourMainListChoix()
    {
        retBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameEnt.dispose();
                new Login();
            }
        });
    }

    //----------------------- get and set id four in list déroulante --------------------
    public void IdFourList()
    {
        setIndexFour(idfourentree.getItemAt(0).toString());
        idfourentree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.getSource();
                String s = (String) idfourentree.getSelectedItem();
                setIndexFour(s);
            }
        });
    }

    //----------------------- get and set id piece in list déroulante --------------------
    public void IdPieceList()
    {
        setIndexPiece(idpieceentree.getItemAt(0).toString());
        idpieceentree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.getSource();
                String s = (String) idpieceentree.getSelectedItem();
                setIndexPiece(s);
            }
        });
    }


    //============================================================================


    //--------------------------actu-----------------------------
    public void actualiser()
    {
        actBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Actualiser();
            }
        });
    }

    //------------------------- ajout entree ----------------------------
    public void AjouterEntree()
    {
        ajoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String qte;
                qte = qtentree.getText().trim();

                if(ChampEstVide(qte))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    qtentree.requestFocus();
                }
                else
                {
                    if(ChampsIdEstInt(qte)==false)
                    {
                        JOptionPane.showMessageDialog(null, "quantité invalide");
                        qtentree.setText("");
                        qtentree.requestFocus();
                    }
                    else
                    {
                        if(QteSupOuEgaleZero(qte) == false)
                        {
                            JOptionPane.showMessageDialog(null, "quantité invalide");
                            qtentree.setText("");
                            qtentree.requestFocus();
                        }
                        else
                        {
                            try
                            {
                                pst1 = con.prepareStatement("select prixunitaire from piece where idpiece ="+getIndexPiece()+";");
                                ResultSet rs1 = pst1.executeQuery();

                                pst = con.prepareStatement("insert into entree (qte,date,montant,idpiece,idfour,idemp) values (?,?,?,?,?,?)");
                                while (rs1.next())
                                {
                                    Double montant1 = (parseInt(qte) * Double.parseDouble(rs1.getString("prixunitaire")));

                                    pst.setString(1, qte);
                                    pst.setString(2, dateentree().toString());
                                    pst.setDouble(3,montant1);
                                    pst.setString(4, getIndexPiece());
                                    pst.setString(5, getIndexFour());
                                    pst.setString(6, currentEmp.getText());//so that the employe doesn't lie about who's adding the entry : auth -> save empid -> insert into entree

                                    pst.executeUpdate();
                                    JOptionPane.showMessageDialog(null, "entrée Ajoutée !!");
                                    Actualiser();
                                    qtentree.setText("");
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

    //------------------------ list déroulante ------------------
    public void setListeDeroulantefour()
    {
        try {
            pst = con.prepareStatement("select idfour from fournisseur");
            ResultSet rs = pst.executeQuery();


            while (rs.next())
            {
                idfourentree.addItem(rs.getString("idfour"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //------------------------ list déroulante ------------------
    public void setListeDeroulantepiece()
    {
        try {
            pst = con.prepareStatement("select idpiece from piece");
            ResultSet rs = pst.executeQuery();


            while (rs.next())
            {
                idpieceentree.addItem(rs.getString("idpiece"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //----------------------------- date ----------------------------
    public Date dateentree() //set it automatic in Entrée and unchangeable in EntreeAdmin
    {
        long millis=System.currentTimeMillis();
        java.sql.Date date =new java.sql.Date(millis);
        return date;
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

    //----------------------------id chiffres-----------------------------
    public boolean ChampsIdEstInt(String champsId)
    {
        boolean b = false ;
        try
        {
            parseInt(champsId);
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
        int qte = parseInt(champsId);
        if(qte >= 0)
        {
            return true;
        }
        return false;
    }

    //-------------------------- SCHEDULE ---------------------
    public void schedule(TimerTask task, Date time)
    {
        
    }

//
}

