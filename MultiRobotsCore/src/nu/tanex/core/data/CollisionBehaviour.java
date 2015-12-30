package nu.tanex.core.data;

import nu.tanex.core.resources.CollisionObjectType;
import nu.tanex.core.resources.CollisionOutcome;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-21
 */
public class CollisionBehaviour {
    private static CollisionBehaviour robotCollisionBehaviour = null;
    private static CollisionBehaviour playerCollisionBehaviour = null;
    private static CollisionBehaviour rubbleCollisionBehaviour = null;

    private CollisionObjectType objectType = null;

    private CollisionOutcome playerCollision = null;
    private CollisionOutcome robotCollision = null;
    private CollisionOutcome rubbleCollision = null;

    public CollisionBehaviour(){

    }

    public CollisionBehaviour(CollisionObjectType objectType, CollisionOutcome playerCollision, CollisionOutcome robotCollision, CollisionOutcome rubbleCollision) {
        this.objectType = objectType;
        this.playerCollision = playerCollision;
        this.robotCollision = robotCollision;
        this.rubbleCollision = rubbleCollision;
    }

    public CollisionOutcome collideWith(GameObject object){
        switch (object.getCollisionBehaviour().objectType) {
            case Player: return playerCollision;
            case Robot: return robotCollision;
            case Rubble: return rubbleCollision;
        }
        return CollisionOutcome.NA;
    }

    public static CollisionBehaviour getPlayerCollisionBehaviour(){
        if (playerCollisionBehaviour == null)
            playerCollisionBehaviour = new CollisionBehaviour(
                    CollisionObjectType.Player,
                    CollisionOutcome.PlayerCollision,
                    CollisionOutcome.Object1Death,
                    CollisionOutcome.Object1Death);

        return playerCollisionBehaviour;
    }

    public static CollisionBehaviour getRobotCollisionBehaviour(){
        if (robotCollisionBehaviour == null)
            robotCollisionBehaviour = new CollisionBehaviour(
                    CollisionObjectType.Robot,
                    CollisionOutcome.Object2Death,
                    CollisionOutcome.RobotCollision,
                    CollisionOutcome.Object1Death);

        return robotCollisionBehaviour;
    }

    public static CollisionBehaviour getRubbleCollisionBehaviour(){
        if (rubbleCollisionBehaviour == null)
            rubbleCollisionBehaviour = new CollisionBehaviour(
                    CollisionObjectType.Rubble,
                    CollisionOutcome.Object2Death,
                    CollisionOutcome.Object2Death,
                    CollisionOutcome.NA);

        return rubbleCollisionBehaviour;
    }
}
