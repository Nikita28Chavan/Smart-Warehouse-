package com.mycompany.smartwarehouse.data;

import com.mycompany.smartwarehouse.logic.Item;

import java.io.*;
import java.util.*;
import java.awt.Point;

public class WarehouseLoader {

    public static Map<Point, String> loadItems(String csvPath) {
        Map<Point, String> itemMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 4) {
                    String id = tokens[0];
                    String name = tokens[1];
                    int row = Integer.parseInt(tokens[2]);
                    int col = Integer.parseInt(tokens[3]);
                    itemMap.put(new Point(row, col), id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return itemMap;
    }
}
