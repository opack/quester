package com.slamdunk.quester.model.points;

import java.util.ArrayList;

/**
 * Places at the disposal of the user all the unmutable points that can be accesed in the map.
 * To retrieve a point, use the {@link #getPoint(int, int) <code>getPoint</code>} method.
 * This class can be accessed either in a static context as a singleton or in a per-instance way.
 * 
 * @author S. Cleret & D. Demange
 *
 * @see #getPoint(int, int)
 */
public class PointManager {

    /**
     * List that contains all the accessible points
     */
    private ArrayList<UnmutablePoint> points;
    
    /**
     * Map width
     */
    private int width;
    
    /**
     * Map height
     */
    private int height;
    
    /**
     * Static instance
     */
    private static PointManager instance;
    
    /**
     * Creates the manager for the specified map size.
     * The manager will create width * height points and dispose them in an arraylist for rapid access
     * @param width map width
     * @param height map height
     */
    public PointManager (int width, int height) {
        this.width = width;
        this.height = height;
        points = new ArrayList<UnmutablePoint> (width * height);
        for (int y = 0; y < height; y ++) {
            for (int x = 0; x < width; x ++) {
                points.add (new UnmutablePoint (x, y, y * width + x));
            }
        }
    }
    
	/**
     * Retrieve a reference to the point that has the specified coordinates.
     * If such a point does not exist, null is returned.
     * @param x x coordinate
     * @param y y coordinate
     * @return reference to the specified point
     */
    public UnmutablePoint getPoint (int x, int y) {
        if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
            return null;
        }
        return getPoint (y * width + x);
    }
    
    /**
     * Translates the specified point of x units along x axis, and y units along y axis.
     * Returns null if no such point exists.
     * @param point source point
     * @param x abscisse translation delta
     * @param y ordinate translation delta
     * @return translated point
     */
    public UnmutablePoint translate (UnmutablePoint point, int x, int y) {
        return getPoint (point.getX() + x, point.getY() + y);
    }
    
    /**
     * Returns a reference to the point that has the specified index
     * or null if the index is not valid.
     * @param index index of the point to get
     * @return reference to the point of the specified index
     */
    public UnmutablePoint getPoint (int index) {
        if ((index < 0) || (index >= points.size())) {
            return null;
        }
        
        return points.get(index);
    }
    
    public static PointManager getInstance() {
    	if (instance == null) {
    		throw new IllegalStateException("Call init() before using PointManager in static context.");
    	}
    	return instance;
    }
    
    public static PointManager init(int width, int height) {
    	instance = new PointManager(width, height);
    	return instance;
    }
}
