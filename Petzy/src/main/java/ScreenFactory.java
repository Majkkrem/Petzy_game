// ScreenFactory.java
import Animals.Animal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenFactory {
  private final JFrame frame;
  private final Animal animal;
  private int coins = 100;
  private final Map<String, Integer> inventory = new HashMap<>();
  private Timer sleepTimer;
  private final JPanel cardPanel;
  private final CardLayout cardLayout;
  private final Map<String, Map<Lables, JLabel>> overlayLabels;

  public ScreenFactory(JFrame frame, Animal animal, JPanel cardPanel, CardLayout cardLayout) {
    this.frame = frame;
    this.animal = animal;
    this.cardPanel = cardPanel;
    this.cardLayout = cardLayout;
    this.overlayLabels = new HashMap<>();
    initializeInventory();
  }

  private void initializeInventory() {
    inventory.put("Toy", 1);
    inventory.put("Health Potion", 0);
    inventory.put("Water", 2);
  }

  public JPanel createMainScreen() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(createBackgroundPanel(), BorderLayout.CENTER);
    panel.add(createOverlayPanel("Main"), BorderLayout.CENTER);
    return panel;
  }

  public JPanel createKitchenScreen() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(createBackgroundPanel(), BorderLayout.CENTER);

    JPanel kitchenPanel = new JPanel(new BorderLayout());
    kitchenPanel.setOpaque(false);

    JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    iconPanel.setOpaque(false);

    JButton fridgeButton = createIconButton("Fridge", e -> openShop());
    JButton inventoryButton = createIconButton("Inventory", e -> openInventory());

    iconPanel.add(fridgeButton);
    iconPanel.add(inventoryButton);

    kitchenPanel.add(iconPanel, BorderLayout.NORTH);
    kitchenPanel.add(createStatsPanel("Kitchen"), BorderLayout.SOUTH);

    panel.add(kitchenPanel, BorderLayout.CENTER);
    return panel;
  }

  public JPanel createBedRoomScreen() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(createBackgroundPanel(), BorderLayout.CENTER);

    JPanel bedroomPanel = new JPanel(new BorderLayout());
    bedroomPanel.setOpaque(false);

    bedroomPanel.add(createStatsPanel("Bedroom"), BorderLayout.NORTH);
    bedroomPanel.add(createButtonPanel(), BorderLayout.SOUTH);

    panel.add(bedroomPanel, BorderLayout.CENTER);
    return panel;
  }

  private JPanel createBackgroundPanel() {
    JLabel backgroundLabel = new JLabel("Placeholder Background", SwingConstants.CENTER);
    backgroundLabel.setOpaque(true);
    backgroundLabel.setBackground(Color.GRAY);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(backgroundLabel, BorderLayout.CENTER);
    return panel;
  }

  private JPanel createOverlayPanel(String screenName) {
    JPanel overlay = new JPanel(new BorderLayout());
    overlay.setOpaque(false);

    overlay.add(createStatsPanel(screenName), BorderLayout.NORTH);
    overlay.add(createButtonPanel(), BorderLayout.SOUTH);

    return overlay;
  }

  private JPanel createStatsPanel(String screenName) {
    JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
    statsPanel.setOpaque(false);
    statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    Map<Lables, JLabel> labels = new HashMap<>();

    for (Lables labelType : Lables.values()) {
      JLabel label = new JLabel(getLabelText(labelType), SwingConstants.CENTER);
      label.setForeground(Color.WHITE);
      label.setFont(new Font("Arial", Font.BOLD, 14));
      statsPanel.add(label);
      labels.put(labelType, label);
    }

    overlayLabels.put(screenName, labels);

    return statsPanel;
  }

  private String getLabelText(Lables labelType) {
    switch (labelType) {
      case ENERGY:
        return "Energy: " + animal.getEnergy();
      case HUNGER:
        return "Hunger: " + animal.getFood();
      case HEALTH:
        return "Health: " + animal.getHealth();
      case MOOD:
        return "Mood: " + animal.getMood();
      default:
        return "";
    }
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 5, 5));
    buttonPanel.setOpaque(false);

    String[] buttons = {"Feed", "Water", "Play", "Sleep", "Heal", "Snake Game"};
    for (String text : buttons) {
      JButton button = new JButton(text);
      button.addActionListener(this::handleButtonAction);
      buttonPanel.add(button);
    }

    return buttonPanel;
  }

  private JButton createIconButton(String tooltip, ActionListener action) {
    JButton button = new JButton(tooltip);
    button.setToolTipText(tooltip);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.addActionListener(action);
    return button;
  }

  private void handleButtonAction(ActionEvent e) {
    String command = ((JButton) e.getSource()).getText();
    switch (command) {
      case "Feed":
        animal.feed();
        break;
      case "Water":
        animal.giveWater();
        break;
      case "Play":
        animal.play();
        break;
      case "Sleep":
        toggleSleep();
        break;
      case "Heal":
        animal.heal();
        break;
      case "Snake Game":
        new SnakeGameFrame(this);
        break;
    }
    updateAllScreens();
  }

  private void toggleSleep() {
    if (sleepTimer == null) {
      animal.setSleeping(true);
      sleepTimer = new Timer();
      sleepTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          animal.setEnergy(Math.min(100, animal.getEnergy() + 5));
          SwingUtilities.invokeLater(() -> updateAllScreens());
        }
      }, 0, 1000);
    } else {
      animal.setSleeping(false);
      sleepTimer.cancel();
      sleepTimer = null;
    }
  }

  public void updateAllScreens() {
    for (Map.Entry<String, Map<Lables, JLabel>> screenEntry : overlayLabels.entrySet()) {
      for (Map.Entry<Lables, JLabel> labelEntry : screenEntry.getValue().entrySet()) {
        labelEntry.getValue().setText(getLabelText(labelEntry.getKey()));
      }
    }
    cardPanel.revalidate();
    cardPanel.repaint();
  }

  public void addGold(int amount) {
    coins += amount;
    JOptionPane.showMessageDialog(frame, "You earned " + amount + " coins!");
    updateAllScreens();
  }

  private void openShop() {
    JDialog shopDialog = new JDialog(frame, "Shop", true);
    shopDialog.setSize(400, 300);
    shopDialog.setLayout(new BorderLayout());

    JLabel coinLabel = new JLabel("Coins: " + coins, SwingConstants.CENTER);
    shopDialog.add(coinLabel, BorderLayout.NORTH);

    JPanel itemsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    String[] items = {"Food", "Toy", "Health Potion", "Water"};
    int[] prices = {10, 20, 30, 5};

    for (int i = 0; i < items.length; i++) {
      JPanel itemPanel = new JPanel(new BorderLayout());
      JButton buyButton = new JButton("Buy (" + prices[i] + " coins)");
      int finalI = i;

      buyButton.addActionListener(e -> {
        if (coins >= prices[finalI]) {
          coins -= prices[finalI];
          inventory.put(items[finalI], inventory.getOrDefault(items[finalI], 0) + 1);
          coinLabel.setText("Coins: " + coins);
          JOptionPane.showMessageDialog(shopDialog, "Purchased " + items[finalI] + "!");
        } else {
          JOptionPane.showMessageDialog(shopDialog, "Not enough coins!", "Error", JOptionPane.ERROR_MESSAGE);
        }
      });

      itemPanel.add(new JLabel(items[i], SwingConstants.CENTER), BorderLayout.CENTER);
      itemPanel.add(buyButton, BorderLayout.SOUTH);
      itemsPanel.add(itemPanel);
    }

    shopDialog.add(itemsPanel, BorderLayout.CENTER);
    shopDialog.setLocationRelativeTo(frame);
    shopDialog.setVisible(true);
  }

  private void openInventory() {
    JDialog inventoryDialog = new JDialog(frame, "Inventory", true);
    inventoryDialog.setSize(400, 300);
    inventoryDialog.setLayout(new BorderLayout());

    JPanel itemsPanel = new JPanel(new GridLayout(2, 2, 10, 10));

    for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
      if (entry.getValue() > 0) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JButton useButton = new JButton("Use (" + entry.getValue() + ")");

        useButton.addActionListener(e -> {
          inventory.put(entry.getKey(), entry.getValue() - 1);
          useItem(entry.getKey());
          inventoryDialog.dispose();
          openInventory(); // Refresh
        });

        itemPanel.add(new JLabel(entry.getKey(), SwingConstants.CENTER), BorderLayout.CENTER);
        itemPanel.add(useButton, BorderLayout.SOUTH);
        itemsPanel.add(itemPanel);
      }
    }

    inventoryDialog.add(itemsPanel, BorderLayout.CENTER);
    inventoryDialog.setLocationRelativeTo(frame);
    inventoryDialog.setVisible(true);
  }

  private void useItem(String item) {
    switch (item) {
      case "Food":
        animal.feed();
        break;
      case "Water":
        animal.giveWater();
        break;
      case "Toy":
        animal.play();
        break;
      case "Health Potion":
        animal.heal();
        break;
    }
    updateAllScreens();
    JOptionPane.showMessageDialog(frame, "Used " + item + "!");
  }
}