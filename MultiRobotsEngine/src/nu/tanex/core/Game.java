package nu.tanex.core;

import nu.tanex.aggregates.GameGrid;
import nu.tanex.aggregates.PlayerList;
import nu.tanex.aggregates.RobotList;
import nu.tanex.resources.GameSettings;

/**
 * @author      Victor Hedlund
 * @version     0.1
 * @since       2015-11-26
 */
public class Game {
    private GameSettings settings;
    private PlayerList players;
    private RobotList robots;
    private GameGrid gameGrid;
    private int turn;

    public Game(){

    }
}
