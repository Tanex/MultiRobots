package nu.tanex.core.data;

import java.io.Reader;
import java.io.Serializable;

/**
 * @author      Victor Hedlund
 * @version     0.3
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
    /**
     * Gets the name of this player.
     *
     * @return The players name.
     */
    public String getName() {
        return name;
    }

    /**
     * The number of attacks that the player can perform in game.
     *
     * @return Number of attacks.
     */
    public int getNumAttacks() { return numAttacks; }

    /**
     * The number of random teleport that the player can perform in game.
     *
     * @return Number of random teleports.
     */
    public int getNumRandomTeleports() { return numRandomTeleports; }

    /**
     * The number of safe teleports that the player can perform in game.
     *
     * @return number of safe teleports.
     */
    public int getNumSafeTeleports() { return numSafeTeleports; }

    /**
     * The score that the player has accumulated in the game.
     *
     * @return the players score.
     */
    public int getScore() { return score; }

    /**
     * Sets the name of the player.
     *
     * @param name the players name.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets how many attacks the player can perform in game.
     *
     * @param numAttacks amount of attacks.
     */
    public void setNumAttacks(int numAttacks) { this.numAttacks = numAttacks; }

    /**
     * Sets how many random teleport the player can perform in game.
     *
     * @param numRandomTeleports amount of random teleports.
     */
    public void setNumRandomTeleports(int numRandomTeleports) { this.numRandomTeleports = numRandomTeleports; }

    /**
     * Sets how many safe teleport the player can perform in game.
     *
     * @param numSafeTeleports amount of safe teleports.
     */
    public void setNumSafeTeleports(int numSafeTeleports) { this.numSafeTeleports = numSafeTeleports; }

    /**
     * Sets the players score.
     *
     * @param score the new score.
     */
    public void setScore(int score) { this.score = score; }
    //endregion

    //region Public Methods
    /**
     * Gives the player additional random teleports that can be used in game.
     *
     * @param additionalTeleports amount of random teleports.
     */
    public void addRandomTeleports(int additionalTeleports){
        this.numRandomTeleports += additionalTeleports;
    }

    /**
     * Gives the player additional safe teleports that can be used in game.
     *
     * @param additionalTeleports amount of safe teleports.
     */
    public void addSafeeleports(int additionalTeleports){
        this.numSafeTeleports += additionalTeleports;
    }

    /**
     * Gives the player addition attacks that can be used in game.
     *
     * @param additionalAttacks amount of attacks.
     */
    public void addAttacks(int additionalAttacks){
        this.numAttacks += additionalAttacks;
    }

    /**
     * Gives the player additional score that has been accumulated in the game.
     *
     * @param additionalScore Score to give the player.
     */
    public void addScore(int additionalScore){
        this.score += additionalScore;
    }

    /**
     * Takes random teleports from the player as such are performed in game.
     *
     * @param teleportCost number of random teleports performed.
     */
    public void takeRandomTeleports(int teleportCost){
        this.numRandomTeleports -= teleportCost;
    }

    /**
     * Takes safe teleports from the player as such are performed in game.
     *
     * @param teleportCost number of safe teleports performed
     */
    public void takeSafeTeleports(int teleportCost){
        this.numSafeTeleports -= teleportCost;
    }

    /**
     * Takes attacks from the player as such are performed in game.
     *
     * @param attackCost number of attacks performed.
     */
    public void takeAttacks(int attackCost){
        this.numAttacks -= attackCost;
    }
    //endregion

    //region Constructors
    /**
     * Initializes the player without a name.
     * <p>
     * The name should be set later when the player has properly logged in.
     */
    public Player() {
        super("H", CollisionBehaviour.getPlayerCollisionBehaviour());
        name = null;
    }
    //endregion

    //region Object overrides

    @Override
    public String toString() {
        return name;
    }

    //endregion
}
