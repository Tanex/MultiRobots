package nu.tanex.core.data;

import javafx.scene.paint.Color;

/**
 * @author      Victor Hedlund
 * @version     0.2
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
    /**
     * Get the point representing this GameObjects location in the game.
     *
     * @return The GameObjects Point.
     */
    public Point getPoint() { return point; }

    /**
     * Checks whether this GameObject is considered Alive in the game.
     *
     * @return Whether the GameObject is alive or not.
     */
    public boolean isAlive() { return alive; }

    /**
     * Sets whether this GameObject should be considered alive or not.
     *
     * @param alive Whether the object is alive.
     */
    public void setAlive(boolean alive) { this.alive = alive; }

    /**
     * Gets this GameObjects CollisionBehaviour.
     *
     * @see CollisionBehaviour
     * @return CollisionBehaviour.
     */
    public CollisionBehaviour getCollisionBehaviour() { return collisionBehaviour; }
    //endregion

    //region Constructors

    /**
     * Initializes the GameObject as an empty space.
     */
    public GameObject() {
        this(".", null);
    }

    /**
     * Initializes the GameObject as alive with a new Point.
     *
     * @see Point
     * @see CollisionBehaviour
     * @param token The Objects "token" to be rendered on the game board.
     * @param collisionBehaviour The Objects CollisionBehaviour.
     */
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
