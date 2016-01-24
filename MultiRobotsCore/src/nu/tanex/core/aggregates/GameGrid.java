package nu.tanex.core.aggregates;

import nu.tanex.core.data.GameObject;
import nu.tanex.core.data.Point;
import nu.tanex.core.resources.Direction;

/**
 * @deprecated  No longer used, use Game class on server instead
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class GameGrid {
    //region Member variables
    private int height;
    private int width;
    private GameObject[][] grid;
    //endregion

    //region Constructor
    public GameGrid() {
        this(30, 20);
    }

    public GameGrid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new GameObject[width][height];
    }
    //endregion

    //region Public methods

    /**
     * Returns the GameObject in the GameGrid that is adjacent to the point (x,y) in the given direction.
     *
     * @param originX X-coordinate.
     * @param originY Y-coordinate.
     * @param direction Direction to get object from.
     * @return Adjacent GameObject direction.
     */
    public GameObject getObjectInDir(int originX, int originY, Direction direction){
        switch (direction) {
            case Up:
                return (originY > 0) ? grid[originX][originY - 1] : null;

            case Right:
                return (originX < width - 1) ? grid[originX + 1][originY] : null;

            case Left:
                return (originX > 0) ? grid[originX - 1][originY] : null;

            case Down:
                return (originY < height - 1) ? grid[originX][originY + 1] : null;

            case UpRight:
                return (originX < width - 1 && originY > 0) ? grid[originX + 1][originY - 1] : null;

            case UpLeft:
                return (originX > 0 && originY > 0) ? grid[originX - 1][originY - 1] : null;

            case DownRight:
                return (originX < width - 1 && originY < height - 1) ? grid[originX + 1][originY + 1] : null;

            case DownLeft:
                return (originX > 0 && originY < height - 1) ? grid[originX - 1][originY + 1] : null;

            default:
                return null;
        }
    }

    /**
     * Randomly places a {@code GameObject} into the grid, will not overwrite anything already in the grid.
     *
     * @param gameObject Object that should be placed in the grid.
     */
    public void randomlyPlaceGameObject(GameObject gameObject) {
        Point point = new Point();
        //Keep generating new x,y values until the object is successfully placed in the grid
        while (!placeGameObject(gameObject, Point.newRandomPoint(width, height), false))
            ;
    }

    public void moveGameObject(GameObject gameObject, Point point, boolean overWriteExisting){
        grid[gameObject.getPoint().getX()][gameObject.getPoint().getY()] = null;
        placeGameObject(gameObject, point, overWriteExisting);
    }

    //endregion

    //region Private methods
    /**
     * Places a @code GameObject in the @code GameGrid at point (x, y).
     *
     * @param gameObject        Object that should be placed in the grid.
     * @param point             Point where the @code GameObject should be placed.
     * @param overWriteExisting Whether to overwrite any existing object at (x, y).
     * @return Indicates if the object was successfully placed in the grid or not.
     */
    private boolean placeGameObject(GameObject gameObject, Point point, boolean overWriteExisting) {
        //Space is occupied and we shouldn't overwrite -> abort
        if (grid[point.getX()][point.getY()] != null && !overWriteExisting)
            return false;

        grid[point.getX()][point.getY()] = gameObject;
        gameObject.getPoint().set(point);
        return true;
    }
    //endregion

    //region Object overrides

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                str += grid[j][i] == null ? "." : grid[j][i].toString();
            }
            str += "\n";
        }
        return str;
    }

    //endregion
}
