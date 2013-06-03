package com.slamdunk.quester.model.points;


public class Point {
	private int x;
	private int y;
	
	public Point(Point copy) {
		this.x = copy.x;
		this.y = copy.y;
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
