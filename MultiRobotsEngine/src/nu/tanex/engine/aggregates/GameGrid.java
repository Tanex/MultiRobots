package nu.tanex.engine.aggregates;

import nu.tanex.engine.data.GameObject;
import nu.tanex.engine.resources.Direction;

import java.util.Random;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class GameGrid {
    //region Member variables
    private int height;
    private int width;
    private GameObject[][] grid;
    private Random rng;
    //endregion

    //region Constructor
    public GameGrid() {
        this(30, 20);
    }

    public GameGrid(int width, int height) {
        this.width = width;
        this.height = height;
        rng = new Random();
        grid = new GameObject[width][height];
    }
    //endregion

    //region Public methods

    /**
     * Returns the @code GameObject in the @code GameGrid that is adjacent to the point (x,y) in the given direction.
     *
     * @param originX X-coordinate.
     * @param originY Y-coordinate.
     * @param direction Direction to get object from.
     * @return Adjacent @code GameObject @code direction.
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
     * Randomly places a @code GameObject into the grid, will not overwrite anything already in the grid.
     *
     * @param gameObject Object that should be placed in the grid.
     */
    public void randomlyPlaceGameObject(GameObject gameObject) {
        int x, y;
        //Keep generating new x,y values until the object is successfully placed in the grid
        do {
            x = rng.nextInt(width);
            y = rng.nextInt(height);
        } while (!placeGameObject(gameObject, x, y, false));
    }

    /**
     * Places a @code GameObject in the @code GameGrid at point (x, y).
     *
     * @param gameObject        Object that should be placed in the grid.
     * @param x                 X-coordinate
     * @param y                 Y-coordinate
     * @param overWriteExisting Whether to overwrite any existing object at (x, y).
     * @return Indicates if the object was successfully placed in the grid or not.
     */
    public boolean placeGameObject(GameObject gameObject, int x, int y, boolean overWriteExisting) {
        //Space is occupied and we shouldn't overwrite -> abort
        if (grid[x][y] != null && !overWriteExisting)
            return false;

        grid[x][y] = gameObject;
        gameObject.setX(x);
        gameObject.setY(y);
        return true;
    }
    //endregion
}
