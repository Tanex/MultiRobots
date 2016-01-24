package nu.tanex.core.aggregates;

import nu.tanex.core.data.HighScore;
import nu.tanex.core.data.Player;

import java.io.*;
import java.util.Collections;
import java.util.TreeSet;
import nu.tanex.core.resources.Resources;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-22
 */
public class HighScoreList extends TreeSet<HighScore> {
    //region Constructors
    public HighScoreList() {
        super(Collections.reverseOrder());
    }
    //endregion

    //region Public methods

    /**
     * Submits a players score to the HighScoreList.
     * <p>
     * The score will not be added if the list is already full of scores that are higher
     * the the new score.
     *
     * @param player Player whose score should be submitted to the list.
     */
    public void submitScore(Player player) {
        HighScore playerHighScore = new HighScore(player);
        this.add(playerHighScore);
        if (this.size() > Resources.HIGH_SCORE_LIST_SIZE)
            this.remove(this.last());
    }

    /**
     * Save the entire HighScoreList to a file.
     *
     * @param filename Filename without extension.
     */
    public void saveToFile(String filename) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(filename + ".bin")));
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Load an entire HighScoreList from file.
     *
     * @param filename Filename without extension.
     */
    public void loadFromFile(String filename) {
        ObjectInputStream in = null;
        try {
            File fin = new File(filename + ".bin");
            if (!fin.isFile())
                return;
            in = new ObjectInputStream(new FileInputStream(fin));
            HighScoreList readList = (HighScoreList) in.readObject();
            this.addAll(readList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    //endregion

    //region Object overrides
    @Override
    public String toString() {
        int iterLimit = 10;
        if (this.size() < iterLimit)
            iterLimit = this.size();

        String str = "";
        int i = 0;
        for (HighScore highScore : this) {
            str += "> " + highScore.getPlayerName() + ": " + highScore.getScore() + "<";
            if (++i >= iterLimit)
                break;
        }
        return str;
    }
    //endregion
}
