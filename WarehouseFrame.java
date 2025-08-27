package com.mycompany.smartwarehouse.ui;

import javax.swing.*;

public class WarehouseFrame extends JFrame {

    public WarehouseFrame() {
        setTitle("Smart Warehouse Item Picker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the GridPanel to the frame
        GridPanel gridPanel = new GridPanel();
        add(gridPanel);

        // Set frame properties
        pack(); // sizes the window to fit the preferred size of its components
        setLocationRelativeTo(null); // center on screen
        setVisible(true);
    }
}
