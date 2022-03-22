import javax.swing.*;
//import com.mysql.cj.xdevapi.Result;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class StartingScreen {

    Connection conn = null;

    public StartingScreen() {
        // connect to sql server
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:db/snp500db.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void refreshStartingScreen() {
        // refresh screen every time there's an update
        ResultSet res = null;
        try {
            // (0) SQLITE GET DATABASE
            this.conn = DriverManager.getConnection("jdbc:sqlite:db/snp500db.db");
            res = this.conn.createStatement().executeQuery("SELECT DISTINCT Ticker FROM EarningsID");
            System.out.println("Connection Established");


            // (0) Initializing overall frame elements
            AutoCompleteDecorator AutoDeco;
            JComboBox combobox;
            JFrame f;
            f = new JFrame("SNP500DB");
            f.setSize(1000,600);//1000 width and 600 height
            f.setLayout(null);//using no layout managers
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setResizable(false);
            // f.setIconImage(new ImageIcon(
            //     getClass().getResource("finance-icon.png")).getImage()); // set icon

            // (1) INPUT LEFT TAB

            JTabbedPane main_tab = new JTabbedPane();
            JPanel panel1, panel2, panel3, panel4;
            panel1 = new JPanel();
            panel2 = new JPanel();
            panel3 = new JPanel();
            panel4 = new JPanel();
            main_tab.setBounds(50, 100, 200, 380);
            main_tab.addTab("Primary", panel1);
            JCheckBox pri_box1, pri_box2, pri_box3, pri_box4, pri_box5;
            // (1.1) Primary Tab: Checkboxes selecting specific financial attributes
            pri_box1 = new JCheckBox("Cash Flow");
            pri_box2 = new JCheckBox("Balance Sheet");
            pri_box3 = new JCheckBox("Income Statement");
            pri_box4 = new JCheckBox("Stock Price");
            pri_box5 = new JCheckBox("Price Target");
            panel1.add(pri_box1);
            panel1.add(pri_box2);
            panel1.add(pri_box3);
            panel1.add(pri_box4);
            panel1.add(pri_box5);

            // (1.2) Secondary Tab: Picking company to compare with

            //
            // TODO: Get company names from query instead of hardcoding
            //
            main_tab.addTab("Secondary", panel2);
            JCheckBox sec_com1, sec_com2, sec_com3, sec_com4, sec_com5, sec_com6;
            sec_com1 = new JCheckBox("Company 1");
            sec_com2 = new JCheckBox("Company 2");
            sec_com3 = new JCheckBox("Company 3");
            sec_com4 = new JCheckBox("Company 4");
            sec_com5 = new JCheckBox("Company 5");
            sec_com6 = new JCheckBox("Company 6");
            panel2.add(sec_com1);
            panel2.add(sec_com2);
            panel2.add(sec_com3);
            panel2.add(sec_com4);
            panel2.add(sec_com5);
            panel2.add(sec_com6);

            // (1.3) Quarter Tab: Pick Q1, Q2, Q3, Q4

            //
            // TODO: Get company names from query instead of hardcoding
            //
            main_tab.addTab("Quarter", panel3);
            JCheckBox q1, q2, q3, q4;
            q1 = new JCheckBox("Q1");
            q2 = new JCheckBox("Q2");
            q3 = new JCheckBox("Q3");
            q4 = new JCheckBox("Q4");
            panel3.add(q1);
            panel3.add(q2);
            panel3.add(q3);
            panel3.add(q4);

            // // (1.4) Year Tab: Pick (DEFUNCT, WILL ONLY USE 2021)
            // main_tab.addTab("Year", panel4);
            // JCheckBox y_2022, y_2021, y_2020, y_2019, y_2018;
            // y_2022 = new JCheckBox("2022");
            // y_2021 = new JCheckBox("2021");
            // y_2020 = new JCheckBox("2020");
            // y_2019 = new JCheckBox("2019");
            // y_2018 = new JCheckBox("2018");
            // panel4.add(y_2022);
            // panel4.add(y_2021);
            // panel4.add(y_2020);
            // panel4.add(y_2019);
            // panel4.add(y_2018);
            // // year checkbox tab

            main_tab.setBackground(Color.lightGray);
            f.add(main_tab);
            
            // SELECT COMPANY: Grab company names from SQL
            ArrayList<String> name = new ArrayList<String>();
            while (res.next()) {
                name.add(res.getString("Ticker"));
                System.out.println(res.getString("Ticker"));
            }
            combobox = new JComboBox(name.toArray());
            AutoCompleteDecorator.decorate(combobox);
            combobox.setBounds(65, 65, 170, 23);
            f.add(combobox, BorderLayout.NORTH);

            // APPLY BUTTON 
            JButton b_apply = new JButton("APPLY");
            b_apply.setBounds(100, 470, 100, 50);//x axis, y axis, width, height
            f.add(b_apply);//adding button in JFrame
            
            // (2) OUTPUT FRAME on the right
            JPanel sql_result = new JPanel();
            sql_result.setBounds(325, 100, 600, 400);
            sql_result.add(new JLabel("Graph to be implemented"));
            sql_result.setBackground(Color.white);
            f.add(sql_result);

            // (3) (?) info button to get description of app
            JButton infoButton = new JButton("?");
            infoButton.setBounds(970, 540, 20, 20);//x axis, y axis, width, height
            infoButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JOptionPane.showMessageDialog(f, "SNP500DB is a CRUD application for investors providing financial information of top 25 companies (by market-cap) in the S&P 500 in 2020-2021.");
                }
            });

            f.add(infoButton);//adding button in JFrame
            
            f.setVisible(true);//making the frame visible

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (this.conn != null) {
                    this.conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}