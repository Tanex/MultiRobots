package nu.tanex.server.core;

import nu.tanex.core.exceptions.GameException;
import nu.tanex.core.resources.ServerSettings;
import nu.tanex.server.aggregates.ClientList;
import nu.tanex.server.aggregates.GameManagerList;
import nu.tanex.server.exceptions.ServerThreadException;

import java.util.Vector;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-23
 */
public class ServerThread {
    private ServerSettings settings;
    private GameManagerList gameManagers;

    public ServerSettings getSettings() {
        return settings;
    }

    public ServerThread(String settingsFile) {
        settings = new ServerSettings();
        settings.loadSettingsFromFile(settingsFile);
        gameManagers = new GameManagerList();
        for (int i = 0; i < settings.getNumGamesToRun(); i++) {
            gameManagers.add(new GameManager());
        }
    }

    public void clientQueueForGame(Client client, int gameNum) throws ServerThreadException{
        if (gameManagers.get(gameNum).isGameRunning())
            return; //do not queue while game is running.
        if (gameNum < 0 || gameNum >= settings.getNumGamesToRun())
            throw new ServerThreadException("gameNum out of range");
        gameManagers.get(gameNum).getPlayerQueue().add(client);
        if (gameManagers.get(gameNum).getPlayerQueue().size() == settings.getNumPlayersToStartGame()) {
            try {
                ServerEngine.getInstance().getConnectedClients().removeAll(gameManagers.get(gameNum).getPlayerQueue());
                gameManagers.get(gameNum).startGame();
            } catch (GameException e) {
                throw new ServerThreadException("Game could not be started", e);
            }
        }
    }

    public void clientLeaveGame(Client client){
        for (GameManager gameManager : gameManagers) {
            gameManager.getPlayerQueue().remove(client);
        }
    }

    public String getGamesInfo() {
        String str = "";
        for (int i = 0; i < gameManagers.size(); i++) {
            //gameId,clientsQueued,clientsNeeded,gameSettings@
            if (gameManagers.get(i).isGameRunning())
                str += i + "," + settings.getNumPlayersToStartGame() + "," + settings.getNumPlayersToStartGame() + "," + gameManagers.get(i).getGameSettings() + "@";
            else
                str += i + "," + gameManagers.get(i).getPlayerQueue().size() + "," + settings.getNumPlayersToStartGame() + "," + gameManagers.get(i).getGameSettings() + "@";
        }
        return str;
    }
}
