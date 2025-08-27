package com.mycompany.smartwarehouse.logic;

public class Item {
    private String id;
    private String name;
    private int row;
    private int col;

    public Item(String id, String name, int row, int col) {
        this.id = id;
        this.name = name;
        this.row = row;
        this.col = col;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getRow() { return row; }
    public int getCol() { return col; }
}

