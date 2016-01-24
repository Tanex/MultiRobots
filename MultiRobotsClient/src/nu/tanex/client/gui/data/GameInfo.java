package nu.tanex.client.gui.data;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-07
 */
public class GameInfo {
    //region Member variables
    private int playersQueued;
    private int playersNeeded;
    private String gameSettings;
    private int gameId;
    //endregion

    //region Getters
    /**
     * Gets a string representing all the information about this game.
     *
     * @return formatted string with all game info.
     */
    public String getGameInfoString() {
        return "Game #" + (gameId + 1) + "\n" +
                "Players queued: " + playersQueued + "/" + playersNeeded + "\n" +
                "Settings:\n" + gameSettings;
    }

    /**
     * Gets the games unique id for this game.
     *
     * @return the games unique ID.
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Gets the amount of players that are queued for this game.
     *
     * @return amount of players queueing
     */
    public int getPlayersQueued() {
        return playersQueued;
    }

    /**
     * Gets the amount of players that are needed in order to start the game.
     *
     * @return amount of players needed.
     */
    public int getPlayersNeeded() {
        return playersNeeded;
    }
    //endregion

    //region Constructors
    /**
     * Initializes the object taking all the information out of the provided string.
     *
     * @param gameInfo string with all the game info.
     */
    public GameInfo(String gameInfo) {
        //gameId,clientsQueued,clientsNeeded,gameSettings@
        String str[] = gameInfo.split(",");
        gameId = Integer.parseInt(str[0]);
        playersQueued = Integer.parseInt(str[1]);
        playersNeeded = Integer.parseInt(str[2]);
        gameSettings = str[3].replace(">", "\n");
    }
    //endregion

    //region Public methods
    public void updateInfo(String gameInfo) {
        //gameId,clientsQueued,clientsNeeded,gameSettings@
        String str[] = gameInfo.split(",");
        playersQueued = Integer.parseInt(str[1]);
        playersNeeded = Integer.parseInt(str[2]);
        gameSettings = str[3].replace(">", "\n");
    }
    //endregion

    //region Object overrides
    @Override
    public String toString() {
        return "Game #" + (gameId + 1) + ". " + playersQueued + "/" + playersNeeded + " players.";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameInfo))
            return false;
        return ((GameInfo) obj).gameId == this.gameId;
    }
    //endregion
}
