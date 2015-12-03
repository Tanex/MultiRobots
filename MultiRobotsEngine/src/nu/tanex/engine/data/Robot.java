package nu.tanex.engine.data;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Robot extends GameObject {
    private Player targetPlayer;

    public Robot(){
        this(null);
    }

    public Robot(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }
}
