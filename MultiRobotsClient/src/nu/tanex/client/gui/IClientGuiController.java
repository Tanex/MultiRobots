package nu.tanex.client.gui;

import javafx.scene.input.KeyCode;
import nu.tanex.client.gui.data.ConnectScreenInfo;
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
    void keyPressed(KeyCode code);
    void loadConnectScreenInfo(ConnectScreenInfo info);
    void setPlayerNum(int playerNum);
    void setBoardWidth(int width);
    void setBoardHeight(int height);
    void showHighScoreList(String highScoreList);
}
