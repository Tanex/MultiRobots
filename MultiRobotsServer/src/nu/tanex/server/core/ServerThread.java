package nu.tanex.server.core;

import nu.tanex.core.aggregates.HighScoreList;
import nu.tanex.core.exceptions.GameException;
import nu.tanex.core.resources.Resources;
import nu.tanex.core.resources.ServerSettings;
import nu.tanex.server.aggregates.GameManagerList;
import nu.tanex.server.exceptions.ServerThreadException;
import nu.tanex.server.gui.data.GameInfo;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-23
 */
public class ServerThread {
    //region Member variables
    private ServerSettings settings;
    private GameManagerList gameManagers;
    private HighScoreList highScores;
    //endregion

    //region Getters

    /**
     * Get the settings that are loaded for the server
     *
     * @return settings that are loaded
     */
    public ServerSettings getSettings() {
        return settings;
    }

    /**
     * Get the HighScoreList that keeps track of all the HighScores on the server.
     *
     * @see HighScoreList
     * @return the HighScoreList
     */
    public HighScoreList getHighScores() {
        return highScores;
    }

    /**
     * List of all the GameManagers that are running on the server.
     *
     * @return all GameManagers
     */
    public GameManagerList getGameManagers() {
        return gameManagers;
    }
    //endregion

    //region Constructors
    /**
     * Initializes the ServerThread loading settings and creating all necessary GameManagers.
     *
     * @param settingsFile filename of the server settings to be loaded, without extension.
     */
    public ServerThread(String settingsFile) {
        settings = new ServerSettings();
        settings.loadSettingsFromFile(settingsFile);
        gameManagers = new GameManagerList();
        for (int i = 0; i < settings.getNumGamesToRun(); i++) {
            gameManagers.add(new GameManager());
        }
        //GameInfo.setPlayersToStart(settings.getNumPlayersToStartGame());
        highScores = new HighScoreList();
        highScores.loadFromFile(Resources.HIGH_SCORE_FILE);
    }

    /**
     * Gets a string containing all the information about all the running games so that the info can be
     * sent out to connected clients.
     *
     * @return string with info on all games.
     */
    public String getGamesInfo() {
        String str = "";
        for (int i = 0; i < gameManagers.size(); i++) {
            //gameId,clientsQueued,clientsNeeded,gameSettings@
            if (gameManagers.get(i).isGameRunning())
                str += i + "," + gameManagers.get(i).getNumPlayersToStartGame() + "," + gameManagers.get(i).getNumPlayersToStartGame() + "," + gameManagers.get(i).getGameSettings() + "@";
            else
                str += i + "," + gameManagers.get(i).getPlayerQueue().size() + "," + gameManagers.get(i).getNumPlayersToStartGame() + "," + gameManagers.get(i).getGameSettings() + "@";
        }
        return str;
    }
    //endregion

    //region Public methods
    /**
     * Queues a player for a game in the appropriate game manager, will start the game if enough players are queued.
     *
     * @param client Client that should be queued.
     * @param gameNum Game Id to queue for
     * @throws ServerThreadException Thrown if an invalid gameNum is passed.
     */
    public void clientQueueForGame(Client client, int gameNum) throws ServerThreadException {
        if (gameNum < 0 || gameNum >= settings.getNumGamesToRun())
            throw new ServerThreadException("gameNum out of range");
        if (gameManagers.get(gameNum).isGameRunning() || gameManagers.get(gameNum).getPlayerQueue().contains(client))
            return; //do not queue while game is running and do not queue multiple times.

        gameManagers.get(gameNum).getPlayerQueue().add(client);
        if (gameManagers.get(gameNum).numPlayersQueued() == gameManagers.get(gameNum).getNumPlayersToStartGame()) {
            try {
                ServerEngine.getInstance().getConnectedClients().removeAll(gameManagers.get(gameNum).getPlayerQueue());
                gameManagers.get(gameNum).startGame();
            } catch (GameException e) {
                throw new ServerThreadException("Game could not be started", e);
            }
        }
    }

    /**
     * Takes a client away from the queue of all GameManagers.
     *
     * @param client client to remove.
     */
    public void clientLeaveGame(Client client) {
        for (GameManager gameManager : gameManagers) {
            gameManager.getPlayerQueue().remove(client);
        }
    }
    //endregion
}
