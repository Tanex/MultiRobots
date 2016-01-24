package nu.tanex.core.data;

import nu.tanex.core.resources.Direction;

import java.util.Random;

/**
 * Simple class to represent points in 2D space that also facilitates
 * generation of random points.
 *
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-14
 */
public class Point implements Comparable<Point>{
    //region Member variables
    private int x;
    private int y;
    private static Random rng = new Random();
    //endregion

    //region Get-/setters
    /**
     * Gets the x coordinate of this Point
     *
     * @return x coordinate.
     */
    public int getX() {
        synchronized (this){
            return this.x;
        }
    }

    /**
     * Sets the x coordinate of this Point
     *
     * @param x x coordinate.
     */
    public void setX(int x) {
        synchronized (this){
            this.x = x;
        }
    }

    /**
     * Gets the y coordinate of this Point
     *
     * @return y coordinate.
     */
    public int getY() {
        synchronized (this){
            return this.y;
        }
    }

    /**
     * Sets the y coordinate of this Point
     *
     * @param y y coordinate.
     */
    public void setY(int y) {
        synchronized (this){
            this.y = y;
        }
    }

    /**
     * Copies the coordinates of the other point to this point.
     *
     * @param other points whose coordinates should be copied.
     */
    public void set(Point other) {
        synchronized (this){
            this.x = other.getX();
            this.y = other.getY();
        }
    }
    //endregion

    //region Constructors
    /**
     * Creates a point with coordinates (0, 0)
     */
    public Point() {
        this(0, 0);
    }

    /**
     * Creates a point with coordinates (x, y)
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     * @param other Point to copy.
     */
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

    /**
     * Calculates linear distance from this point to the one given.
     *
     * @param other Point to measure distance to.
     * @return The linear distance.
     */
    public double distanceTo(Point other) {
        synchronized (this){
            return Math.sqrt(Math.pow(this.x - other.getX(), 2.0) + Math.pow(this.y - other.getY(), 2.0));
        }
    }

    /**
     * Checks whether this point is reachable with a single move from the other point.
     *
     * @param other point to check against
     * @return if it is reachable with one move
     */
    public boolean isWithinOneMove(Point other){
        synchronized (this){
            return !(Math.abs(this.x - other.getX()) > 1 || Math.abs(this.y - other.getY()) > 1);
        }
    }

    /**
     * Return a new Point that is one move away from this in a random direction
     *
     * @return new Point
     */
    public Point getPointInRandomDirection(){
        int xChange = (Math.abs(rng.nextInt()) % 3) - 1; //-1, 0 or 1
        int yChange = (Math.abs(rng.nextInt()) % 3) - 1; //-1, 0 or 1
        synchronized (this){
            return new Point(this.x + xChange, this.y + yChange);
        }
    }

    /**
     * Gets the new Point that is adjacent to this one in the given direction.
     *
     * @param direction direction to look at.
     * @return new Point.
     */
    public Point getPointInDirection(Direction direction){
        synchronized (this){
            switch (direction) {
                case Up:
                    return new Point(this.x, this.y - 1);
                case Right:
                    return new Point(this.x + 1, this.y);
                case Left:
                    return new Point(this.x - 1, this.y);
                case Down:
                    return new Point(this.x, this.y + 1);
                case UpRight:
                    return new Point(this.x + 1, this.y - 1);
                case UpLeft:
                    return new Point(this.x - 1, this.y - 1);
                case DownRight:
                    return new Point(this.x + 1, this.y + 1);
                case DownLeft:
                    return new Point(this.x - 1, this.y + 1);
            }
        }
        return null;
    }
    //endregion

    //region Object overrides
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Point))
            return false;

        return this.x == ((Point)other).getX() && this.y == ((Point)other).getY();
    }

    @Override
    public int hashCode() {
        synchronized (this){
            return Integer.hashCode(x + y);
        }
    }

    @Override
    public int compareTo(Point other) {
        synchronized (this){
            return Integer.compare(this.x + this.y, other.x + other.y);
        }
    }
    //endregion
}
