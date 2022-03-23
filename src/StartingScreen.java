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
            System.out.println("Connection Established");


            // (0) Initializing overall frame elements
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
            JRadioButton pri_box1, pri_box2, pri_box3, pri_box4;
            // (1.1) Primary Tab: Checkboxes selecting specific financial attributes + quarter
            ButtonGroup G = new ButtonGroup();
            pri_box1 = new JRadioButton("Cash Flow");
            pri_box2 = new JRadioButton("Balance Sheet");
            pri_box3 = new JRadioButton("Income Statement");
            pri_box4 = new JRadioButton("Price Target");
            G.add(pri_box1);
            G.add(pri_box2);
            G.add(pri_box3);
            G.add(pri_box4);
            panel1.add(pri_box1);
            panel1.add(pri_box2);
            panel1.add(pri_box3);
            panel1.add(pri_box4);

            // Q1 Q2 Q3 Q4
            // main_tab.addTab("Quarter", panel3);
            JRadioButton q1, q2, q3, q4;
            // (1.1) Primary Tab: Checkboxes selecting specific financial attributes
            ButtonGroup G2 = new ButtonGroup();
            q1 = new JRadioButton("Q1");
            q2 = new JRadioButton("Q2");
            q3 = new JRadioButton("Q3");
            q4 = new JRadioButton("Q4");
            G2.add(q1);
            G2.add(q2);
            G2.add(q3);
            G2.add(q4);
            panel1.add(new JSeparator(SwingConstants.VERTICAL));
            panel1.add(q1);
            panel1.add(q2);
            panel1.add(q3);
            panel1.add(q4);

            main_tab.setBackground(Color.lightGray);
            f.add(main_tab);
            
            // SELECT COMPANY: Grab company names from SQL
            ArrayList<String> name = new ArrayList<String>();
            
            res = this.conn.createStatement().executeQuery("SELECT DISTINCT Ticker FROM EarningsID");
            while (res.next()) {
                name.add(res.getString("Ticker"));
                System.out.println(res.getString("Ticker"));
            }
            JComboBox companyOne;
            JComboBox companyTwo;
            // AutoCompleteDecorator AutoDeco;
            companyOne = new JComboBox(name.toArray());
            name.add(0,"");
            companyTwo = new JComboBox(name.toArray());
            companyOne.setBounds(55, 65, 170, 23);
            companyTwo.setBounds(245, 65, 170, 23);
            f.add(companyOne, BorderLayout.NORTH);
            f.add(companyTwo, BorderLayout.NORTH);

            // (2) OUTPUT FRAME on the right
            JScrollPane sql_result = new JScrollPane();
            sql_result.setBounds(325, 100, 600, 400);
            sql_result.setBackground(Color.white);
            f.add(sql_result);

            // APPLY BUTTON 
            JButton b_apply = new JButton("APPLY");
            b_apply.setBounds(100, 470, 100, 50);//x axis, y axis, width, height
            f.add(b_apply);//adding button in JFrame
            b_apply.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    // execute apply button
                    sql_result.removeAll();
                    
                    // FINANCIAL CATEGORY
                    String category = "CashFlow";
                    ArrayList<String> categoryColumns = new ArrayList<String>();
                    if (pri_box1.isSelected()) {
                        category = "CashFlow";
                        categoryColumns = new ArrayList<>(Arrays.asList("ID", "NetIncome", "OperatingActivities", "InvestingActivities", "FinancialActivities", "EffectOfExchRateChanges", "NetIncInCashCashEquiv"));
                    }
                    if (pri_box2.isSelected()) {
                        category = "BalanceSheet";
                        categoryColumns = new ArrayList<>(Arrays.asList("ID", "TotalCurrentAssets", "TotalNoncurrentAssets", "TotalAssets", "TotalCurrentLiabilities", "TotalNoncurrentLiabilities", "TotalLiabilities", "EquitiesAttrToParentCompany", "TotalEquities", "TotalLiabilitiesEquities"));
                    }
                    if (pri_box3.isSelected()) {
                        category = "IncomeStatement";
                        categoryColumns = new ArrayList<>(Arrays.asList("ID", "TotalRevenue", "GrossProfit", "OperatingIncome", "NetIncomePreTax", "NetIncome", "DilutedEPS"));
                    }
                    if (pri_box4.isSelected()) {
                        category = "PriceTarget";
                        categoryColumns = new ArrayList<>(Arrays.asList());
                    }
                    // QUARTER
                    String quarter = "Q1";
                    if (q1.isSelected()) {quarter = "Q1";}
                    if (q2.isSelected()) {quarter = "Q2";}
                    if (q3.isSelected()) {quarter = "Q3";}
                    if (q4.isSelected()) {quarter = "Q4";}
                    
                    String[][] data_as_array;
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    try {
                        Connection conn = DriverManager.getConnection("jdbc:sqlite:db/snp500db.db");
                        ResultSet res = conn.createStatement().executeQuery(String.format("SELECT * FROM %s c JOIN EarningsID e ON c.ID=e.ID WHERE e.Quarter='%s' AND e.Ticker IN ('%s', '%s');",category,quarter,companyOne.getSelectedItem(),companyTwo.getSelectedItem()));
                        
                        while (res.next()) {
                            ArrayList<String> data_one_col = new ArrayList<String>();
                            for (String column:categoryColumns) {
                                data_one_col.add(res.getString(column));
                            }
                            data.add(data_one_col);
                        }
                        data_as_array = new String[data.size()][];
                        for (int i = 0; i < data.size(); i++) {
                            ArrayList<String> row = data.get(i);
                            data_as_array[i] = row.toArray(new String[row.size()]);
                        }

                        // update result display
                        JTable table = new JTable(data_as_array, categoryColumns.toArray());
                        f.remove(sql_result);
                        JScrollPane sql_result = new JScrollPane(table);
                        sql_result.setBounds(325, 100, 600, 400);
                        sql_result.setBackground(Color.white);
                        f.add(sql_result);
                        f.add(new JSeparator(SwingConstants.VERTICAL)); // add separator in between table(top) and graph(bottom) for display

                    table.setFillsViewportHeight(true);

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });

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