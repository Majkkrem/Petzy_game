import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGamePanel extends JPanel implements ActionListener {
  private static final int SCREEN_WIDTH = 800;
  private static final int SCREEN_HEIGHT = 800;
  private static final int UNIT_SIZE = 25;
  private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
  private static final int DELAY = 75;

  private final int[] x = new int[GAME_UNITS];
  private final int[] y = new int[GAME_UNITS];
  private int bodyParts = 6;
  private int applesEaten;
  private int appleX;
  private int appleY;
  private char direction = 'R';
  private boolean running = false;
  private Timer timer;
  private final Random random;
  private final SnakeGameFrame parentFrame;

  public SnakeGamePanel(SnakeGameFrame parentFrame) {
    this.parentFrame = parentFrame;
    random = new Random();
    initializeGame();
  }

  private void initializeGame() {
    this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    this.setBackground(Color.BLACK);
    this.setFocusable(true);
    this.addKeyListener(new GameKeyAdapter());
    startGame();
  }

  private void startGame() {
    newApple();
    running = true;
    timer = new Timer(DELAY, this);
    timer.start();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  // In SnakeGamePanel.java
  private void draw(Graphics g) {
    if (running) {
      // Draw grid background
      g.setColor(new Color(20, 20, 20));
      g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

      // Draw grid lines
      g.setColor(new Color(30, 30, 30));
      for (int i = 0; i < SCREEN_WIDTH/UNIT_SIZE; i++) {
        g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
        g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
      }

      // Draw apple with gradient
      Graphics2D g2d = (Graphics2D)g;
      GradientPaint appleGradient = new GradientPaint(appleX, appleY, Color.RED,
          appleX+UNIT_SIZE, appleY+UNIT_SIZE, new Color(200, 0, 0));
      g2d.setPaint(appleGradient);
      g2d.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

      // Draw snake with gradient and rounded corners
      for (int i = 0; i < bodyParts; i++) {
        GradientPaint snakeGradient;
        if (i == 0) {
          snakeGradient = new GradientPaint(x[i], y[i], new Color(100, 255, 100),
              x[i]+UNIT_SIZE, y[i]+UNIT_SIZE, new Color(50, 200, 50));
        } else {
          snakeGradient = new GradientPaint(x[i], y[i], new Color(70, 220, 70),
              x[i]+UNIT_SIZE, y[i]+UNIT_SIZE, new Color(30, 150, 30));
        }
        g2d.setPaint(snakeGradient);
        g2d.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);
      }

      // Draw score with shadow effect
      g.setColor(Color.BLACK);
      g.setFont(new Font("Monospace", Font.BOLD, 40));
      FontMetrics metrics = getFontMetrics(g.getFont());
      g.drawString("Score: " + applesEaten,
          (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2 + 2,
          g.getFont().getSize() + 2);

      g.setColor(new Color(200, 200, 255));
      g.drawString("Score: " + applesEaten,
          (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2,
          g.getFont().getSize());
    } else {
      gameOver(g);
    }
  }

  private void newApple() {
    appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
    appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
  }

  private void move() {
    for (int i = bodyParts; i > 0; i--) {
      x[i] = x[i - 1];
      y[i] = y[i - 1];
    }

    switch (direction) {
      case 'U':
        y[0] = y[0] - UNIT_SIZE;
        break;
      case 'D':
        y[0] = y[0] + UNIT_SIZE;
        break;
      case 'L':
        x[0] = x[0] - UNIT_SIZE;
        break;
      case 'R':
        x[0] = x[0] + UNIT_SIZE;
        break;
    }
  }

  private void checkApple() {
    if ((x[0] == appleX) && (y[0] == appleY)) {
      bodyParts++;
      applesEaten++;
      newApple();
    }
  }

  private void checkCollisions() {
    // Check if head collides with body
    for (int i = bodyParts; i > 0; i--) {
      if ((x[0] == x[i]) && (y[0] == y[i])) {
        running = false;
      }
    }

    // Check if head touches borders
    if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
      running = false;
    }

    if (!running) {
      timer.stop();
    }
  }

  private void gameOver(Graphics g) {
    // Dark overlay
    g.setColor(new Color(0, 0, 0, 150));
    g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

    // Game Over text with shadow
    g.setColor(Color.BLACK);
    g.setFont(new Font("Monospace", Font.BOLD, 75));
    FontMetrics metrics = getFontMetrics(g.getFont());
    g.drawString("Game Over",
        (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2 + 5,
        SCREEN_HEIGHT/2 - 45);

    g.setColor(new Color(255, 100, 100));
    g.drawString("Game Over",
        (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2,
        SCREEN_HEIGHT/2 - 50);

    // Score text
    g.setColor(Color.WHITE);
    g.setFont(new Font("Monospace", Font.BOLD, 40));
    metrics = getFontMetrics(g.getFont());
    g.drawString("Score: " + applesEaten,
        (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2,
        SCREEN_HEIGHT/2 + 20);

    // Create prettier buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
    buttonPanel.setOpaque(false);
    buttonPanel.setBounds(SCREEN_WIDTH/2 - 150, SCREEN_HEIGHT/2 + 80, 300, 60);

    JButton restartButton = createGameButton("Restart", Color.GREEN);
    restartButton.addActionListener(e -> resetGame());

    JButton exitButton = createGameButton("Exit", Color.RED);
    exitButton.addActionListener(e -> exitGame());

    setLayout(null);
    buttonPanel.setOpaque(false);
    buttonPanel.setBounds(SCREEN_WIDTH / 2 - 150, SCREEN_HEIGHT / 2 + 80, 300, 60);
    buttonPanel.add(restartButton);
    buttonPanel.add(exitButton);
    add(buttonPanel);
    buttonPanel.setVisible(true);
  }


  private void resetGame() {
    bodyParts = 6;
    applesEaten = 0;
    direction = 'R';
    running = true;

    // Reset snake position
    for (int i = 0; i < bodyParts; i++) {
      x[i] = 0;
      y[i] = 0;
    }

    newApple();
    timer.start();
    removeAll(); // Remove any buttons from game over screen
    repaint();
  }

  private JButton createGameButton(String text, Color bgColor) {
    JButton button = new JButton(text);
    button.setFont(new Font("Monospace", Font.BOLD, 20));
    button.setPreferredSize(new Dimension(120, 50));
    button.setFocusable(false);
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.WHITE, 2),
        BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    return button;
  }


  private void exitGame() {
    parentFrame.getScreenFactory().addGold(applesEaten);
    parentFrame.dispose();
  }

  public void stopGame() {
    if (timer != null) {
      timer.stop();
    }
  }



  @Override
  public void actionPerformed(ActionEvent e) {
    if (running) {
      move();
      checkApple();
      checkCollisions();
    }
    repaint();
  }

  private class GameKeyAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
          if (direction != 'R') {
            direction = 'L';
          }
          break;
        case KeyEvent.VK_RIGHT:
          if (direction != 'L') {
            direction = 'R';
          }
          break;
        case KeyEvent.VK_UP:
          if (direction != 'D') {
            direction = 'U';
          }
          break;
        case KeyEvent.VK_DOWN:
          if (direction != 'U') {
            direction = 'D';
          }
          break;
      }
    }
  }
}