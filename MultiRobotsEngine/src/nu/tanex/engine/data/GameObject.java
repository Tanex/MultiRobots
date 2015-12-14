package nu.tanex.engine.data;

import javafx.scene.paint.Color;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public abstract class GameObject {
    //region Member variables
    //private int x;
    //private int y;
    protected Point point;
    private Color color;
    private String token;
    //endregion

    //region Get-/setters
    public Point getPoint() { return point; }
    //endregion

    //region Constructors
    public GameObject() {
        this(".");
    }

    public GameObject(String token) {
        this.token = token;
        point = new Point();
    }

    //endregion

    //region Object overrides

    @Override
    public String toString() {
        return token;
    }

    //endregion
}
