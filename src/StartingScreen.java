import javax.swing.*;
import java.awt.*;

public class StartingScreen {
    
    // hello update from david
    public static void startingScreen() {
        JFrame f;
        
        f = new JFrame();
        f.setResizable(false);
        JButton b = new JButton("extend");
        JPanel sql_result = new JPanel();
        JPanel sql_data = new JPanel();
        JPanel sql_company = new JPanel();

        sql_result.setBounds(500, 200, 450, 350);
        sql_result.add(new JLabel("Welcome to Tutorials Point"));
        sql_result.setBackground(Color.gray);
        sql_result.setVisible(true);//making the frame visible 
        f.add(sql_result);

        sql_data.setBounds(100, 200, 300, 350);
        sql_data.add(new JLabel("Pri chk | second camp | Quarter | Year"));
        sql_data.setBackground(Color.GRAY);
        sql_data.setVisible(true);//making the frame visible 
        f.add(sql_data);

        sql_company.setBounds(100, 100, 300, 50);
        sql_company.add(new JLabel("Primary Company Name"));
        sql_company.setBackground(Color.GRAY);
        sql_company.setVisible(true);//making the frame visible 
        f.add(sql_company);
        
        b.setBounds(500, 175, 100, 25);//x axis, y axis, width, height  
        f.add(b);//adding button in JFrame  
        f.setSize(1000,600);//1000 width and 600 height  
        f.setLayout(null);//using no layout managers  
        f.setVisible(true);//making the frame visible 
    }
}