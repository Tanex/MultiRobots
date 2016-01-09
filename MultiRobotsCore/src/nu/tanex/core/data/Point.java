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

    /**
     * Calculates linear distance from this point to the one given.
     *
     * @param other Point to measure distance to.
     * @return The linear distance.
     */
    public double distanceTo(Point other) {
        return Math.sqrt(Math.pow(this.getX() + other.getX(), 2.0) + Math.pow(this.getY() + other.getY(), 2.0));
    }

    public boolean isWithinOneMove(Point other){
        return !(Math.abs(this.x - other.x) > 1 || Math.abs(this.y - other.y) > 1);
    }

    public Point getPointInRandomDirection(){
        int xChange = (rng.nextInt() % 3) - 1;
        int yChange = (rng.nextInt() % 3) - 1;
        return new Point(this.x + xChange, this.y + yChange);
    }

    public Point getPointInDirection(Direction direction){
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
        return null;
    }
    //endregion


    //region Object overrides
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Point))
            return false;
        return this.x == ((Point)other).x && this.y == ((Point)other).y;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x + y);
    }

    @Override
    public int compareTo(Point other) {
        return Integer.compare(this.x + this.y, other.x + other.y);
    }
    //endregion

}
