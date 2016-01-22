package nu.tanex.server.gui.data;

import nu.tanex.core.resources.GameSettings;
import nu.tanex.core.resources.PlayerAttacks;
import nu.tanex.core.resources.RobotAiMode;
import nu.tanex.core.resources.RobotCollisions;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-19
 */
public class SettingsInfo {
    //region Member variables
    public int numInitialRobots;
    public int numAdditionalRobotsPerLevel;
    public int numInitialRubble;
    public RobotCollisions robotCollisions;
    public int numSafeTeleportsAwarded;
    public int numRandomTeleportsAwarded;
    public int numAttacksAwarded;
    public PlayerAttacks playerAttacks;
    public RobotAiMode robotAiMode;
    public int gridWidth;
    public int gridHeight;
    public int numPlayersToStartGame;
    //endregion

    //region Getters
    /**
     * Gets the total size of the game grid.
     *
     * @return total size, width * height
     */
    public int getGridSize() {
        return gridWidth * gridHeight;
    }
    //endregion

    //region Constructors
    /**
     * Initializes the object by loading all the settings from the settings object.
     *
     * @param settings settings object to load settings from
     */
    public SettingsInfo(GameSettings settings) {
        this.numInitialRobots = settings.getNumInitialRobots();
        this.numAdditionalRobotsPerLevel = settings.getNumAdditionalRobotsPerLevel();
        this.numInitialRubble = settings.getNumInitialRubble();
        this.robotCollisions = settings.getRobotCollisions();
        this.numSafeTeleportsAwarded = settings.getNumSafeTeleportsAwarded();
        this.numRandomTeleportsAwarded = settings.getNumRandomTeleportsAwarded();
        this.numAttacksAwarded = settings.getNumAttacksAwarded();
        this.playerAttacks = settings.getPlayerAttacks();
        this.robotAiMode = settings.getRobotAiMode();
        this.gridWidth = settings.getGridWidth();
        this.gridHeight = settings.getGridHeight();
        this.numPlayersToStartGame = settings.getNumPlayersToStartGame();
    }

    /**
     * Initializes an empty object.
     */
    public SettingsInfo() {
    }
    //endregion
}
