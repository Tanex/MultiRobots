package nu.tanex.client.gui;

import nu.tanex.client.resources.GuiState;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-05
 */
public interface IClientGuiController {
    void updateGameState(String gameState);
    void setInputDisabled(boolean enabled);
    void changeGuiState(GuiState newState);
    void updateGamesList(String gamesList);
    void updatePlayerInfo(String playerInfo);
    void updatePlayerList(String playerList);
}
