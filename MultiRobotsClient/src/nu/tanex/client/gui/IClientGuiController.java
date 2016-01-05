package nu.tanex.client.gui;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-05
 */
public interface IClientGuiController {
    void updateGameState(String gameState);
    void setInputDisabled(boolean enabled);
}
