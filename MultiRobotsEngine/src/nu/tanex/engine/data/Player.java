package nu.tanex.engine.data;

import java.util.Random;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Player extends GameObject {
    //region Member variables
    private int playerNum;
    private int numAttacks;
    private int numRandomTeleports;
    private int numSafeTeleports;
    private int score;
    //endregion

    //region Get-/Setters
    public int getPlayerNum() {
        return playerNum;
    }
    //endregion

    //region Constructors
    public Player() {
        playerNum = new Random().nextInt(1000);
    }
    //endregion
}
