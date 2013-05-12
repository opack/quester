package com.slamdunk.quester.core.pathfinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class is a pathfinder which uses AStar algorithm.
 * 
 * The A* algorithm is an algorithm of search for way in a graph between an initial
 * node and a final node.  It uses a heuristic evaluation on each node to consider
 * the best way passing there, and visits then the nodes by order of this heuristic
 * evaluation. 
 * </br>AStar is an algorithm which certifies to find a way between a point
 * A and a point B if there is one.  It is more intelligent than Dijsktra because it
 * is not interested only in the points enabling him to approach the point B. At each
 * turn it uses the theorem of Pythagore to calculate an approximation of the distance
 * between the point running and the point B. At each point to analyze AStar updates
 * the parent of the point, the distance since the source and the distance (approximate)
 * to the arrival. When the visited point is the arrival point , AStar updates the list
 * that it will be returned.  For that it course from parent to parent from the point B
 * (arrival) to the point A (departure). Point A is not put in the list because we consider
 * that it is the position of the object asking for a path. In the case of the way does
 * not exist AStar will return null.
 * 
 * @author S. Cleret & D. Demange
 */

public class AStar {
	
    private final int[] initCost;

    private final UnmutablePoint[] initParent;

    private int[] costSinceSrc;

    int[] totalCost;

    private UnmutablePoint[] parent;

    private final PriorityQueue<UnmutablePoint> opened;

    private final HashSet<UnmutablePoint> closed;

    private final ArrayList<UnmutablePoint> listPoint;
    
    private boolean canMoveDiagonally;
    
    private PointManager pointManager;
    
    /**
     * Can indicate whether a position is walkable or not
     */
    private boolean[][] walkables;

    /**
     * Creates an AStar object. Call this function when the map dimension
     * has changed or if the free positions are not the same.
     */
    public AStar(int width, int height) {
        initCost = new int[width * height];
        initParent = new UnmutablePoint[width * height];

        closed = new HashSet<UnmutablePoint>();

        opened = new PriorityQueue<UnmutablePoint>(20, new CostComparator ());

        listPoint = new ArrayList<UnmutablePoint>(8);
        
        pointManager = new PointManager(width, height);
        
        // Par défaut toute la map est walkable
        walkables = new boolean[width][height];
        for (int curCol = 0; curCol < width; curCol++) {
        	for (int curRow = 0; curRow < height; curRow++) {
        		walkables[curCol][curRow] = true;
        	}
        }
    }
    
    public boolean isCanMoveDiagonally() {
		return canMoveDiagonally;
	}

	public void setCanMoveDiagonally(boolean canMoveDiagonally) {
		this.canMoveDiagonally = canMoveDiagonally;
	}
	
