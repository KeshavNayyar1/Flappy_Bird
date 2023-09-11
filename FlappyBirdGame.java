import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdGame extends JPanel implements ActionListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int BIRD_SIZE = 30;
    private final int PIPE_WIDTH = 60;
    private final int PIPE_GAP = 200;
    private final int GROUND_HEIGHT = 20;
    private final int GRAVITY = 2;

    private int birdY;
    private int birdVelocity;
    private ArrayList<Rectangle> topPipes;
    private ArrayList<Rectangle> bottomPipes;
    private int score;

    private boolean isGameOver;

    public FlappyBirdGame() {
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !isGameOver) {
                    birdVelocity = -20;
                }
                if (isGameOver) {
                    restartGame();
                }
            }
        });

        topPipes = new ArrayList<>();
        bottomPipes = new ArrayList<>();
        generatePipes();

        Timer timer = new Timer(20, this);
        timer.start();

        birdY = HEIGHT / 2;
        birdVelocity = 0;
        score = 0;
        isGameOver = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            birdVelocity += GRAVITY;
            birdY += birdVelocity;

            movePipes();

            checkCollisions();

            if (!isGameOver) {
                score++;
            }

            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, WIDTH, HEIGHT - GROUND_HEIGHT);

        // Draw ground
        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, GROUND_HEIGHT);

        // Draw bird
        g.setColor(Color.red);
        g.fillRect(100, birdY, BIRD_SIZE, BIRD_SIZE);

        // Draw pipes
        g.setColor(Color.green);
        for (Rectangle pipe : topPipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }
        for (Rectangle pipe : bottomPipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        // Draw score
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 36));
        g.drawString("Score: " + score, 20, 40);

        // Draw game over message
        if (isGameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.drawString("Game Over", WIDTH / 2 - 200, HEIGHT / 2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 36));
            g.drawString("Press SPACE to restart", WIDTH / 2 - 200, HEIGHT / 2 + 50);
        }
    }

    private void generatePipes() {
        Random random = new Random();
        int pipeGapY = random.nextInt(HEIGHT - GROUND_HEIGHT - PIPE_GAP * 2) + PIPE_GAP;
        int pipeX = WIDTH;

        // Top pipe
        topPipes.add(new Rectangle(pipeX, 0, PIPE_WIDTH, pipeGapY));

        // Bottom pipe
        bottomPipes.add(new Rectangle(pipeX, pipeGapY + PIPE_GAP, PIPE_WIDTH, HEIGHT - pipeGapY - PIPE_GAP - GROUND_HEIGHT));
    }

    private void movePipes() {
        ArrayList<Rectangle> pipesToRemove = new ArrayList<>();
        for (int i = 0; i < topPipes.size(); i++) {
            Rectangle topPipe = topPipes.get(i);
            Rectangle bottomPipe = bottomPipes.get(i);

            topPipe.x -= 5;
            bottomPipe.x -= 5;

            if (topPipe.x + topPipe.width < 0) {
                pipesToRemove.add(topPipe);
                pipesToRemove.add(bottomPipe);
            }
        }

        topPipes.removeAll(pipesToRemove);
        bottomPipes.removeAll(pipesToRemove);

        if (topPipes.isEmpty() || topPipes.get(topPipes.size() - 1).x < WIDTH - 300) {
            generatePipes();
        }
    }

    private void checkCollisions() {
        Rectangle birdRect = new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE);

        for (int i = 0; i < topPipes.size(); i++) {
            Rectangle topPipe = topPipes.get(i);
            Rectangle bottomPipe = bottomPipes.get(i);

            if (birdRect.intersects(topPipe) || birdRect.intersects(bottomPipe)) {
                isGameOver = true;
            }
        }

        if (birdY >= HEIGHT - GROUND_HEIGHT - BIRD_SIZE) {
            isGameOver = true;
        }
    }

    private void restartGame() {
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        topPipes.clear();
        bottomPipes.clear();
        score = 0;
        generatePipes();
        isGameOver = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlappyBirdGame());
    }
}