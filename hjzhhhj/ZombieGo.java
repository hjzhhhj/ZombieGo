package hjzhhhj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.List;

public class ZombieGo extends JPanel implements ActionListener, KeyListener {

    private int playerX = 300, playerY = 400, playerSpeed = 10;
    private int score = 0;
    private int highScore = 0;
    private Timer timer;
    private ArrayList<Point> zombies;
    private ArrayList<Point> projectiles;
    private Random random = new Random();
    private int projectileSpeedX = 0, projectileSpeedY = 0;
    private int lastDirectionX = 0, lastDirectionY = -1;
    private long lastAttackTime = 0;
    private final int attackCooldown = 1000;
    private String schoolId;
    private FileHandler fileHandler;
    private Image playerFront, playerBack, playerLeft, playerRight, background;
    private Image arrowUp, arrowDown, arrowLeft, arrowRight;
    private Image zombieFront, zombieBack, zombieLeft, zombieRight;
    private final int MIN_DISTANCE = 100;
    private Login loginFrame; // Login 프레임에 대한 참조

    public ZombieGo(String schoolId, FileHandler fileHandler, Login loginFrame) {
        this.schoolId = schoolId;
        this.fileHandler = fileHandler;
        this.highScore = fileHandler.getScore(schoolId);
        this.loginFrame = loginFrame; // Login 프레임 참조 초기화

        playerBack = new ImageIcon(getClass().getResource("/img/C_back.PNG")).getImage();
        playerFront = new ImageIcon(getClass().getResource("/img/C_front.PNG")).getImage();
        playerLeft = new ImageIcon(getClass().getResource("/img/C_left.png")).getImage();
        playerRight = new ImageIcon(getClass().getResource("/img/C_right.PNG")).getImage();
        background = new ImageIcon(getClass().getResource("/img/background.JPEG")).getImage();
        arrowUp = new ImageIcon(getClass().getResource("/img/A_back.PNG")).getImage();
        arrowDown = new ImageIcon(getClass().getResource("/img/A_front.PNG")).getImage();
        arrowLeft = new ImageIcon(getClass().getResource("/img/A_left.PNG")).getImage();
        arrowRight = new ImageIcon(getClass().getResource("/img/A_right.PNG")).getImage();
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

        spawnZombies(3);
    }

    private void chooseJob() {
        JOptionPane.showMessageDialog(null, "당신은 아처입니다!", "직업 선택하기", JOptionPane.PLAIN_MESSAGE);
    }

    private void spawnZombie() {
        Point newZombie;
        do {
            newZombie = new Point(random.nextInt(800), random.nextInt(600));
        } while (Math.abs(newZombie.x - playerX) < MIN_DISTANCE && Math.abs(newZombie.y - playerY) < MIN_DISTANCE);
        zombies.add(newZombie);
    }

