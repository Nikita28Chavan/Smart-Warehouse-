package com.mycompany.smartwarehouse.ui;

import com.mycompany.smartwarehouse.logic.PathFinder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class GridPanel extends JPanel {

    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int CELL_SIZE = 50;

    public Map<Point, String> itemMap = new HashMap<>();
    private Set<Point> highlightedItems = new HashSet<>();
    private Set<Point> pickedItems = new LinkedHashSet<>();
    private Point robotPos = new Point(0, 0);
    private List<Point> pathHistory = new ArrayList<>();
    private List<String> pickedItemNames = new ArrayList<>();
    private Queue<Point> targetQueue = new LinkedBlockingQueue<>();
    private boolean waitingForPickup = false;

    public GridPanel() {
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        loadItemsFromCSV("items.csv");
    }

    private void loadItemsFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    String itemName = tokens[0].trim();
                    int x = Integer.parseInt(tokens[1].trim());
                    int y = Integer.parseInt(tokens[2].trim());
                    itemMap.put(new Point(x, y), itemName);
                }
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Grid lines
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        // Path history in black
        g.setColor(Color.BLACK);
        for (Point p : pathHistory) {
            g.fillRect(p.x * CELL_SIZE + CELL_SIZE / 4, p.y * CELL_SIZE + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
        }

        // Draw items
        for (Map.Entry<Point, String> entry : itemMap.entrySet()) {
            Point p = entry.getKey();
            int x = p.x * CELL_SIZE;
            int y = p.y * CELL_SIZE;

            if (pickedItems.contains(p)) {
                g.setColor(Color.GRAY); // picked
            } else if (highlightedItems.contains(p)) {
                g.setColor(Color.GREEN); // ready to pick
            } else {
                g.setColor(Color.WHITE); // not selected
            }

            g.fillRect(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
            g.setColor(Color.BLACK);
            g.drawString(entry.getValue(), x + 8, y + 25);
        }

        // Draw robot
        g.setColor(Color.RED);
        int rx = robotPos.x * CELL_SIZE + CELL_SIZE / 2 - 10;
        int ry = robotPos.y * CELL_SIZE + CELL_SIZE / 2 - 10;
        g.fillOval(rx, ry, 20, 20);
    }

    public void setHighlightedItems(Set<Point> items) {
        highlightedItems.clear();
        highlightedItems.addAll(items);
        repaint();
    }

    public void resetGrid() {
        highlightedItems.clear();
        pickedItems.clear();
        pathHistory.clear();
        robotPos = new Point(0, 0);
        pickedItemNames.clear();
        targetQueue.clear();
        waitingForPickup = false;
        repaint();
    }

    public boolean isWaitingForPickup() {
        return waitingForPickup;
    }

    public void moveToItemsInteractive(List<Point> targets) {
        targetQueue.clear();
        targetQueue.addAll(targets);
        moveNext(); // Start with first item
    }

    public void moveNext() {
        if (targetQueue.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Picked items:\n" + String.join(", ", pickedItemNames),
                        "All Items Picked", JOptionPane.INFORMATION_MESSAGE);
            });
            return;
        }

        Point target = targetQueue.poll();
        List<Point> path = PathFinder.findPath(robotPos, target);

        if (path != null) {
            new Thread(() -> {
                for (Point step : path) {
                    robotPos = step;
                    pathHistory.add(step);
                    repaint();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                waitingForPickup = true;
                repaint();
            }).start();
        }
    }

    public void pickCurrentItem() {
        if (!waitingForPickup) return;

        pickedItems.add(robotPos);
        pickedItemNames.add(itemMap.get(robotPos));
        waitingForPickup = false;
        repaint();

        moveNext(); // Go to next after confirmation
    }

    public Point getRobotPosition() {
        return robotPos;
    }
}
