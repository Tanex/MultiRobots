package nu.tanex.engine.core;

import nu.tanex.engine.aggregates.GameGrid;
import nu.tanex.engine.aggregates.PlayerList;
import nu.tanex.engine.aggregates.RobotList;
import nu.tanex.engine.resources.GameSettings;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Game {
    //region Member variables
    private GameSettings settings;
    private PlayerList players;
    private RobotList robots;
    private GameGrid gameGrid;
    private int turn;
    //endregion

    //region Constructors
    public Game() {

    }
    //endregion
}
