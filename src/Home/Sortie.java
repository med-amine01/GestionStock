package Home;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static java.lang.Integer.parseInt;

public class Sortie {
    private JPanel MainSortie;
    private JTextField qtsortie;
    private JTextField datesortie;
    private JButton ajoutBtn;
    private JTable table1;
    private JButton actBtn;
    private JComboBox idpiecesortie;
    private JButton retBtn;
    private JLabel currentEmp;


    private String indexPiece;
    public String getIndexPiece()
    {
        return indexPiece;
    }
    public void setIndexPiece(String indexPiece) {
        this.indexPiece = indexPiece;
    }

    Connection con;
    PreparedStatement pst,pst1;
    JFrame frameSortie;

    public Sortie(String vendid)
    {
        frameSortie = new JFrame("Sortie");
        frameSortie.setContentPane(MainSortie);
        frameSortie.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameSortie.setLocationRelativeTo(null);
        frameSortie.setResizable(false);
        frameSortie.pack();
        frameSortie.setVisible(true);
        datesortie.setEditable(false);

        setCurrentUser(vendid);
        connect();
        setListeDeroulantepiece();
        datesortie.setText(datesortie().toString());
        IdPieceList();
        actualiser();
        AjouterSortie();
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
            pst = con.prepareStatement("select idsortie,idpiece,qte,date,montant from sortie");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
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

    //------------------------- ajout sortie ----------------------------
    public void AjouterSortie()
    {
        ajoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String qte;
                qte = qtsortie.getText().trim();


                if(ChampEstVide(qte))
                {
                    JOptionPane.showMessageDialog(null, "Verifiez Les Champs !!");
                    qtsortie.requestFocus();
                }
                else
                {
                    if(ChampsIdEstInt(qte)==false)
                    {
                        JOptionPane.showMessageDialog(null, "quantité invalide");
                        qtsortie.setText("");
                        qtsortie.requestFocus();
                    }
                    else
                    {
                        if(QteSupOuEgaleZero(qte) == false)
                        {
                            JOptionPane.showMessageDialog(null, "quantité invalide");
                            qtsortie.setText("");
                            qtsortie.requestFocus();
                        }
                        else
                        {
                            try
                            {
                                pst1 = con.prepareStatement("select prixunitaire from piece where idpiece ="+getIndexPiece()+";");
                                ResultSet rs1 = pst1.executeQuery();

                                while (rs1.next())
                                {
                                    Double montant1 = (parseInt(qte) * Double.parseDouble(rs1.getString("prixunitaire")));

                                    pst = con.prepareStatement("insert into sortie (qte,date,montant,idpiece,idemp) values (?,?,?,?,?)");
                                    pst.setString(1, qte);
                                    pst.setString(2, datesortie().toString());
                                    pst.setDouble(3, montant1);
                                    pst.setString(4, getIndexPiece());
                                    pst.setString(5, currentEmp.getText());//so that the venddeur doesn't lie about who's adding the entry : auth -> save empid -> insert into entree

                                    pst.executeUpdate();
                                    JOptionPane.showMessageDialog(null, "Sortie Ajoutée !!");
                                    Actualiser();
                                    qtsortie.setText("");
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
                frameSortie.dispose();
                new Login();
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

    //------------------------ list déroulante ------------------
    public void setListeDeroulantepiece()
    {
        try {
            pst = con.prepareStatement("select idpiece from piece");
            ResultSet rs = pst.executeQuery();


            while (rs.next())
            {
                idpiecesortie.addItem(rs.getString("idpiece"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //----------------------------- date ----------------------------
    public Date datesortie() //set it automatic in Entrée and unchangeable in EntreeAdmin
    {
        long millis=System.currentTimeMillis();
        java.sql.Date date =new java.sql.Date(millis);
        return date;
    }

    //----------------------- get and set id piece in list déroulante --------------------
    public void IdPieceList()
    {
        setIndexPiece(idpiecesortie.getItemAt(0).toString());
        idpiecesortie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.getSource();
                String s = (String) idpiecesortie.getSelectedItem();
                setIndexPiece(s);
            }
        });
    }
    //
}
