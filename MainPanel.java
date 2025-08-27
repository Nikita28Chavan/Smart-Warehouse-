package com.mycompany.smartwarehouse.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;

public class MainPanel extends JPanel {

    private GridPanel gridPanel;
    private JTextField inputField;
    private JButton startButton;
    private JButton resetButton;
    private JButton confirmButton;
    private Map<Point, String> itemMap;

    public MainPanel() {
        setLayout(new BorderLayout());

        gridPanel = new GridPanel();
        itemMap = gridPanel.itemMap;

        JPanel controlPanel = new JPanel();
        inputField = new JTextField(25);
        startButton = new JButton("Start Picking");
        resetButton = new JButton("Reset");
        confirmButton = new JButton("Confirm Pickup");

        controlPanel.add(new JLabel("Enter items (comma-separated):"));
        controlPanel.add(inputField);
        controlPanel.add(startButton);
        controlPanel.add(confirmButton);
        controlPanel.add(resetButton);

        add(controlPanel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleItemInput();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gridPanel.resetGrid();
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gridPanel.isWaitingForPickup()) {
                    gridPanel.pickCurrentItem();
                }
            }
        });
    }

    private void handleItemInput() {
        String inputText = inputField.getText().trim();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter item names.");
            return;
        }

        String[] itemNames = inputText.split(",");
        List<String> requestedItems = new ArrayList<>();
        for (String name : itemNames) {
            requestedItems.add(name.trim());
        }

        Map<String, Point> foundItems = new LinkedHashMap<>();
        List<String> missingItems = new ArrayList<>();

        for (String name : requestedItems) {
            Point pos = findItemPosition(name);
            if (pos != null) {
                foundItems.put(name, pos);
            } else {
                missingItems.add(name);
            }
        }

        if (!missingItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "These items are not in the warehouse:\n" + String.join(", ", missingItems),
                    "Items Missing", JOptionPane.WARNING_MESSAGE);
        }

        if (!foundItems.isEmpty()) {
            Set<Point> highlighted = new HashSet<>(foundItems.values());
            gridPanel.setHighlightedItems(highlighted);

            // 🔄 Optimize path before picking
            List<Point> optimizedOrder = sortByNearestPath(new ArrayList<>(foundItems.values()), gridPanel.getRobotPosition());

            new Thread(() -> {
                gridPanel.moveToItemsInteractive(new ArrayList<>(optimizedOrder)); // ✅ Fixed type
            }).start();
        } else {
            JOptionPane.showMessageDialog(this, "No valid items to pick.");
        }
    }

    private Point findItemPosition(String name) {
        for (Map.Entry<Point, String> entry : itemMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // ✅ Greedy nearest-neighbor sorting
    private List<Point> sortByNearestPath(List<Point> points, Point start) {
        List<Point> result = new ArrayList<>();
        Set<Point> remaining = new HashSet<>(points);
        Point current = new Point(start);

        while (!remaining.isEmpty()) {
            Point nearest = null;
            int minDist = Integer.MAX_VALUE;

            for (Point p : remaining) {
                int dist = Math.abs(current.x - p.x) + Math.abs(current.y - p.y); // Manhattan distance
                if (dist < minDist) {
                    minDist = dist;
                    nearest = p;
                }
            }

            result.add(nearest);
            remaining.remove(nearest);
            current = nearest;
        }

        return result;
    }
}
