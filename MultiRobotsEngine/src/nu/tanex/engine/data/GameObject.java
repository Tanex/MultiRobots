package nu.tanex.engine.data;

import javafx.scene.paint.Color;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public abstract class GameObject {
    //region Member variables
    protected Point point;
    private Color color;
    private String token;
    private boolean alive;
    private CollisionBehaviour collisionBehaviour;
    //endregion

    //region Get-/setters
    public Point getPoint() { return point; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public CollisionBehaviour getCollisionBehaviour() { return collisionBehaviour; }
    //endregion

    //region Constructors
    public GameObject() {
        this(".", null);
    }

    public GameObject(String token, CollisionBehaviour collisionBehaviour) {
        this.token = token;
        this.collisionBehaviour = collisionBehaviour;
        point = new Point();
        alive = true;
    }

    //endregion

    //region Object overrides

    @Override
    public String toString() {
        return token;
    }

    //endregion
}
