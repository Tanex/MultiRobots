package nu.tanex.core.data;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-03
 */
public class Rubble extends GameObject {
    /**
     * Initializes the rubble object
     */
    public Rubble() {
        super("#", CollisionBehaviour.getRubbleCollisionBehaviour());
    }
}
