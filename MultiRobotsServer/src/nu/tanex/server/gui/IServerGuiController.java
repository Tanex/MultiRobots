package nu.tanex.server.gui;

import nu.tanex.server.aggregates.GameManagerList;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-19
 */
public interface IServerGuiController {
    void updateGameList(GameManagerList games, int numConnectedClients);
}
