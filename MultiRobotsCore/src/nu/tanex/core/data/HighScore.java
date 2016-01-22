package nu.tanex.core.data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-22
 */
public class HighScore implements Comparable<HighScore>, Serializable{
    //region Member variables
    private String playerName;
    private int score;
    private Date date;
    //endregion

    //region Constructors
    /**
     * Initializes the HighScore object with the current date and time.
     * <p>
     * The date is only used internally for sorting making sure that newer HighScores take precedence.
     *
     * @param player Player whose high score to be stored.
     */
    public HighScore(Player player) {
        this.playerName = player.getName();
        this.score = player.getScore();
        this.date = new Date();
    }
    //endregion

    //region Interface Comparable<HighScore>
    @Override
    public int compareTo(HighScore other) {
        int intComp = Integer.compare(this.score, other.score);
        if (intComp != 0)
            return intComp;
        else
            return this.date.compareTo(other.date);
    }
    //endregion

    //region Getters
    /**
     * Gets the name of the player who got this score.
     *
     * @return The players name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the score that the player got.
     *
     * @return The players score.
     */
    public int getScore() {
        return score;
    }
    //endregion
}
