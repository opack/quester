package com.slamdunk.quester.model.points;

/**
 * Unmutable point used to accelerate the process. This class contains unmutable
 * data and the only way the program can obtain and handle points is retrieve a
 * reference on the correct point.
 * 
 * @author S. Cleret & D. Demange
 */
public class Point {

	/**
	 * index of the point in an array
	 */
	private int index;

	/**
	 * x coordinate of the point. Cannot be change out of the constructor.
	 */
	private int x;

	/**
	 * y coordinate of the point. Cannot be change out of the constructor.
	 */
	private int y;

	/**
	 * Creates the point that has the specified coordinates. Once created, the
	 * point cannot be moved to another location.
	 * 
	 * @param x
	 *            abscisse of the point
	 * @param y
	 *            ordinate of the point
	 */
	public Point(int x, int y) {
		this(x, y, 0);
	}

	/**
	 * Creates the point that has the specified coordinates. Once created, the
	 * point cannot be moved to another location.
	 * 
	 * @param x
	 *            abscisse of the point
	 * @param y
	 *            ordinate of the point
	 * @param index
	 *            index of the point in an array
	 */
	public Point(int x, int y, int index) {
		this.x = x;
		this.y = y;
		this.index = index;
	}

	public Point(Point other) {
		this.x = other.x;
		this.y = other.y;
		this.index = other.index;
	}

	/**
	 * Returns the distance between the current point and the specified one.
	 * This distance is calculated using Pythagore, but without the squareroot
	 * 
	 * @param point
	 *            The other point
	 * @return Square of the distance between the two points
	 */
	public double distanceSq(Point point) {
		int deltaX = point.getX() - x;
		int deltaY = point.getY() - y;
		return deltaX * deltaX + deltaY * deltaY;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point point = (Point) o;
			return (point.getX() == x) && (point.getY() == y)
					&& (point.getIndex() == index);
		}
		return false;
	}

	/**
	 * Returns index of the point.
	 * 
	 * @return index of the point
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns abscisse of the point.
	 * 
	 * @return abscisse of the point
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns ordinate of the point.
	 * 
	 * @return ordinate of the point
	 */
	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return x ^ y ^ index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + ";" + y + ")";
	}
}
