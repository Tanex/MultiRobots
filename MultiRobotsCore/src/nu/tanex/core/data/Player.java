package nu.tanex.core.data;

import java.io.Serializable;
import java.util.Random;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Player extends GameObject implements Serializable{
    //region Member variables
    private int playerNum;
    private int numAttacks = 10;
    private int numRandomTeleports = 10;
    private int numSafeTeleports = 10;
    private int score;
    private String name;
    //endregion

    //region Get-/Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPlayerNum() { return playerNum; }
    public int getNumAttacks() { return numAttacks; }
    public int getNumRandomTeleports() { return numRandomTeleports; }
    public int getNumSafeTeleports() { return numSafeTeleports; }
    public int getScore() { return score; }
    //endregion

    //region Constructors
    public Player() {
        super("H", CollisionBehaviour.getPlayerCollisionBehaviour());
        playerNum = new Random().nextInt(1000);
        name = null;
    }
    //endregion
}
