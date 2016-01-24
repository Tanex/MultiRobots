package nu.tanex.core.data;

import nu.tanex.core.exceptions.TargetPlayerDeadException;

import java.util.Random;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Robot extends GameObject {
    //region Member variables
    private Player targetPlayer;
    private static Random rng = new Random();
    //endregion

    //region Setters
    /**
     * Sets which player the robot should chase.
     *
     * @param targetPlayer player that the robot should chase.
     */
    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }
    //endregion

    //region Constructors
    /**
     * Initializes the robot without a target player.
     * <p>
     * Should only be used together with RobotAiMode.ChaseClosest
     *
     * @see nu.tanex.core.resources.RobotAiMode
     */
    public Robot() {
        this(null);
    }

    /**
     * Initializes the robot with a player that should be chased by the robot.
     *
     * @param targetPlayer player that the robot targets.
     */
    public Robot(Player targetPlayer) {
        super("@", CollisionBehaviour.getRobotCollisionBehaviour());
        this.targetPlayer = targetPlayer;
    }
    //endregion

    //region Public methods
    /**
     * Returns a new Point indicating the coordinates that this Robot should
     * bew moved to for it to move towards targetPoint
     *
     * @param targetPoint The point to move towards, if null, targetPlayer point is used.
     * @return Point that should be the new position of this Robot.
     */
    public Point calculateMovement(Point targetPoint) throws TargetPlayerDeadException {
        if (targetPoint == null) {
            if (!targetPlayer.isAlive())
                throw new TargetPlayerDeadException("He's dead...");
            targetPoint = targetPlayer.getPoint();
        }

        int xChange = Integer.signum(targetPoint.getX() - this.point.getX());
        int yChange = Integer.signum(targetPoint.getY() - this.point.getY());
        return new Point(this.point.getX() + xChange, this.point.getY() + yChange);
    }
    //endregion
}
