package com.mycompany.smartwarehouse.logic;

import java.awt.Point;
import java.util.*;

public class PathFinder {

    public static List<Point> findPath(Point start, Point end) {
        List<Point> path = new ArrayList<>();
        Point current = new Point(start);

        while (!current.equals(end)) {
            if (current.x < end.x) {
                current = new Point(current.x + 1, current.y);
            } else if (current.x > end.x) {
                current = new Point(current.x - 1, current.y);
            } else if (current.y < end.y) {
                current = new Point(current.x, current.y + 1);
            } else if (current.y > end.y) {
                current = new Point(current.x, current.y - 1);
            }
            path.add(current);
        }

        return path;
    }
}
