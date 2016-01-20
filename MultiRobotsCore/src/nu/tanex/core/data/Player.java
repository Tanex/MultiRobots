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
    private int numAttacks = 10;
    private int numRandomTeleports = 10;
    private int numSafeTeleports = 10;
    private int score;
    private String name;
    //endregion

    //region Get-/Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getNumAttacks() { return numAttacks; }
    public int getNumRandomTeleports() { return numRandomTeleports; }
    public int getNumSafeTeleports() { return numSafeTeleports; }
    public int getScore() { return score; }
    public void setNumAttacks(int numAttacks) { this.numAttacks = numAttacks; }
    public void setNumRandomTeleports(int numRandomTeleports) { this.numRandomTeleports = numRandomTeleports; }
    public void setNumSafeTeleports(int numSafeTeleports) { this.numSafeTeleports = numSafeTeleports; }
    public void setScore(int score) { this.score = score; }
    //endregion

    //region Public Methods
    public void addRandomTeleports(int additionalTeleports){
        this.numRandomTeleports += additionalTeleports;
    }
    public void addSafeeleports(int additionalTeleports){
        this.numSafeTeleports += additionalTeleports;
    }
    public void addAttacks(int additionalAttacks){
        this.numAttacks += additionalAttacks;
    }
    public void addScore(int additionalScore){
        this.score += additionalScore;
    }

    public void takeRandomTeleports(int teleportCost){
        this.numRandomTeleports -= teleportCost;
    }
    public void takeSafeTeleports(int teleportCost){
        this.numSafeTeleports -= teleportCost;
    }
    public void takeAttacks(int attackCost){
        this.numAttacks -= attackCost;
    }
    //endregion

    //region Constructors
    public Player() {
        super("H", CollisionBehaviour.getPlayerCollisionBehaviour());
        name = null;
    }
    //endregion
}
