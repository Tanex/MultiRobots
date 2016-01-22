package nu.tanex.core.data;

import nu.tanex.core.resources.CollisionObjectType;
import nu.tanex.core.resources.CollisionOutcome;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-21
 */
public class CollisionBehaviour {
    //region Memeber variables
    private static CollisionBehaviour robotCollisionBehaviour = null;
    private static CollisionBehaviour playerCollisionBehaviour = null;
    private static CollisionBehaviour rubbleCollisionBehaviour = null;

    private CollisionObjectType objectType = null;

    private CollisionOutcome playerCollision = null;
    private CollisionOutcome robotCollision = null;
    private CollisionOutcome rubbleCollision = null;
    //endregion

    //region Constructors
    public CollisionBehaviour() {
    }

    public CollisionBehaviour(CollisionObjectType objectType, CollisionOutcome playerCollision, CollisionOutcome robotCollision, CollisionOutcome rubbleCollision) {
        this.objectType = objectType;
        this.playerCollision = playerCollision;
        this.robotCollision = robotCollision;
        this.rubbleCollision = rubbleCollision;
    }
    //endregion

    //region Public methods
    /**
     * Collides with the other GameObject and calculates the outcome of said collision.
     *
     * @see CollisionOutcome
     * @param object Object to collide with.
     * @return The outcome of the Collision.
     */
    public CollisionOutcome collideWith(GameObject object) {
        switch (object.getCollisionBehaviour().objectType) {
            case Player:
                return playerCollision;
            case Robot:
                return robotCollision;
            case Rubble:
                return rubbleCollision;
        }
        return CollisionOutcome.NA;
    }
    //endregion

    //region Factories
    /**
     * Factory function for getting a players collision behaviour.
     * <p>
     * Will return a static object so that it can be reused for all objects that need Player collision behaviour.
     *
     * @return CollisionBehaviour object setup for a Player-agent.
     */
    public static CollisionBehaviour getPlayerCollisionBehaviour() {
        if (playerCollisionBehaviour == null)
            playerCollisionBehaviour = new CollisionBehaviour(
                    CollisionObjectType.Player,
                    CollisionOutcome.PlayerCollision,
                    CollisionOutcome.Object1Death,
                    CollisionOutcome.Object1Death);

        return playerCollisionBehaviour;
    }
    /**
     *
     * Factory function for getting a robots collision behaviour.
     * <p>
     * Will return a static object so that it can be reused for all objects that need Robot collision behaviour.
     *
     * @return CollisionBehaviour object setup for a Robot-agent.
     */
    public static CollisionBehaviour getRobotCollisionBehaviour() {
        if (robotCollisionBehaviour == null)
            robotCollisionBehaviour = new CollisionBehaviour(
                    CollisionObjectType.Robot,
                    CollisionOutcome.Object2Death,
                    CollisionOutcome.RobotCollision,
                    CollisionOutcome.Object1Death);

        return robotCollisionBehaviour;
    }

    /**
     * Factory function for getting a rubbles collision behaviour.
     * <p>
     * Will return a static object so that it can be reused for all objects that need Rubble collision behaviour.
     *
     * @return CollisionBehaviour object setup for a Rubble-agent.
     */
    public static CollisionBehaviour getRubbleCollisionBehaviour() {
        if (rubbleCollisionBehaviour == null)
            rubbleCollisionBehaviour = new CollisionBehaviour(
                    CollisionObjectType.Rubble,
                    CollisionOutcome.Object2Death,
                    CollisionOutcome.Object2Death,
                    CollisionOutcome.NA);

        return rubbleCollisionBehaviour;
    }
    //endregion
}
