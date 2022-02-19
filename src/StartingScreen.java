import javax.swing.*;

public class StartingScreen {
    JFrame f;
    // hello update from david
    StartingScreen() {
        f = new JFrame();
        JButton b = new JButton("click");
        b.setBounds(130,100,100, 40);//x axis, y axis, width, height  
        f.add(b);//adding button in JFrame  
        f.setSize(400,500);//400 width and 500 height  
        f.setLayout(null);//using no layout managers  
        f.setVisible(true);//making the frame visible 
    }
}