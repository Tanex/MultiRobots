package nu.tanex.server.gui.data;

import nu.tanex.core.data.Player;
import nu.tanex.server.core.Client;
import nu.tanex.server.core.GameManager;

import java.util.ArrayList;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-19
 */
public class GameInfo {
    //region Member variables
    private int playersToStart = 0;
    private SettingsInfo settings;
    private int playersQueued;
    private int gameNum;
    private ArrayList<PlayerInfo> playerList;
    private boolean gameRunning;
    private int currentLevel;
    private int numRobots;
    private int numRubble;
    //endregion

    //region Getters

    /**
     * Gets whether the game is running or not
     *
     * @return is the game running.
     */
    public boolean getGameRunning() {
        return gameRunning;
    }

    /**
     * Gets the number of players needed to start the game.
     *
     * @return amount of players needed
     */
    public int getNumPlayers() {
        return gameRunning ? playersToStart : 0;
    }

    /**
     * Gets the number of players that are queueing for the game.
     *
     * @return amount of players queueing
     */
    public int getNumQueueing() {
        return gameRunning ? 0 : playersQueued;
    }

    /**
     * Gets the id of this game.
     *
     * @return game id
     */
    public int getGameNum() {
        return this.gameNum;
    }
    /**
     * Gets the "fill rate" of the current settings, that is, how full the game board would be with these settings.
     *
     * @return fill rate of these settings.
     */
    private int getFillRate(){
        return gameRunning ? (playersToStart + numRubble + numRobots)/settings.getGridSize() : -1;
    }

    /**
     * Gets a string with detailed information about the game.
     *
     * @return formatted string with game information.
     */
    public String getInfoString(){
        return  "Game #" + (gameNum + 1) + "\n" +
                "Status: " + (gameRunning ? ( "Running..." ) : ( "Waiting for more Players...") ) + "\n" +
                (gameRunning ? "Current level: " + currentLevel + "\n" : "") +
                ((getFillRate() != -1 ) ? "Board fill rate: " + getFillRate() + "\n" : "") ;
    }

    /**
     * Get the SettingsInfo associated with this game.
     *
     * @return this games SettingsInfo
     */
    public SettingsInfo getSettingsInfo() {
        return settings;
    }

    /**
     * Gets the list of all the players playing or queueing for this game.
     *
     * @return list of players.
     */
    public ArrayList<PlayerInfo> getPlayerList() {
        return playerList;
    }
    //endregion

    //region Constructor
    /**
     * Initializes a GameInfo object by reading information from the GameManager.
     *
     * @see GameManager
     * @param gameNum the game id of this game.
     * @param gm the GameManager to read information from
     */
    public GameInfo(int gameNum, GameManager gm) {
        this.gameNum = gameNum;
        settings = new SettingsInfo(gm.getGame().getSettings());
        playersQueued = gm.numPlayersQueued();
        currentLevel = gm.getGame().getLevel();
        gameRunning = gm.isGameRunning();
        numRobots = gameRunning ? gm.getGame().getNumRobots() : -1;
        numRubble = gameRunning ? gm.getGame().getNumRubble() : -1;
        playersToStart = gm.getNumPlayersToStartGame();

        playerList = new ArrayList<>();
        if (gameRunning) {
            for (int i = 0; i < gm.getGame().getNumPlayers(); i++) {
                Client player = gm.getGame().getPlayers().get(i);
                if (!player.isDummy())
                    playerList.add(new PlayerInfo(i, player));
            }
        } else {
            for (int i = 0; i < gm.numPlayersQueued(); i++)
                playerList.add(new PlayerInfo(i, gm.getPlayerQueue().get(i)));
        }
    }
    //endregion

    //region Object overrides
    @Override
    public String toString() {
        return "Game #" + (gameNum + 1) + " : " + (gameRunning ? ("Running...") : (playersQueued + "/" + playersToStart + " players queued"));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameInfo))
            return false;
        return ((GameInfo) obj).gameNum == this.gameNum;
    }
    //endregion
}
