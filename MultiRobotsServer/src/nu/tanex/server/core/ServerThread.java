package nu.tanex.server.core;

import nu.tanex.core.exceptions.GameException;
import nu.tanex.server.exceptions.ServerThreadException;
import nu.tanex.core.resources.ServerSettings;
import nu.tanex.server.aggregates.ClientList;

import java.util.Vector;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2015-12-23
 */
public class ServerThread {
    private ServerSettings settings;
    private Vector<GameManager> gameManagers;
    private ClientList clientsQueuedForGame[];

    public ServerSettings getSettings() {
        return settings;
    }

    public ServerThread(String settingsFile) {
        settings = new ServerSettings();
        settings.loadSettingsFromFile(settingsFile);
        gameManagers = new Vector<>();
        clientsQueuedForGame = new ClientList[settings.getNumGamesToRun()];
        for (int i = 0; i < settings.getNumGamesToRun(); i++) {
            gameManagers.add(new GameManager());
            clientsQueuedForGame[i] = new ClientList();
        }
    }

    public void clientQueueForGame(Client client, int gameNum) throws ServerThreadException{
        if (gameNum < 0 || gameNum >= settings.getNumGamesToRun())
            throw new ServerThreadException("gameNum out of range");
        clientsQueuedForGame[gameNum].add(client);
        if (clientsQueuedForGame[gameNum].size() == settings.getNumPlayersToStartGame()) {
            try {
                gameManagers.get(gameNum).startGame(clientsQueuedForGame[gameNum]);
            } catch (GameException e) {
                throw new ServerThreadException("Game could not be started", e);
            }
            ServerEngine.getInstance().getConnectedClients().removeAll(clientsQueuedForGame[gameNum]);
            clientsQueuedForGame[gameNum].clear();
        }
    }
}
