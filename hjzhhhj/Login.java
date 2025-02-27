// Login.java
package hjzhhhj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class Login extends JFrame {

    private JTextField idField;
    private FileHandler fileHandler;
    private String schoolId;
    private JPanel topScoresPanel;

    public Login() {
        fileHandler = new FileHandler("scores.txt");
        setTitle("Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("src/img/background.PNG");
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        JLabel titleLabel = new JLabel("MIRIMGO", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setBounds(250, 50, 300, 50);
        add(titleLabel);

        JLabel idLabel = new JLabel("School ID");
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        idLabel.setBounds(200, 200, 100, 30);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(300, 200, 300, 30);
        add(idField);

        topScoresPanel = new JPanel();
        topScoresPanel.setLayout(new BoxLayout(topScoresPanel, BoxLayout.Y_AXIS));
        topScoresPanel.setBounds(250, 300, 300, 100);
        topScoresPanel.setBackground(new Color(0, 0, 0, 0));
        add(topScoresPanel);

        displayTopScores();

        JButton loginButton = new JButton("시작하기");
        loginButton.setBounds(350, 250, 100, 40);
        loginButton.setBackground(Color.WHITE);
        add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                schoolId = idField.getText().trim();
                if (!schoolId.isEmpty()) {
                    startGame();
                } else {
                    JOptionPane.showMessageDialog(Login.this, "학번을 입력해주세요.");
                }
            }
        });

        setVisible(true);
    }

    private void displayTopScores() {
        List<Map.Entry<String, Integer>> topScores = fileHandler.getTopScores(3);
        topScoresPanel.removeAll();
        JLabel titleLabel = new JLabel("최고 점수 Top 3", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topScoresPanel.add(titleLabel);

        if (topScores.isEmpty()) {
            JLabel noScoresLabel = new JLabel("점수 없음", SwingConstants.CENTER);
            noScoresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            topScoresPanel.add(noScoresLabel);
        } else {
            for (Map.Entry<String, Integer> entry : topScores) {
                JLabel scoreLabel = new JLabel(entry.getKey() + ": " + entry.getValue(), SwingConstants.CENTER);
                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                topScoresPanel.add(scoreLabel);
            }
        }

        topScoresPanel.revalidate();
        topScoresPanel.repaint();
    }

    private void startGame() {
        JFrame frame = new JFrame("Zombie Go");
        ZombieGo game = new ZombieGo(schoolId, fileHandler, this); // Login frame을 ZombieGo에 전달
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        this.setVisible(false); // 로그인 창 숨기기
    }

    // BackgroundPanel 클래스 (배경 이미지 설정)
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

    public static void main(String[] args) {
        new Login();
    }
}
