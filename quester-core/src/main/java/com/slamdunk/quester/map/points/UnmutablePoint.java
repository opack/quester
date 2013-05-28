package com.slamdunk.quester.map.points;


/**
 * Unmutable point used to accelerate the process. This class contains unmutable data and the only way the
 * program can obtain and handle points is retrieve a reference on the correct point.
 * @author S. Cleret & D. Demange
 */
public class UnmutablePoint {

    /**
     * x coordinate of the point. Cannot be change out of the constructor.
     */
    private final int x;
    
    /**
     * y coordinate of the point. Cannot be change out of the constructor.
     */
    private final int y;
    
    /**
     * index of the point in an array
     */
    private final int index;
    
    /**
     * Creates the point that has the specified coordinates. Once created, the point cannot be moved to another location.
     * @param x abscisse of the point
     * @param y ordinate of the point
     */
    public UnmutablePoint (int x, int y) {
        this (x, y, 0);
    }

    /**
     * Creates the point that has the specified coordinates. Once created, the point cannot be moved to another location.
     * @param x abscisse of the point
     * @param y ordinate of the point
     * @param index index of the point in an array
     */
    public UnmutablePoint (int x, int y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }
    
    /**
     * Returns abscisse of the point.
     * @return abscisse of the point
     */
    public int getX () {
        return x;
    }
    
    /**
     * Returns ordinate of the point.
     * @return ordinate of the point
     */
    public int getY () {
        return y;
    }
    
    /**
     * Returns index of the point.
     * @return index of the point
     */
    public int getIndex () {
        return index;
    }
    
    /**
     * Returns the distance between the current point and the specified one. This distance is calculated
     * using Pythagore, but without the squareroot
     * @param point The other point
     * @return Square of the distance between the two points
     */
    public double distanceSq (UnmutablePoint point) {
        int deltaX = point.getX() - x;
        int deltaY = point.getY() - y;
        return deltaX * deltaX + deltaY * deltaY;
    }
    
    @Override public boolean equals (Object o) {
        if (o instanceof UnmutablePoint) {
            UnmutablePoint point = (UnmutablePoint) o;
            return (point.getX() == x) && (point.getY() == y) && (point.getIndex() == index);
        }
        return false;
    }
    
    @Override public int hashCode () {
        return x ^ y ^ index;
    }
    
    @Override public String toString () {
        return "(" + x + ";" + y + ")";
    }
}
