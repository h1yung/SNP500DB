import javax.swing.*;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import java.awt.*;
import java.sql.*;
import java.util.*;

// TODO
// connect CRUD to procedures
// - stored procedures 
// - prepared statement, orm
// automatically reorder sql
// DISPLAY GRAPHS https://docs.oracle.com/javafx/2/charts/pie-chart.htm

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

    JComboBox financialSubcategory;
    JScrollPane sql_result; // output frame on the right
    JComboBox companyOne;
    JComboBox companyTwo;
    
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

            // (1.2) SELECT COMPANY: Grab company names from SQL
            ArrayList<String> name = new ArrayList<String>();
            
            res = this.conn.createStatement().executeQuery("SELECT DISTINCT Ticker FROM EarningsID;");
            while (res.next()) {
                name.add(res.getString("Ticker"));
            }

            companyOne = new JComboBox(name.toArray());
            name.add(0,"");
            companyTwo = new JComboBox(name.toArray());
            companyOne.setBounds(55, 45, 170, 23);
            companyTwo.setBounds(245, 45, 170, 23);
            f.add(companyOne, BorderLayout.NORTH);
            f.add(companyTwo, BorderLayout.NORTH);

            // (1.2.2) Button with option of comparing all companies at the same time
            JRadioButton allCompaniesCheck = new JRadioButton("All Companies");
            JPanel allCompaniesCheckBoxPanel = new JPanel();
            allCompaniesCheckBoxPanel.setBounds(215,65,200,30);
            allCompaniesCheckBoxPanel.add(allCompaniesCheck);
            f.add(allCompaniesCheckBoxPanel);

            // (2) OUTPUT FRAME on the right
            sql_result = new JScrollPane();
            sql_result.setBounds(325, 100, 600, 400);
            sql_result.setBackground(Color.white);
            f.add(sql_result);
            // (2.1) BUTTONS UNDER OUTPUT FRAME selecting:
            // - allColumns, income, asset, ...
            // - default, barGraph, dotGraph, LineGraph, circleGraph
            financialSubcategory = new JComboBox(new String[] {""}); // this will be filled with options once information queried for the first time
            JComboBox displayType = new JComboBox(new String[] {"Default", "Bar Graph", "Dot Graph", "Line Graph", "Pie Graph"});
            financialSubcategory.setBounds(550, 520, 170, 23);
            displayType.setBounds(750, 520, 170, 23);
            f.add(financialSubcategory, BorderLayout.NORTH);
            f.add(displayType, BorderLayout.NORTH);

            // (3) APPLY BUTTON 
            JButton b_apply = new JButton("APPLY");
            b_apply.setBounds(50, 470, 100, 50);//x axis, y axis, width, height
            f.add(b_apply);//adding button in JFrame
            b_apply.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    // execute apply button
                
                    // SELECTING: FINANCIAL CATEGORY
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
                        category = "PriceTargets";
                        categoryColumns = new ArrayList<>(Arrays.asList("ID", "Price"));
                    }

                    // SELECTING: QUARTER
                    String quarter = "Q1";
                    if (q1.isSelected()) {quarter = "Q1";}
                    if (q2.isSelected()) {quarter = "Q2";}
                    if (q3.isSelected()) {quarter = "Q3";}
                    if (q4.isSelected()) {quarter = "Q4";}

                    // SELECTING: financialSubcategory and displayType
                    // int subcatIndex = financialSubcategory.getSelectedIndex();
                    // int dispTypeIndex = displayType.getSelectedIndex();

                    // populate financial subcategory combobox
                    f.remove(financialSubcategory);

                    ArrayList<String> financialSubcategoryArray = (ArrayList)categoryColumns.clone(); // list of financial subcategories for JComboBox

                    ArrayList<ArrayList<String>> data = new ArrayList<>();

                    Connection conn = null;
                    try {
                        conn = DriverManager.getConnection("jdbc:sqlite:db/snp500db.db");
                        ResultSet res = conn.createStatement().executeQuery(String.format("SELECT * FROM %s c JOIN EarningsID e ON c.ID=e.ID WHERE e.Quarter='%s' AND e.Ticker IN ('%s', '%s');",category,quarter,companyOne.getSelectedItem(),companyTwo.getSelectedItem()));
                        if (allCompaniesCheck.isSelected()) {
                            res = conn.createStatement().executeQuery(String.format("SELECT * FROM %s c JOIN EarningsID e ON c.ID=e.ID WHERE e.Quarter='%s' AND e.Ticker IN (SELECT DISTINCT Ticker FROM EarningsID);",category,quarter));
                        }
                        
                        while (res.next()) {
                            ArrayList<String> data_one_col = new ArrayList<String>();
                            // if we select anything other than "" as financial subcategory, return only that subcategory
                            if (!financialSubcategory.getSelectedItem().equals("")) { // if "" (i.e. all subcategories) not selected
                                
                                data_one_col.add(res.getString("ID"));
                                data_one_col.add(res.getString(financialSubcategory.getSelectedItem().toString())); // subcategory row
                            // if we select "", return every subcategory
                            } else {
                                for (String column:categoryColumns) {
                                    if (column == "ID") {
                                        ResultSet res_1 = conn.createStatement().executeQuery(String.format("SELECT Ticker FROM EarningsID WHERE ID=%s;",res.getString("id")));
                                        data_one_col.add(res_1.getString("Ticker"));
                                    } else {
                                        data_one_col.add(res.getString(column));
                                    }
                                }
                            }
                            data.add(data_one_col);
                        }
                        

                        financialSubcategoryArray.set(0, ""); // add "" to beginning of the list to select for all categories
                        financialSubcategory = new JComboBox(financialSubcategoryArray.toArray()); // filled with options once information queried for the first time
                        financialSubcategory.setBounds(550, 520, 170, 23);
                        f.add(financialSubcategory, BorderLayout.NORTH);

                        f.remove(sql_result);
                        sql_result = new JScrollPane(); //JScrollPane sql_result = new JScrollPane(table);

                        sql_result.setBounds(325, 100, 600, 400);
                        sql_result.setBackground(Color.white);
                        
                        // update result display
                        JPanel table_panel = new JPanel(new GridLayout(0,1));

                        for (int i = 0; i < data.get(0).size(); i++) {
                            JPanel panel_row = new JPanel(new GridLayout(0,3)); // row contains finance category, company1's metric, company2's metric
                            JPanel category_panel = new JPanel(); // (1) finance category
                            category_panel.add(new JLabel(categoryColumns.get(i))); // place the cells with subcategory labels first
                            panel_row.add(category_panel);
                            panel_row.setSize(10,10);
                            if (data.size() == 1) { // only result for 1 company 
                                JPanel company1 = new JPanel();
                                company1.setSize(5,5);
                                company1.setBackground(Color.green); 
                                company1.add(new JLabel(data.get(0).get(i).toString()));
                                panel_row.add(company1);
                            }
                            if (data.size() == 2) { // result for 2 companies
                                JPanel company1 = new JPanel();
                                company1.setSize(5,5);
                                company1.add(new JLabel(data.get(0).get(i).toString()));
                                JPanel company2 = new JPanel();
                                company2.setSize(5,5);
                                company2.add(new JLabel(data.get(1).get(i).toString()));
                                
                                try {
                                    if (Float.parseFloat(data.get(0).get(i)) > Float.parseFloat(data.get(1).get(i))) {
                                        company1.setBackground(Color.green); 
                                        company2.setBackground(Color.lightGray); 
                                    } else {
                                        company1.setBackground(Color.lightGray); 
                                        company2.setBackground(Color.green); 
                                    }
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                

                                panel_row.add(company1);
                                panel_row.add(company2);
                            }
                            if (data.size() > 2) { // result for all companies 
                                //(TODO: implement green cell for best metric like above)

                                panel_row = new JPanel(new GridLayout(0,1+data.size())); // remake grid to accomodate 25 companies instead of 2
                                panel_row.add(category_panel); // again, place the cells with subcategory labels first
                                panel_row.setSize(10,10);
                                for (int k=0 ; k < data.size() ; k++) {
                                    JPanel company = new JPanel();
                                    company.setSize(5,5);
                                    company.add(new JLabel(data.get(k).get(i).toString()));
                                    panel_row.add(company);
                                }
                            }
                            table_panel.add(panel_row);
                        }

                        sql_result.getViewport().add(table_panel);
                        f.add(sql_result);

                        sql_result.revalidate();
                        sql_result.repaint();

                        f.revalidate();
                        f.repaint();
                    } catch (Exception e) {
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
            });
            
            // (3) ADD/DELETE BUTTON 
            final JLabel label = new JLabel();
            JButton b_add_delete = new JButton("UPDATE");
            b_add_delete.setBounds(150, 470, 100, 50);//x axis, y axis, width, height
            f.add(b_add_delete);//adding button in JFrame
            b_add_delete.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    // execute apply button
                    Object[] options = {"add/update", "delete"};
                    int x = JOptionPane.showOptionDialog(null, "Choose an operation.", "CRUD", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                    
                    String ticker, quarter, year;
                    double totalRevenue, grossProfit, operatingIncome, netIncomePreTax, netIncome, dilutedEPS;
                    double totalCurrentAssets, totalNoncurrentAssets, totalAssets, totalCurrentLiabilities, totalNoncurrentLiabilities, totalLiabilities, equitiesAttrToParentCompany, totalEquities, totalLiabilitiesEquities;
                    double /*netIncome*/ operatingActivities, investingActivities, financialActivities, effectOfExchRateChanges, netIncInCashCashEquiv;
                    double price;
                    
                    JTextField tickerField = new JTextField();
                    JComboBox quarterField = new JComboBox(new String[] {"Q1","Q2","Q3","Q4"});
                    JComboBox yearField = new JComboBox(new String[] {"2020", "2021"});
                    switch (x) {
                        case 0: // add
                            JTextField totalRevenueField = new JTextField("-1");
                            JTextField grossProfitField = new JTextField("-1");
                            JTextField operatingIncomeField = new JTextField("-1");
                            JTextField netIncomePreTaxField = new JTextField("-1");
                            JTextField netIncomeField = new JTextField("-1");
                            JTextField dilutedEPSField = new JTextField("-1");

                            JTextField totalCurrentAssetsField = new JTextField("-1");
                            JTextField totalNoncurrentAssetsField = new JTextField("-1");
                            JTextField totalAssetsField = new JTextField("-1");
                            JTextField totalCurrentLiabilitiesField = new JTextField("-1"); 
                            JTextField totalNoncurrentLiabilitiesField = new JTextField("-1");
                            JTextField totalLiabilitiesField = new JTextField("-1");
                            JTextField equitiesAttrToParentCompanyField = new JTextField("-1");
                            JTextField totalEquitiesField = new JTextField("-1");
                            JTextField totalLiabilitiesEquitiesField = new JTextField("-1");

                            JTextField operatingActivitiesField = new JTextField("-1");
                            JTextField investingActivitiesField = new JTextField("-1");
                            JTextField financialActivitiesField = new JTextField("-1");
                            JTextField effectOfExchRateChangesField = new JTextField("-1");
                            JTextField netIncInCashCashEquivField = new JTextField("-1");

                            JTextField priceField = new JTextField("-1");
                            
                            GridLayout gridForOptionPane = new GridLayout(6,6);
                            
                            Object[] first_list = {
                                "Ticker:", tickerField,
                                "Quarter:", quarterField,
                                "Year:", yearField
                            };
                            Object[] second_list = {
                                "Total Revenue:", totalRevenueField,
                                "Gross Profit:", grossProfitField,
                                "Operating Income:", operatingIncomeField,
                                "Net Income Pre-Tax:", netIncomePreTaxField,
                                "Net Income:", netIncomeField,
                                "Diluted EPS:", dilutedEPSField
                            };
                            Object[] third_list = {
                                "Total Current Assets:", totalCurrentAssetsField,
                                "Total Noncurrent Assets:", totalNoncurrentAssetsField,
                                "Total Assets:", totalAssetsField,
                                "Total Current Liabilities:", totalCurrentLiabilitiesField,
                                "Total Noncurrent Liabilities:", totalNoncurrentLiabilitiesField,
                                "Total Liabilities:", totalLiabilitiesField,
                                "Equities Attributable To Parent Company:", equitiesAttrToParentCompanyField,
                                "Total Equities:", totalEquitiesField,
                                "Total Liabilities Equities:", totalLiabilitiesEquitiesField
                            };
                            Object[] fourth_list = {
                                "Operating Activities:", operatingActivitiesField,
                                "Investing Activities:", investingActivitiesField,
                                "Financial Activities:", financialActivitiesField,
                                "Effect Of Exchange Rate Changes:", effectOfExchRateChangesField,
                                "Net Income In Cash Equivalent:", netIncInCashCashEquivField
                            };
                            Object[] fifth_list = {
                                "Price Target:", priceField
                            };
                            
                            int first = JOptionPane.showConfirmDialog(null, first_list, "General Information", JOptionPane.OK_CANCEL_OPTION);
                            if (first == JOptionPane.OK_OPTION)
                            {
                                ticker = tickerField.getText();
                                quarter = quarterField.getSelectedItem().toString();
                                year = yearField.getSelectedItem().toString();
                            } else {
                                break;
                            }

                            int second = JOptionPane.showConfirmDialog(null, second_list, "Income Statement", JOptionPane.OK_CANCEL_OPTION);
                            if (second == JOptionPane.OK_OPTION)
                            {
                                totalRevenue = Double.valueOf(totalRevenueField.getText());
                                grossProfit = Double.valueOf(grossProfitField.getText());
                                operatingIncome = Double.valueOf(operatingIncomeField.getText());
                                netIncomePreTax = Double.valueOf(netIncomePreTaxField.getText());
                                netIncome = Double.valueOf(netIncomeField.getText());
                                dilutedEPS = Double.valueOf(dilutedEPSField.getText());
                            } else {
                                break;
                            }

                            int third = JOptionPane.showConfirmDialog(null, third_list, "Balance Sheet", JOptionPane.OK_CANCEL_OPTION);
                            if (third == JOptionPane.OK_OPTION)
                            {
                                totalCurrentAssets = Double.valueOf(totalCurrentAssetsField.getText());
                                totalNoncurrentAssets = Double.valueOf(totalNoncurrentAssetsField.getText());
                                totalAssets = Double.valueOf(totalAssetsField.getText());
                                totalCurrentLiabilities = Double.valueOf(totalCurrentLiabilitiesField.getText());
                                totalNoncurrentLiabilities = Double.valueOf(totalNoncurrentLiabilitiesField.getText());
                                totalLiabilities = Double.valueOf(totalLiabilitiesField.getText());
                                equitiesAttrToParentCompany = Double.valueOf(equitiesAttrToParentCompanyField.getText());
                                totalEquities = Double.valueOf(totalEquitiesField.getText());
                                totalLiabilitiesEquities = Double.valueOf(totalLiabilitiesEquitiesField.getText()); 
                            }
                            else {
                                break;
                            }

                            int fourth = JOptionPane.showConfirmDialog(null, fourth_list, "Cash Flow", JOptionPane.OK_CANCEL_OPTION);
                            if (fourth == JOptionPane.OK_OPTION)
                            {
                                operatingActivities = Double.valueOf(operatingActivitiesField.getText());
                                investingActivities = Double.valueOf(investingActivitiesField.getText());
                                financialActivities = Double.valueOf(financialActivitiesField.getText());
                                effectOfExchRateChanges = Double.valueOf(effectOfExchRateChangesField.getText());
                                netIncInCashCashEquiv = Double.valueOf(netIncInCashCashEquivField.getText());
                            }
                            else {
                                break;
                            }

                            int fifth = JOptionPane.showConfirmDialog(null, fifth_list, "Price Target", JOptionPane.OK_CANCEL_OPTION);
                            if (fifth == JOptionPane.OK_OPTION)
                            {
                                price = Double.parseDouble(priceField.getText());
                            }
                            else {
                                break;
                            }
                            
                            // TODO: add if doesn't exist already. otherwise, update
                            ArrayList<String> ticker_list = new ArrayList<String>();
                            Connection connection = null;
                            try {
                                connection = DriverManager.getConnection("jdbc:sqlite:db/snp500db.db");
                                ResultSet distinct_tickers = connection.createStatement().executeQuery("SELECT DISTINCT Ticker FROM EarningsID");
                                ResultSet last_id = connection.createStatement().executeQuery("SELECT MAX(ID) FROM EarningsID");
                                String temp = String.format("SELECT ID FROM EarningsID WHERE Ticker='%s'", ticker);
                                ResultSet ticker_id = connection.createStatement().executeQuery(temp);

                                while (distinct_tickers.next()) {
                                    ticker_list.add(distinct_tickers.getString("Ticker"));
                                }
                                if (name.contains(ticker)) {
                                    String sql = String.format("UPDATE EarningsID SET Quarter=%s, Year=%s WHERE Ticker=%s", quarter, year, ticker);
                                    connection.createStatement().executeUpdate(sql);

                                    sql = String.format("UPDATE IncomeStatement SET TotalRevenue=%f, GrossProfit=%f, OperatingIncome=%f, NetIncomePreTax=%f, NetIncome=%f, DilutedEPS=%f WHERE ID=%s", 
                                    totalRevenue, grossProfit, operatingIncome, netIncomePreTax, netIncome, dilutedEPS, ticker_id);
                                    connection.createStatement().executeUpdate(sql);

                                    sql = String.format("UPDATE BalanceSheet SET TotalCurrentAssets=%f, TotalNoncurrentAssets=%f, TotalAssets=%f, TotalCurrentLiabilities=%f, TotalNoncurrentLiabilities=%f, TotalLiabilities=%f, EquitiesAttrToParentCompany=%f, TotalEquities=%f, TotalLiabilitiesEquities=%f WHERE Ticker=%s", 
                                    totalCurrentAssets, totalNoncurrentAssets, totalAssets, totalCurrentLiabilities, totalNoncurrentLiabilities, totalLiabilities, equitiesAttrToParentCompany, totalEquities, totalLiabilitiesEquities, ticker_id);
                                    connection.createStatement().executeUpdate(sql);

                                    sql = String.format("UPDATE CashFlow SET NetIncome=%f, OperatingActivities=%f, InvestingActivities=%f, FinancialActivities=%f, EffectOfExchRateChanges=%f, NetIncInCashCashEquiv=%f WHERE Ticker=%s", 
                                    netIncome, operatingActivities, investingActivities, financialActivities, effectOfExchRateChanges, netIncInCashCashEquiv, ticker_id);
                                    connection.createStatement().executeUpdate(sql);

                                    sql = String.format("UPDATE PriceTargets SET Price=%f WHERE Ticker=%s", 
                                    price, ticker_id);
                                    connection.createStatement().executeUpdate(sql);

                                    connection.close();
                                } else {

                                    Integer next_id = Integer.parseInt(last_id.getString(1));
                                    String sql = String.format("INSERT INTO EarningsID (ID, Quarter, Year, Ticker) VALUES (%s, '%s', %s, '%s')", ((Integer)(next_id + 1)).toString(), quarter, year, ticker);
                                    connection.createStatement().executeUpdate(sql);

                                    sql = String.format("INSERT INTO IncomeStatement (ID, TotalRevenue, GrossProfit, OperatingIncome, NetIncomePreTax, NetIncome, DilutedEPS) VALUES (%s, %f, %f, %f, %f, %f, %f)", 
                                    ((Integer)(next_id + 1)).toString(), totalRevenue, grossProfit, operatingIncome, netIncomePreTax, netIncome, dilutedEPS);
                                    connection.createStatement().executeUpdate(sql);
                                    
                                    sql = String.format("INSERT INTO BalanceSheet (ID, TotalCurrentAssets, TotalNoncurrentAssets, TotalAssets, TotalCurrentLiabilities, TotalNoncurrentLiabilities, TotalLiabilities, EquitiesAttrToParentCompany, TotalEquities, TotalLiabilitiesEquities) VALUES (%s, %f, %f, %f, %f, %f, %f, %f, %f, %f)", 
                                    ((Integer)(next_id + 1)).toString(), totalCurrentAssets, totalNoncurrentAssets, totalAssets, totalCurrentLiabilities, totalNoncurrentLiabilities, totalLiabilities, equitiesAttrToParentCompany, totalEquities, totalLiabilitiesEquities);
                                    connection.createStatement().executeUpdate(sql);

                                    sql = String.format("INSERT INTO CashFlow (ID, NetIncome, OperatingActivities, InvestingActivities, FinancialActivities, EffectOfExchRateChanges, NetIncInCashCashEquiv) VALUES (%s, %f, %f, %f, %f, %f, %f)", 
                                    ((Integer)(next_id + 1)).toString(), netIncome, operatingActivities, investingActivities, financialActivities, effectOfExchRateChanges, netIncInCashCashEquiv);
                                    connection.createStatement().executeUpdate(sql);

                                    sql = String.format("INSERT INTO PriceTargets (ID, Price) VALUES (%s, %f)", 
                                    ((Integer)(next_id + 1)).toString(), price);
                                    connection.createStatement().executeUpdate(sql);
                                    
                                    companyOne.addItem(ticker);
                                    companyTwo.addItem(ticker);

                                    connection.close();
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            } finally {
                                try {
                                    if (connection != null) {
                                        connection.close();
                                    }
                                } catch (SQLException ex) {
                                    System.out.println(ex.getMessage());
                                }
                            }
                            break;

                        case 1: // delete
                            Object[] delInputs = {
                                "Ticker:", tickerField,
                                "Quarter:", quarterField,
                                "Year:", yearField
                            };
                            int delOption = JOptionPane.showConfirmDialog(null, delInputs, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);
                            if (delOption == JOptionPane.OK_OPTION) {
                                ticker = tickerField.getText();
                                quarter = quarterField.getSelectedItem().toString();
                                year = yearField.getSelectedItem().toString();

                                // TODO: delete if exists.
                                Connection connection_2 = null;
                                try {
                                    connection_2 = DriverManager.getConnection("jdbc:sqlite:db/snp500db.db");
                                    String temp = String.format("SELECT ID FROM EarningsID WHERE Ticker='%s'", ticker);
                                    ResultSet ticker_id = connection_2.createStatement().executeQuery(temp);

                                    if (name.contains(ticker)) {
                                        String sql = String.format("DELETE FROM EarningsID WHERE ID=%s",
                                        ticker_id.getString("ID"));
                                        connection_2.createStatement().executeUpdate(sql);

                                        sql = String.format("DELETE FROM IncomeStatement WHERE ID=%s", 
                                        ticker_id.getString("ID"));
                                        connection_2.createStatement().executeUpdate(sql);

                                        sql = String.format("DELETE FROM BalanceSheet WHERE ID=%s", 
                                        ticker_id.getString("ID"));
                                        connection_2.createStatement().executeUpdate(sql);

                                        sql = String.format("DELETE FROM CashFlow WHERE ID=%s", 
                                        ticker_id.getString("ID"));
                                        connection_2.createStatement().executeUpdate(sql);

                                        sql = String.format("DELETE FROM PriceTargets WHERE ID=%s", 
                                        ticker_id.getString("ID"));
                                        connection_2.createStatement().executeUpdate(sql);

                                        connection_2.close();
                                    }
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                } finally {
                                    try {
                                        if (connection_2 != null) {
                                            connection_2.close();
                                        }
                                    } catch (SQLException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    // update company comboboxes after CRUD
                    companyOne.repaint();
                    companyTwo.repaint();
                    companyOne.revalidate();
                    companyTwo.revalidate();
                }
            });

            // (3) (?) info button to get description of app
            JButton infoButton = new JButton("?");
            infoButton.setBounds(970, 540, 20, 20);//x axis, y axis, width, height
            infoButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JOptionPane.showMessageDialog(f, "SNP500DB is a CRUD application for investors providing financial information of top 25 companies (by market-cap) in the S&P 500 in 2020-2021. Numbers are in units of billion $USD");
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