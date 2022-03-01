import javax.swing.*;
import java.awt.*;

public class StartingScreen {
    JFrame f;
    // hello update from david
    StartingScreen() {
        f = new JFrame();
        JButton b = new JButton("click");
        JPanel sql_result = new JPanel();
        sql_result.setBounds(500, 200, 400, 150);
        sql_result.add(new JLabel("Welcome to Tutorials Point"));
        sql_result.setBackground(Color.gray);
        sql_result.setVisible(true);
        sql_result.setVisible(true);//making the frame visible 
        f.add(sql_result);
        
        b.setBounds(300, 200, 100, 50);//x axis, y axis, width, height  
        f.add(b);//adding button in JFrame  
        f.setSize(1000,600);//1000 width and 600 height  
        f.setLayout(null);//using no layout managers  
        f.setVisible(true);//making the frame visible 
    }
}