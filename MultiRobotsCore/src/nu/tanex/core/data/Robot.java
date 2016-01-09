package nu.tanex.core.data;

import nu.tanex.core.exceptions.TargetPlayerDeadException;

import java.util.Optional;
import java.util.Random;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Robot extends GameObject {
    private Player targetPlayer;
    private static Random rng = new Random();

    public Robot(){
        this(null);
    }

    public Robot(Player targetPlayer) {
        super("@", CollisionBehaviour.getRobotCollisionBehaviour());
        this.targetPlayer = targetPlayer;
    }

    /**
     * Returns a new Point indicating the coordinates that this Robot should
     * bew moved to for it to move towards targetPoint
     *
     * @param targetPoint The point the @to move towards, if null, targetPlayer point is used.
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

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }
}
