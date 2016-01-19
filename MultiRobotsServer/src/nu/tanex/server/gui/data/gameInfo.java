package nu.tanex.server.gui.data;

import nu.tanex.core.resources.GameSettings;
import nu.tanex.server.core.Game;

/**
 * @author Victor Hedlund
 * @version 0.1
 * @since 2016-01-19
 */
public class GameInfo {
    private GameSettings settings;

    public GameInfo(Game game) {
        settings = game.getSettings();

    }

    @Override
    public String toString() {
        return super.toString();
    }
}
