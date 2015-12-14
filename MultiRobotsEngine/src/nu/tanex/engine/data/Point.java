package nu.tanex.engine.data;

import nu.tanex.engine.resources.Direction;

import java.util.Random;

/**
 * Simple class to represent points in 2D space that also facilitates
 * generation of random points.
 *
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-14
 */
public class Point {
    //region Member variables
    private int x;
    private int y;
    private static Random rng = new Random();
    //endregion

    //region Get-/setters
    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void set(Point other) {
        this.x = other.x;
        this.y = other.y;
    }
    //endregion

    //region Constructors
    public Point() {
        this(0, 0);
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point other){
        set(other);
    }
    //endregion

    //region Public methods

    /**
     * Returns a new @code Point with random x, y coordinates between
     * 0 and xMax/yMax respectively.
     *
     * @param xMax Maximum value for x.
     * @param yMax Maximum value for y.
     * @return a new Point with random coordinates.
     */
    public static Point newRandomPoint(int xMax, int yMax) {
        return new Point(rng.nextInt(xMax), rng.nextInt(yMax));
    }
    //endregion
}
