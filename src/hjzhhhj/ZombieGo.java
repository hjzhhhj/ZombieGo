package hjzhhhj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class ZombieGo extends JPanel implements ActionListener, KeyListener {
    private int playerX = 300, playerY = 400, playerSpeed = 10;
    private int score = 0;
    private Timer timer;
    private ArrayList<Point> zombies;
    private ArrayList<Point> projectiles;
    private Random random = new Random();
    private int projectileSpeedX = 0, projectileSpeedY = 0;
    private int lastDirectionX = 0, lastDirectionY = -1;

    // 이미지 변수
    private Image playerFront, playerBack, playerLeft, playerRight, background;
    private Image arrowUp, arrowDown, arrowLeft, arrowRight;
    private Image zombieFront, zombieBack, zombieLeft, zombieRight;

    public ZombieGo() {
        // 이미지 로딩
        playerBack = new ImageIcon(getClass().getResource("/img/C_back.PNG")).getImage();
        playerFront = new ImageIcon(getClass().getResource("/img/C_front.PNG")).getImage();
        playerLeft = new ImageIcon(getClass().getResource("/img/C_left.PNG")).getImage();
        playerRight = new ImageIcon(getClass().getResource("/img/C_right.PNG")).getImage();
        background = new ImageIcon(getClass().getResource("/img/background.JPEG")).getImage();

        // 화살 이미지 로딩
        arrowUp = new ImageIcon(getClass().getResource("/img/A_up.PNG")).getImage();
        arrowDown = new ImageIcon(getClass().getResource("/img/A_down.PNG")).getImage();
        arrowLeft = new ImageIcon(getClass().getResource("/img/A_left.PNG")).getImage();
        arrowRight = new ImageIcon(getClass().getResource("/img/A_right.PNG")).getImage();

        // 좀비 이미지 로딩
        zombieFront = new ImageIcon(getClass().getResource("/img/Z_front.PNG")).getImage();
        zombieBack = new ImageIcon(getClass().getResource("/img/Z_back.PNG")).getImage();
        zombieLeft = new ImageIcon(getClass().getResource("/img/Z_left.PNG")).getImage();
        zombieRight = new ImageIcon(getClass().getResource("/img/Z_right.PNG")).getImage();

        chooseJob();
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        timer = new Timer(30, this);
        timer.start();
        zombies = new ArrayList<>();
        projectiles = new ArrayList<>();
        spawnZombie();
    }

    private void chooseJob() {
        JOptionPane.showMessageDialog(null, "You are an Archer!", "Job Selection", JOptionPane.PLAIN_MESSAGE);
    }

    private void spawnZombie() {
        zombies.add(new Point(random.nextInt(800), random.nextInt(500)));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // 플레이어 방향에 따라 이미지 선택
        Image currentPlayerImage = playerFront;
        if (lastDirectionY == -1) currentPlayerImage = playerBack;
        else if (lastDirectionY == 1) currentPlayerImage = playerFront;
        else if (lastDirectionX == -1) currentPlayerImage = playerLeft;
        else if (lastDirectionX == 1) currentPlayerImage = playerRight;

        g.drawImage(currentPlayerImage, playerX, playerY, 30, 30, this);

        // 좀비 그리기 (방향 적용)
        for (Point zombie : zombies) {
            int dx = Integer.compare(playerX, zombie.x);
            int dy = Integer.compare(playerY, zombie.y);
            Image currentZombieImage = zombieFront;

            if (dy == -1) currentZombieImage = zombieBack;
            else if (dy == 1) currentZombieImage = zombieFront;
            else if (dx == -1) currentZombieImage = zombieLeft;
            else if (dx == 1) currentZombieImage = zombieRight;

            g.drawImage(currentZombieImage, zombie.x, zombie.y, 30, 30, this);
        }

        // 화살 그리기 (방향 적용)
        for (Point projectile : projectiles) {
            Image currentArrow = arrowUp;
            if (projectileSpeedY == -5) currentArrow = arrowUp;
            else if (projectileSpeedY == 5) currentArrow = arrowDown;
            else if (projectileSpeedX == -5) currentArrow = arrowLeft;
            else if (projectileSpeedX == 5) currentArrow = arrowRight;

            g.drawImage(currentArrow, projectile.x, projectile.y, 10, 10, this);
        }

        // 점수 표시
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveZombies();
        moveProjectiles();
        checkCollisions();
        repaint();
    }

    private void moveZombies() {
        for (Point zombie : zombies) {
            if (zombie.x < playerX) zombie.x += 2;
            else if (zombie.x > playerX) zombie.x -= 2;

            if (zombie.y < playerY) zombie.y += 2;
            else if (zombie.y > playerY) zombie.y -= 2;
        }
    }

    private void moveProjectiles() {
        for (int i = 0; i < projectiles.size(); i++) {
            Point p = projectiles.get(i);
            p.x += projectileSpeedX;
            p.y += projectileSpeedY;
            if (p.y < 0 || p.y > 600 || p.x < 0 || p.x > 800) {
                projectiles.remove(i);
                i--;
            }
        }
    }

    private void checkCollisions() {
        for (Point zombie : zombies) {
            if (new Rectangle(playerX, playerY, 30, 30).intersects(new Rectangle(zombie.x, zombie.y, 30, 30))) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score);
                System.exit(0);
            }
        }

        for (int i = 0; i < projectiles.size(); i++) {
            Point p = projectiles.get(i);
            for (int j = 0; j < zombies.size(); j++) {
                Point zombie = zombies.get(j);
                if (new Rectangle(p.x, p.y, 10, 10).intersects(new Rectangle(zombie.x, zombie.y, 30, 30))) {
                    zombies.remove(j);
                    projectiles.remove(i);
                    i--;
                    score += 100;
                    spawnZombie();
                    break;
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) { playerX -= playerSpeed; lastDirectionX = -1; lastDirectionY = 0; }
        if (key == KeyEvent.VK_RIGHT) { playerX += playerSpeed; lastDirectionX = 1; lastDirectionY = 0; }
        if (key == KeyEvent.VK_UP) { playerY -= playerSpeed; lastDirectionX = 0; lastDirectionY = -1; }
        if (key == KeyEvent.VK_DOWN) { playerY += playerSpeed; lastDirectionX = 0; lastDirectionY = 1; }
        if (key == KeyEvent.VK_SPACE) attack();
    }

    private void attack() {
        projectiles.add(new Point(playerX + 10, playerY + 10));
        projectileSpeedX = lastDirectionX * 5;
        projectileSpeedY = lastDirectionY * 5;
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Go");
        ZombieGo game = new ZombieGo();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
