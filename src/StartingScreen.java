import javax.swing.*;
//import com.mysql.cj.xdevapi.Result;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class StartingScreen {
    // hello update from david
    public static void startingScreen() {
        // CONNECT TO SQL
        Connection conn = null;
        ResultSet res = null;
        try {
            // SQLITE
            conn = DriverManager.getConnection("jdbc:sqlite:db/snp500db.db");
            res = conn.createStatement().executeQuery("SELECT DISTINCT Ticker FROM EarningsID");
            System.out.println("Connection Established");


            // GUI
            JFrame f;
            f = new JFrame();
            JButton b = new JButton("extend");
            JPanel sql_result = new JPanel();
            JPanel sql_data = new JPanel();
            JPanel sql_company = new JPanel();
            sql_result.setBounds(500, 200, 450, 350);
            sql_result.add(new JLabel("Welcome to Tutorials Point"));
            sql_result.setBackground(Color.gray);
            sql_result.setVisible(true);
            sql_result.setVisible(true);//making the frame visible
            f.add(sql_result);

            sql_data.setBounds(100, 200, 200, 350);
            sql_data.add(new JLabel("Pri chk | second camp | Quarter | Year"));
            sql_data.setBackground(Color.GRAY);
            sql_data.setVisible(true);
            sql_data.setVisible(true);//making the frame visible
            f.add(sql_data);

            sql_company.setBounds(100, 100, 300, 50);
            sql_company.add(new JLabel("Primary Company Name"));
            sql_company.setBackground(Color.GRAY);
            sql_company.setVisible(true);
            sql_company.setVisible(true);//making the frame visible
            f.add(sql_company);

            ArrayList<String> name = new ArrayList<String>();
            while (res.next()) {
                name.add(res.getString("Ticker"));
                System.out.println(res.getString("Ticker"));
            }

            JComboBox comboBox = new JComboBox(name.toArray());
            sql_company.add(comboBox, BorderLayout.NORTH);

            b.setBounds(500, 175, 100, 25);//x axis, y axis, width, height
            f.add(b);//adding button in JFrame
            f.setSize(1000,600);//1000 width and 600 height
            f.setLayout(null);//using no layout managers
            f.setVisible(true);//making the frame visible

            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
}