	public List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY, boolean ignoreArrivalWalkable) {
		return findPath(pointManager.getPoint(fromX, fromY), pointManager.getPoint(toX, toY), ignoreArrivalWalkable);
	}

	/**
     * Finds a shortest path from the specified departure spot to the secified
     * arrival position. This method uses the A* algorithm has described in the
     * header. If a parameter is null or if the arrival position is occupied or
     * equal to the departure, this method returns null. If no path can be
     * found, this method returns null. The returned stack contains all the
     * successive positions to go through to reach the arrival.
     * 
     * @param departure
     *            The departure spot
     * @param arrival
     *            Where to go
     * @param ignoreArrivalWalkable
     * 			  If true, arrival position walkability is ignored.
     * @return null if no path could be found or a reference to a stack
     *         containing the positions to use
     */
    public List<UnmutablePoint> findPath(UnmutablePoint departure, UnmutablePoint arrival, boolean ignoreArrivalWalkable) {

        // If the destination is a blocked position, null is returned
        if ((departure == null) || (arrival == null)
        || (!ignoreArrivalWalkable && !walkables[arrival.getX()][arrival.getY()])
        || departure.equals(arrival)) {
            return null;
        }
        LinkedList<UnmutablePoint> path = new LinkedList<UnmutablePoint>();

        boolean isSonOpened = false;
        boolean isSonClosed = false;
        boolean pathFound = false;

        costSinceSrc = initCost.clone();
        totalCost = initCost.clone();
        parent = initParent.clone();

        closed.clear();
        opened.clear();

        opened.offer(departure);

        UnmutablePoint s = null;
        int ignoredWalkabilityIndex = -1;
        if (ignoreArrivalWalkable) {
        	ignoredWalkabilityIndex = arrival.getIndex();
        }

        while (!opened.isEmpty()) {
            s = opened.poll();

            if (s.equals(arrival)) {
                pathFound = true;
                break;
            }

            int sIndex = s.getIndex();

            for (UnmutablePoint t : getNeighborhood(s, ignoredWalkabilityIndex)) {
                int tIndex = t.getIndex();
                int tempCostSinceSrc = costSinceSrc[sIndex] + 1;
                int tempTotalCost = tempCostSinceSrc + getDistanceToArrival(t, arrival);

                isSonOpened = opened.contains(t);
                isSonClosed = closed.contains(t);

                if ((!isSonOpened && !isSonClosed) || (totalCost[tIndex] > tempTotalCost)) {

                    parent[tIndex] = s;
                    totalCost[tIndex] = tempTotalCost;
                    costSinceSrc[tIndex] = tempCostSinceSrc;

                    if (isSonOpened) {
                        opened.remove(t);
                    }

                    if (isSonClosed) {
                        closed.remove(t);
                    }

                    opened.offer(t);
                }
            }

            closed.add(s);
        }

        if (pathFound) {
            path.addFirst(arrival);
            s = parent[arrival.getIndex()];
            while (!s.equals(departure)) {
                path.addFirst(s);
                s = parent[s.getIndex()];
            }
            return path;
        }
        else {
            return null;
        }
    }

    private int getDistanceToArrival(UnmutablePoint position, UnmutablePoint destination) {
        return (destination.getX() - position.getX()) * (destination.getX() - position.getX()) + (destination.getY() - position.getY()) * (destination.getY() - position.getY());
    }

    private ArrayList<UnmutablePoint> getNeighborhood(UnmutablePoint center, int ignoredWalkabilityIndex) {
        listPoint.clear();
        
        // North
        addIfWalkable(listPoint, center.getX(), center.getY() - 1, ignoredWalkabilityIndex);

        // East
        addIfWalkable(listPoint, center.getX() + 1, center.getY(), ignoredWalkabilityIndex);

        // South
        addIfWalkable(listPoint, center.getX(), center.getY() + 1, ignoredWalkabilityIndex);

        // West
        addIfWalkable(listPoint, center.getX() - 1, center.getY(), ignoredWalkabilityIndex);

        if (canMoveDiagonally) {
	        // North East
        	addIfWalkable(listPoint, center.getX() + 1, center.getY() - 1, ignoredWalkabilityIndex);
	        
	        // South East
	        addIfWalkable(listPoint, center.getX() + 1, center.getY() + 1, ignoredWalkabilityIndex);
	        
	        // South West
	        addIfWalkable(listPoint, center.getX() - 1, center.getY() + 1, ignoredWalkabilityIndex);
	        
	        // North West
	        addIfWalkable(listPoint, center.getX() - 1, center.getY() - 1, ignoredWalkabilityIndex);
        }
        return listPoint;
    }
    
    private void addIfWalkable(ArrayList<UnmutablePoint> listPoint2, int x, int y, int ignoredWalkabilityIndex) {
    	UnmutablePoint pos = pointManager.getPoint(x, y);
        if (pos != null
        // La position est ajoutée si on doit ignorer sa walkability ou si elle est walkable
        && (pos.getIndex() == ignoredWalkabilityIndex || walkables[pos.getX()][pos.getY()])) {
            listPoint.add(pos);
        }		
	}

	private class CostComparator implements Comparator<UnmutablePoint>, Serializable {

        private static final long serialVersionUID = 8286298148231746736L;

        public int compare(UnmutablePoint p1, UnmutablePoint p2) {
            return (totalCost[p1.getIndex()] - totalCost[p2.getIndex()]);
        }
    }

	public void setWalkable(int x, int y, boolean walkable) {
		walkables[x][y] = walkable;
	}
}
