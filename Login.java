package hjzhhhj;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    public Login() {
    	setTitle("Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 배경 패널 추가
        BackgroundPanel backgroundPanel = new BackgroundPanel("src/img/background.PNG");
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);
        

        // "MIRIMGO" 라벨
        JLabel titleLabel = new JLabel("MIRIMGO", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setBounds(250, 50, 300, 50);
        add(titleLabel);

        // 아이디 라벨 및 입력 필드
        JLabel idLabel = new JLabel("School ID");
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        idLabel.setBounds(200, 200, 100, 30);
        add(idLabel);

        JTextField idField = new JTextField();
        idField.setBounds(300, 200, 300, 30);
        add(idField);

        // 로그인 버튼
        JButton loginButton = new JButton("시작하기");
        loginButton.setBounds(350, 320, 100, 40);
        loginButton.setBackground(Color.WHITE);
        add(loginButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Login();
    }
}

//배경 이미지를 그리는 패널 클래스
class BackgroundPanel extends JPanel {
 private Image backgroundImage;

 public BackgroundPanel(String imagePath) {
     backgroundImage = new ImageIcon(imagePath).getImage();
 }

 @Override
 protected void paintComponent(Graphics g) {
     super.paintComponent(g);
     g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
 }
}