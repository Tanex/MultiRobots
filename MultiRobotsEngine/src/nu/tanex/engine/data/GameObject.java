package nu.tanex.engine.data;

import javafx.scene.paint.Color;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public abstract class GameObject {
    //region Member variables
    private int x;
    private int y;
    private Color color;
    private char token;
    //endregion

    //region Get-/setters
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    //endregion

    //region Constructors
    public GameObject() {

    }
    //endregion
}
