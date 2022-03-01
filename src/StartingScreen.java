import javax.swing.*;
import java.awt.*;

public class StartingScreen {
    JFrame f;
    // hello update from david
    StartingScreen() {
        f = new JFrame();
        JButton b = new JButton("click");
        JPanel sql_result = new JPanel();
        JPanel sql_data = new JPanel();
        JPanel sql_company = new JPanel();
        sql_result.setBounds(500, 200, 400, 150);
        sql_result.add(new JLabel("Welcome to Tutorials Point"));
        sql_result.setBackground(Color.gray);
        sql_result.setVisible(true);
        sql_result.setVisible(true);//making the frame visible 
        f.add(sql_result);

        sql_data.setBounds(100, 200, 200, 200);
        sql_data.add(new JLabel("Pri chk | second camp | Quarter | Year"));
        sql_data.setBackground(Color.GRAY);
        sql_data.setVisible(true);
        sql_data.setVisible(true);//making the frame visible 
        f.add(sql_data);

        sql_company.setBounds(100, 100, 200, 50);
        sql_company.add(new JLabel("Primary Company Name"));
        sql_company.setBackground(Color.GRAY);
        sql_company.setVisible(true);
        sql_company.setVisible(true);//making the frame visible 
        f.add(sql_company);
        
        b.setBounds(300, 200, 100, 50);//x axis, y axis, width, height  
        f.add(b);//adding button in JFrame  
        f.setSize(1000,600);//1000 width and 600 height  
        f.setLayout(null);//using no layout managers  
        f.setVisible(true);//making the frame visible 
    }
}