    private void spawnZombies(int count) {
        for (int i = 0; i < count; i++) {
            spawnZombie();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        Image currentPlayerImage = playerFront;
        if (lastDirectionY == -1)
            currentPlayerImage = playerBack;
        else if (lastDirectionY == 1)
            currentPlayerImage = playerFront;
        else if (lastDirectionX == -1)
            currentPlayerImage = playerLeft;
        else if (lastDirectionX == 1)
            currentPlayerImage = playerRight;
        g.drawImage(currentPlayerImage, playerX, playerY, 70, 100, this);

        for (Point zombie : zombies) {
            int dx = Integer.compare(playerX, zombie.x);
            int dy = Integer.compare(playerY, zombie.y);

            Image currentZombieImage = zombieFront;
            if (dx == 0) {
                if (dy == -1)
                    currentZombieImage = zombieBack;
                else if (dy == 1)
                    currentZombieImage = zombieFront;
            } else {
                if (dx == -1)
                    currentZombieImage = zombieLeft;
                else if (dx == 1)
                    currentZombieImage = zombieRight;
            }
            g.drawImage(currentZombieImage, zombie.x, zombie.y, 80, 110, this);
        }

        for (Point projectile : projectiles) {
            Image currentArrow = arrowUp;
            if (projectileSpeedY == -10)
                currentArrow = arrowUp;
            else if (projectileSpeedY == 10)
                currentArrow = arrowDown;
            else if (projectileSpeedX == -10)
                currentArrow = arrowLeft;
            else if (projectileSpeedX == 10)
                currentArrow = arrowRight;
            g.drawImage(currentArrow, projectile.x, projectile.y, 50, 50, this);
        }

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("High Score: " + highScore, 10, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveZombies();
        moveProjectiles();
        checkCollisions();
        repaint();
        if (random.nextInt(100) < 2) {
            spawnZombie();
        }
    }

    private void moveZombies() {
        for (Point zombie : zombies) {
            int dx = Integer.compare(playerX, zombie.x);
            int dy = Integer.compare(playerY, zombie.y);
            if (dx != 0 && dy != 0) {
                if (random.nextBoolean())
                    dy = 0;
                else
                    dx = 0;
            }
            if (dx == -1)
                zombie.x -= 2;
            else if (dx == 1)
                zombie.x += 2;
            else if (dy == -1)
                zombie.y -= 2;
            else if (dy == 1)
                zombie.y += 2;
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
        // 1. 플레이어가 좀비랑 충돌했는지 체크 (플레이어 죽음)
        for (Point zombie : zombies) {
            if (new Rectangle(playerX, playerY, 50, 50).intersects(new Rectangle(zombie.x, zombie.y, 50, 50))) {
                System.out.println("💀 플레이어가 좀비한테 닿음! 게임 종료");
                timer.stop();

                // 점수 업데이트 (이전 점수보다 높을 경우)
                if (score > highScore) {
                    System.out.println("🏆 새로운 최고 점수! 업데이트 중...");
                    highScore = score;
                    fileHandler.updateScore(schoolId, highScore);
                } else {
                    System.out.println("📌 최고 점수 갱신 안 함 (기존 점수보다 낮음)");
                }

                // 게임 종료 메시지 표시
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Game Over! \nYour score: " + score + "\nHigh Score: " + highScore + "\n다시 시작하시겠습니까?",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    restartGame();
                } else {
                    loginFrame.setVisible(true); // 로그인 창 다시 표시
                    Window window = SwingUtilities.getWindowAncestor(this);
                    if (window != null) {
                        window.dispose(); // 게임 창 닫기
                    }
                }
                return; // 플레이어가 죽으면 더 이상 체크할 필요 없음
            }
        }

        // 2. 화살이 좀비를 맞췄는지 체크 (좀비 사망)
        for (int i = 0; i < projectiles.size(); i++) {
            Point p = projectiles.get(i);
            for (int j = 0; j < zombies.size(); j++) {
                Point zombie = zombies.get(j);

                if (new Rectangle(p.x, p.y, 20, 20).intersects(new Rectangle(zombie.x, zombie.y, 50, 50))) {
                    System.out.println("🎯 화살이 좀비를 맞춤! 좀비 제거");
                    zombies.remove(j);
                    projectiles.remove(i);
                    i--; 

                    // 점수 증가
                    score += 100;
                    System.out.println("💯 현재 점수: " + score);

                    // 새로운 좀비 생성
                    spawnZombie();
                    break;
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            playerX -= playerSpeed;
            lastDirectionX = -1;
            lastDirectionY = 0;
        }
        if (key == KeyEvent.VK_RIGHT) {
            playerX += playerSpeed;
            lastDirectionX = 1;
            lastDirectionY = 0;
        }
        if (key == KeyEvent.VK_UP) {
            playerY -= playerSpeed;
            lastDirectionX = 0;
            lastDirectionY = -1;
        }
        if (key == KeyEvent.VK_DOWN) {
            playerY += playerSpeed;
            lastDirectionX = 0;
            lastDirectionY = 1;
        }
        if (key == KeyEvent.VK_SPACE)
            attack();
    }

    private void attack() {
        if (System.currentTimeMillis() - lastAttackTime >= attackCooldown) {
            projectiles.add(new Point(playerX + 15, playerY + 15));
            projectileSpeedX = lastDirectionX * 10;
            projectileSpeedY = lastDirectionY * 10;
            lastAttackTime = System.currentTimeMillis();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

   // 게임 재시작 메서드
    private void restartGame() {
        score = 0;
        playerX = 300;
        playerY = 400;
        zombies.clear();
        projectiles.clear();
        spawnZombies(3);
        timer.start();
        requestFocusInWindow(); // 키 입력 다시 활성화
    }
}